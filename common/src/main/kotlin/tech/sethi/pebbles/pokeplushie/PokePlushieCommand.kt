package tech.sethi.pebbles.pokeplushie

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

object PokePlushiesCommand {

    val slotSuggestions: List<Int> = listOf(1, 2, 3, 4, 5, 6)

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val pokeTransferCommand = literal("pokeplushie")
            .then(argument("slotNumber", IntegerArgumentType.integer())
                .suggests { context, builder ->
                    CommandSource.suggestMatching(
                        slotSuggestions.map { it.toString() },
                        builder
                    )
                }
                .executes { ctx ->

                    val slotNumber = IntegerArgumentType.getInteger(ctx, "slotNumber")

                    val success = BuyPlushie().buyPlushie(ctx.source.player!!, slotNumber)

                    if (success) {
                        ctx.source.sendFeedback(
                            PM.returnStyledText("<aqua>You got a plushie!</aqua>"),
                            true
                        )
                        1
                    } else {
                        PM.returnStyledText("<red>Failed to get a plushie!</red>")
                        0
                    }

                }
            )

        dispatcher.register(pokeTransferCommand)
    }
}