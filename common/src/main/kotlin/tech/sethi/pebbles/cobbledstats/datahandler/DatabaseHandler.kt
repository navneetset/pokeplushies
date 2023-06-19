package tech.sethi.pebbles.cobbledstats.datahandler

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import org.bson.Document
import tech.sethi.pebbles.cobbledstats.CobbledStats
import tech.sethi.pebbles.cobbledstats.CobbledStats.MinecraftPlayerStats
import tech.sethi.pebbles.cobbledstats.CobbledStats.CobblemonPlayerStats
import java.sql.Connection
import java.sql.DriverManager
import java.util.*


interface DatabaseHandler {
    fun getAllCobblemonStats(): List<CobblemonPlayerStats>
    fun getAllMinecraftPlayerStats(): List<MinecraftPlayerStats>
    fun getCobblemonStats(uuid: UUID): CobblemonPlayerStats?
    fun getMinecraftPlayerStats(uuid: UUID): MinecraftPlayerStats?
    fun saveAllCobblemonStats(cobblemonStats: List<CobblemonPlayerStats>)
    fun saveAllMinecraftPlayerStats(minecraftStats: List<MinecraftPlayerStats>)
    fun saveCobblemonStats(uuid: UUID, cobblemonStats: CobblemonPlayerStats)
    fun saveMinecraftPlayerStats(uuid: UUID, minecraftStats: MinecraftPlayerStats)
}

class MongoDBDatabaseHandler : DatabaseHandler {
    private val mongo = MongoDBConfig.config

    private val mongoClientURI: MongoClientURI = MongoClientURI(mongo.connectionString)
    private val mongoClient: MongoClient = MongoClient(mongoClientURI)
    private val database: MongoDatabase = mongoClient.getDatabase(mongoClientURI.database ?: "pebbles_cobbledstats")

    private val cobblemonStatsCollection = database.getCollection("cobblemonStats")
    private val minecraftStatsCollection = database.getCollection("minecraftStats")
    private val caughtPokemonCollection = database.getCollection("caughtPokemon")

    private val gson = Gson()

    init {
        ensureDatabaseSetup()
    }

    private fun ensureDatabaseSetup() {
        val dummyDoc = Document("_id", "dummy")

        cobblemonStatsCollection.insertOne(dummyDoc)
        cobblemonStatsCollection.deleteOne(dummyDoc)

        minecraftStatsCollection.insertOne(dummyDoc)
        minecraftStatsCollection.deleteOne(dummyDoc)

        caughtPokemonCollection.insertOne(dummyDoc)
        caughtPokemonCollection.deleteOne(dummyDoc)

    }

    override fun getCobblemonStats(uuid: UUID): CobblemonPlayerStats? {
        val statsDoc = cobblemonStatsCollection.find(eq("playerUUID", uuid.toString())).firstOrNull() ?: return null

        val stats = gson.fromJson(statsDoc.toJson(), CobblemonPlayerStats::class.java)
        val caughtPokemonDocs = caughtPokemonCollection.find(eq("playerUUID", uuid.toString())).toList()
        stats = caughtPokemonDocs.map { gson.fromJson(it.toJson(), CobbledStats.CaughtPokemon::class.java) }

        return stats
    }
    override fun saveCobblemonStats(uuid: UUID, cobblemonStats: CobblemonPlayerStats) {
        val doc = Document.parse(gson.toJson(cobblemonStats))
        doc.remove("caughtPokemon")
        cobblemonStatsCollection.replaceOne(eq("playerUUID", uuid.toString()), doc, ReplaceOptions().upsert(true))
    }


    override fun getMinecraftPlayerStats(uuid: UUID): MinecraftPlayerStats? {
        val doc = minecraftStatsCollection.find(eq("playerUUID", uuid.toString())).firstOrNull() ?: return null
        return gson.fromJson(doc.toJson(), MinecraftPlayerStats::class.java)
    }

    override fun saveMinecraftPlayerStats(uuid: UUID, minecraftStats: MinecraftPlayerStats) {
        val doc = Document.parse(gson.toJson(minecraftStats))
        minecraftStatsCollection.replaceOne(eq("playerUUID", uuid.toString()), doc, ReplaceOptions().upsert(true))
    }

