package tech.sethi.pebbles.cobbledstats.datahandler

import com.google.gson.GsonBuilder
import java.io.File

object MySQLConfig {
    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    val mySQLConfigFile = File("config/pebbles-cobbledstats/db/mysql.json")

    var config: MySQLConfig

    init {
        if (!mySQLConfigFile.exists()) {
            mySQLConfigFile.parentFile.mkdirs()
            config = MySQLConfig(
                "jdbc:mysql://root@localhost/pebbles_cobbledstats"
            )
            mySQLConfigFile.writeText(gson.toJson(config))
        } else {
            val configJson = mySQLConfigFile.readText()
            config = gson.fromJson(configJson, MySQLConfig::class.java)
        }
    }

    data class MySQLConfig(
        val connectionString: String = "jdbc:mysql://root@localhost/pebbles_cobbledstats"
    )
}
