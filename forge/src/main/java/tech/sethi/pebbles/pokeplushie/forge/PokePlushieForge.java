package tech.sethi.pebbles.pokeplushie.forge;

import dev.architectury.platform.forge.EventBuses;
import tech.sethi.pebbles.pokeplushie.PokePlushie;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PokePlushie.MOD_ID)
public class PokePlushieForge {
    public PokePlushieForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(PokePlushie.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        PokePlushie.init();
    }
}