    override fun getAllCobblemonStats(): List<CobblemonPlayerStats> {
        val docs = cobblemonStatsCollection.find().toList()
        return docs.map { gson.fromJson(it.toJson(), CobblemonPlayerStats::class.java) }
    }

    override fun getAllMinecraftPlayerStats(): List<MinecraftPlayerStats> {
        val docs = minecraftStatsCollection.find().toList()
        return docs.map { gson.fromJson(it.toJson(), MinecraftPlayerStats::class.java) }
    }


    override fun saveAllCobblemonStats(cobblemonStats: List<CobblemonPlayerStats>) {
        cobblemonStats.forEach { cobblemonStats ->
            val doc = Document.parse(gson.toJson(cobblemonStats))
            cobblemonStatsCollection.replaceOne(
                eq("playerUUID", cobblemonStats.playerUUID),
                doc,
                ReplaceOptions().upsert(true)
            )
        }
    }

    override fun saveAllMinecraftPlayerStats(minecraftStats: List<MinecraftPlayerStats>) {
        minecraftStats.forEach { minecraftStats ->
            val doc = Document.parse(gson.toJson(minecraftStats))
            minecraftStatsCollection.replaceOne(
                eq("playerUUID", minecraftStats.playerUUID),
                doc,
                ReplaceOptions().upsert(true)
            )
        }
    }


}

class MySQLDatabaseHandler : DatabaseHandler {
    private val config = MySQLConfig.config
    private val connection: Connection

    private val gson = Gson()

    init {
        val url = config.connectionString
        connection = DriverManager.getConnection(url)

        val createCobblemonTable = connection.prepareStatement(
            """
CREATE TABLE IF NOT EXISTS CobblemonPlayerStats (
    playerUUID VARCHAR(36) PRIMARY KEY,
    pokemonCaught INT,
    shinyPokemonCaught INT,
    caughtPokemon TEXT,
    caughtPokemonByPrimaryType TEXT,
    wildPokemonDefeated INT,
    wildPokemonDefeatedByPrimaryType TEXT
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)

"""
        )
        createCobblemonTable.execute()

        val createMinecraftTable = connection.prepareStatement(
            """
CREATE TABLE IF NOT EXISTS MinecraftPlayerStats (
    playerUUID VARCHAR(36) PRIMARY KEY,
    playTime BIGINT
)
"""
        )
        createMinecraftTable.execute()

        val createCaughtPokemonTable = connection.prepareStatement(
            """
CREATE TABLE IF NOT EXISTS CaughtPokemon (
// incremental id primary key
    id INT AUTO_INCREMENT
    playerUUID VARCHAR(36),
    species VARCHAR(255),
    pokeball VARCHAR(255),
    gender VARCHAR(255),
    level INT,
    shiny BOOLEAN,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    PRIMARY KEY (id),
    FOREIGN KEY(playerUUID) REFERENCES CobblemonPlayerStats(playerUUID)
)
"""
        )
        createCaughtPokemonTable.execute()
    }

