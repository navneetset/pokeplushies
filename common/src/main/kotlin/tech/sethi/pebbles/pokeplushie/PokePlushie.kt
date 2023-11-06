package tech.sethi.pebbles.pokeplushie

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import dev.architectury.event.EventResult
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.InteractionEvent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import org.apache.logging.log4j.LogManager
import tech.sethi.pebbleseconomy.PebblesEconomyInitializer

object PokePlushie {
    const val MOD_ID = "pokeplushie"
    val LOGGER = LogManager.getLogger()
//    val economy = PebblesEconomyInitializer.economy


    @JvmStatic
    fun init() {
        LOGGER.info("Pebble's PokePlushie Initialized!")

        CommandRegistrationEvent.EVENT.register { dispatcher, _, _ ->
            PokePlushiesCommand.register(dispatcher)
        }
    }
}