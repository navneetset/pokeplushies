package tech.sethi.pebbles.cobbledstats

import com.cobblemon.mod.common.api.events.CobblemonEvents
import dev.architectury.event.events.common.CommandRegistrationEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.sethi.pebbles.cobbledstats.stats.ManageStats
import java.util.UUID
import java.util.concurrent.Executors

object CobbledStats {
    const val MOD_ID = "cobbledstats"
    val LOGGER: Logger = LoggerFactory.getLogger(CobbledStats::class.java)

    var databaseType = "mysql"

    val playerStatsSet = HashSet<PlayerStats>()

//    val databaseConnection = when (databaseType) {
//        "mongodb" -> MongoDBDatabaseHandler()
//        "mysql" -> MySQLDatabaseHandler()
//        else -> throw IllegalArgumentException("Invalid database type")
//    }

    val scheduledExecutorService = Executors.newScheduledThreadPool(1)

    val statsManager = ManageStats()

    @JvmStatic
    fun init() {
        LOGGER.info("Pebble's Cobbled Stats Initialized!")

        CommandRegistrationEvent.EVENT.register { dispatcher, _, _ ->
            StatsCommands.register(dispatcher)
        }

        when (databaseType) {
            "mongodb" -> {
                LOGGER.info("Using MongoDB database")
            }

            "mysql" -> {
                LOGGER.info("Using MySQL database")
            }

            else -> {
                LOGGER.error("Invalid database type")
            }
        }

        CobblemonEvents.POKEMON_CAPTURED.subscribe { event ->
            val playerUuid = event.player.uuid
            val pokemonPrimaryType = event.pokemon.primaryType.name
            val shiny = event.pokemon.shiny

            val cobblemonStats = statsManager.getCobblemonStats(playerUuid)
                ?: CobblemonPlayerStats(
                    playerUuid,
                    0,
                    0,
                    0,
                    initializePokemonTypeMap()
                )


            cobblemonStats.pokemonCaught++
            if (shiny) {
                cobblemonStats.shinyPokemonCaught++
            }
            cobblemonStats.caughtPokemon[pokemonPrimaryType] = cobblemonStats.caughtPokemon[pokemonPrimaryType]!! + 1

            statsManager.saveCobblemonStats(playerUuid, cobblemonStats)
        }
    }

    fun initializePokemonTypeMap(): MutableMap<String, Int> {
        val pokemonTypes = listOf(
            "normal", "fire", "water", "grass", "electric", "ice", "fighting",
            "poison", "ground", "flying", "psychic", "bug", "rock", "ghost",
            "dragon", "dark", "steel", "fairy"
        )

        val typeMap = mutableMapOf<String, Int>()
        pokemonTypes.forEach { type ->
            typeMap[type] = 0
        }
        return typeMap
    }


    data class PlayerStats(
        val uuid: UUID,
        var name: String,
        val minecraftStats: MinecraftPlayerStats,
        val cobblemonStats: CobblemonPlayerStats
    )

    data class MinecraftPlayerStats(
        val playerUUID: UUID,
        var playTime: Long
    )

    data class CobblemonPlayerStats(
        val playerUUID: UUID,
        var totalPokemonCaught: Int = 0,
        var totalShinyPokemonCaught: Int = 0,
        var totalPokemonDefeated: Int = 0,
        var defeatedPokemonByType: MutableMap<String, Int> = initializePokemonTypeMap()
    )


    data class CaughtPokemon(
        val playerUUID: UUID,
        val species: String,
        val pokeball: String,
        val primaryType: String,
        val secondaryType: String,
        val ability: String,
        val nature: String,
        val gender: String,
        val level: Int,
        val shiny: Boolean
    )

    data class DefeatedPokemon(
        val playerUUID: UUID,
        val species: String,
        val primaryType: String,
        val secondaryType: String,
        val ability: String,
        val nature: String,
        val gender: String,
        val level: Int,
        val shiny: Boolean
    )

}