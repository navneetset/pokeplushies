package tech.sethi.pebbles.pokeplushie

import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Pokemon
import com.google.gson.JsonObject

class ResetEV() {
//    fun resetPokemonEVs(pokemon: Pokemon) {
//        val pokemonJson = JsonObject()
//        pokemon.saveToJSON(pokemonJson)
//
//        // Get the EVs object from the JSON, creating a new object if it doesn't exist.
//        val evsJson = pokemonJson.getAsJsonObject("EVs") ?: JsonObject()
//
//        // Set all the EVs to zero.
//        for (stat in Stats.PERMANENT) {
//            evsJson.addProperty(stat.identifier.toString(), 0)
//        }
//
//        // Replace the old EVs object with the new one.
//        pokemonJson.add("EVs", evsJson)
//
//        // Load the modified JSON back into the Pokemon.
//        pokemon.loadFromJSON(pokemonJson)
//    }
}
