package tech.sethi.pebbles.cobbledstats.fabric;

import net.fabricmc.api.ModInitializer;
import tech.sethi.pebbles.cobbledstats.CobbledStats;

public class CobbledStatsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobbledStats.init();
    }
}