package tech.sethi.pebbles.pokeplushie.fabric;

import net.fabricmc.api.ModInitializer;

public class PokePlushieFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        tech.sethi.pebbles.pokeplushie.PokePlushie.init();
    }
}