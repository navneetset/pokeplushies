package tech.sethi.pebbles.cobbledstats.datahandler

import tech.sethi.pebbles.cobbledstats.CobbledStats

object DatabaseHandlerSingleton {
    val handler: DatabaseHandler by lazy {
        when (CobbledStats.databaseType) {
            "mongodb" -> MongoDBDatabaseHandler()
            "mysql" -> MySQLDatabaseHandler()
            else -> throw IllegalArgumentException("Invalid database type")
        }
    }
}
