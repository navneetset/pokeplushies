package tech.sethi.pebbles.cobbledstats.stats

import tech.sethi.pebbles.cobbledstats.CobbledStats.MinecraftPlayerStats
import tech.sethi.pebbles.cobbledstats.CobbledStats.CobblemonPlayerStats
import tech.sethi.pebbles.cobbledstats.datahandler.DatabaseHandlerSingleton
import java.util.UUID

class ManageStats {
    private val handler = DatabaseHandlerSingleton.handler

    @Synchronized
    fun getCobblemonStats(uuid: UUID): CobblemonPlayerStats? {
        return handler.getCobblemonStats(uuid)
    }

    @Synchronized
    fun saveCobblemonStats(uuid: UUID, cobblemonStats: CobblemonPlayerStats) {
        handler.saveCobblemonStats(uuid, cobblemonStats)
    }

    @Synchronized
    fun getMinecraftPlayerStats(uuid: UUID): MinecraftPlayerStats? {
        return handler.getMinecraftPlayerStats(uuid)
    }

    @Synchronized
    fun saveMinecraftPlayerStats(uuid: UUID, minecraftStats: MinecraftPlayerStats) {
        handler.saveMinecraftPlayerStats(uuid, minecraftStats)
    }

    @Synchronized
    fun getAllCobblemonStats(): List<CobblemonPlayerStats> {
        return handler.getAllCobblemonStats()
    }

    @Synchronized
    fun getAllMinecraftPlayerStats(): List<MinecraftPlayerStats> {
        return handler.getAllMinecraftPlayerStats()
    }

    @Synchronized
    fun saveAllCobblemonStats(cobblemonStats: List<CobblemonPlayerStats>) {
        handler.saveAllCobblemonStats(cobblemonStats)
    }

    @Synchronized
    fun saveAllMinecraftPlayerStats(minecraftStats: List<MinecraftPlayerStats>) {
        handler.saveAllMinecraftPlayerStats(minecraftStats)
    }
}
