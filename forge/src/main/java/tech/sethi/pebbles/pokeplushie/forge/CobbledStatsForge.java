package tech.sethi.pebbles.pokeplushie.forge;

import dev.architectury.platform.forge.EventBuses;
import tech.sethi.pebbles.cobbledstats.CobbledStats;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CobbledStats.MOD_ID)
public class CobbledStatsForge {
    public CobbledStatsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(CobbledStats.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CobbledStats.init();
    }
}