    override fun getCobblemonStats(uuid: UUID): CobblemonPlayerStats? {
        val statement = connection.prepareStatement("SELECT * FROM CobblemonPlayerStats WHERE playerUUID = ?")
        statement.setString(1, uuid.toString())
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            CobblemonPlayerStats(
                playerUUID = UUID.fromString(resultSet.getString("playerUUID")),
                pokemonCaught = resultSet.getInt("pokemonCaught"),
                shinyPokemonCaught = resultSet.getInt("shinyPokemonCaught"),
                caughtPokemon = gson.fromJson(
                    resultSet.getString("caughtPokemon"),
                    object : TypeToken<Map<String, Int>>() {}.type
                ),
                caughtPokemonByPrimaryType = gson.fromJson(
                    resultSet.getString("caughtPokemonByPrimaryType"),
                    object : TypeToken<Map<String, Int>>() {}.type
                ),
                wildPokemonDefeated = resultSet.getInt("wildPokemonDefeated"),
                wildPokemonDefeatedByPrimaryType = gson.fromJson(
                    resultSet.getString("wildPokemonDefeatedByPrimaryType"),
                    object : TypeToken<Map<String, Int>>() {}.type
                )
            )
        } else null
    }

    override fun saveCobblemonStats(uuid: UUID, cobblemonStats: CobblemonPlayerStats) {
        val statement =
            connection.prepareStatement("REPLACE INTO CobblemonPlayerStats (playerUUID, pokemonCaught, shinyPokemonCaught, caughtPokemon, caughtPokemonByPrimaryType, wildPokemonDefeated, wildPokemonDefeatedByPrimaryType) VALUES (?, ?, ?, ?, ?, ?, ?)")
        statement.setString(1, uuid.toString())
        statement.setInt(2, cobblemonStats.pokemonCaught)
        statement.setInt(3, cobblemonStats.shinyPokemonCaught)
        statement.setString(4, gson.toJson(cobblemonStats.caughtPokemon))
        statement.setString(5, gson.toJson(cobblemonStats.caughtPokemonByPrimaryType))
        statement.setInt(6, cobblemonStats.wildPokemonDefeated)
        statement.setString(7, gson.toJson(cobblemonStats.wildPokemonDefeatedByPrimaryType))
        statement.execute()
    }


    override fun getMinecraftPlayerStats(uuid: UUID): MinecraftPlayerStats? {
        val statement = connection.prepareStatement("SELECT * FROM MinecraftPlayerStats WHERE playerUUID = ?")
        statement.setString(1, uuid.toString())
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            MinecraftPlayerStats(
                playerUUID = UUID.fromString(resultSet.getString("playerUUID")),
                playTime = resultSet.getLong("playTime")
            )
        } else null
    }

    override fun saveMinecraftPlayerStats(uuid: UUID, minecraftStats: MinecraftPlayerStats) {
        val statement =
            connection.prepareStatement("REPLACE INTO MinecraftPlayerStats (playerUUID, playTime) VALUES (?, ?)")
        statement.setString(1, uuid.toString())
        statement.setLong(2, minecraftStats.playTime)
        statement.execute()
    }


    override fun getAllCobblemonStats(): List<CobblemonPlayerStats> {
        val statement = connection.prepareStatement("SELECT * FROM CobblemonPlayerStats")
        val resultSet = statement.executeQuery()
        val statsList = mutableListOf<CobblemonPlayerStats>()
        while (resultSet.next()) {
            statsList.add(gson.fromJson(resultSet.getString("data"), CobblemonPlayerStats::class.java))
        }
        return statsList
    }

    override fun getAllMinecraftPlayerStats(): List<MinecraftPlayerStats> {
        val statement = connection.prepareStatement("SELECT * FROM MinecraftPlayerStats")
        val resultSet = statement.executeQuery()
        val statsList = mutableListOf<MinecraftPlayerStats>()
        while (resultSet.next()) {
            statsList.add(gson.fromJson(resultSet.getString("data"), MinecraftPlayerStats::class.java))
        }
        return statsList
    }

    override fun saveAllCobblemonStats(cobblemonStats: List<CobblemonPlayerStats>) {
        val statement =
            connection.prepareStatement("REPLACE INTO CobblemonPlayerStats (playerUUID, data) VALUES (?, ?)")
        cobblemonStats.forEach { stats ->
            statement.setString(1, stats.playerUUID.toString())
            statement.setString(2, gson.toJson(stats))
            statement.addBatch()
        }
        statement.executeBatch()
    }

    override fun saveAllMinecraftPlayerStats(minecraftStats: List<MinecraftPlayerStats>) {
        val statement =
            connection.prepareStatement("REPLACE INTO MinecraftPlayerStats (playerUUID, data) VALUES (?, ?)")
        minecraftStats.forEach { stats ->
            statement.setString(1, stats.playerUUID.toString())
            statement.setString(2, gson.toJson(stats))
            statement.addBatch()
        }
        statement.executeBatch()
    }


}
