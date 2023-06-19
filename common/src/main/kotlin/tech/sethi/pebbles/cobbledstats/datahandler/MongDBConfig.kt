package tech.sethi.pebbles.cobbledstats.datahandler

import com.google.gson.GsonBuilder
import java.io.File

object MongoDBConfig {

    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    val mongoConfigFile = File("config/pebbles-cobbledstats/db/mongodb.json")

    init {
        if (!mongoConfigFile.exists()) {
            mongoConfigFile.parentFile.mkdirs()
            mongoConfigFile.writeText(
                gson.toJson(
                    MongoDBConfig(
                        "mongodb://localhost:27017/pebbles-cobbledstats"
                    )
                )
            )
        }
    }

    val config: MongoDBConfig by lazy {
        gson.fromJson(mongoConfigFile.readText(), MongoDBConfig::class.java)
    }

    data class MongoDBConfig(
        val connectionString: String
    )
}
