package tech.sethi.pebbles.cobbledstats

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity

class BuyPlushie {
    fun buyPlushie(playerEntity: ServerPlayerEntity, slot: Int): Boolean {
        val playerParty = Cobblemon.storage.getParty(playerEntity)
        val pokemon = playerParty.get(slot - 1)
        if (pokemon == null) {
            playerEntity.sendMessage(
                PM.returnStyledText("<red>There is no Pokemon in that slot!</red>"),
                false
            )
            return false
        }

        val plushie = getPlushie(pokemon)
        playerEntity.inventory.offerOrDrop(plushie)
        return true
    }

    private fun getPlushie(pokemon: Pokemon): ItemStack {
        val pokemonItem = PokemonItem.from(pokemon)
        pokemonItem.setCustomName(PM.returnStyledText("<aqua>${pokemon.species.name} Plushie</aqua>"))
        return pokemonItem
    }
}