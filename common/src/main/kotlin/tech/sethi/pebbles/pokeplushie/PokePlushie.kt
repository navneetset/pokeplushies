package tech.sethi.pebbles.pokeplushie

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import dev.architectury.event.EventResult
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.InteractionEvent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import org.apache.logging.log4j.LogManager

object PokePlushie {
    const val MOD_ID = "pokeplushie"
    val LOGGER = LogManager.getLogger()


    @JvmStatic
    fun init() {
        LOGGER.info("Pebble's PokePlushie Initialized!")

        CommandRegistrationEvent.EVENT.register { dispatcher, _, _ ->
            PokePlushiesCommand.register(dispatcher)

//            val resetEVItem = ItemStack(Items.FEATHER, 1)
//            resetEVItem.setCustomName(PM.returnStyledText("<yellow>Reset All EVs"))
//            resetEVItem.getOrCreateSubNbt("resetEV").putString("stat", "all")

//            InteractionEvent.INTERACT_ENTITY.register { player, entity, hand ->
//                player.giveItemStack(resetEVItem.copy())
//                if (player.mainHandStack.getOrCreateSubNbt("resetEV").getString("stat") == "all") {
//                    if (entity is PokemonEntity && entity.pokemon.getOwnerPlayer() == player) {
//                        player.mainHandStack.decrement(1)
//                        val resetEV = ResetEV()
//                        resetEV.resetPokemonEVs(entity.pokemon)
//                        player.sendMessage(PM.returnStyledText("<green>Reset EVs of ${entity.name.string}.</green>"))
//                        EventResult.pass()
//                    } else {
//                        EventResult.pass()
//                    }
//                } else {
//                    EventResult.pass()
//                }
//            }
        }
    }
}