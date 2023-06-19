package tech.sethi.pebbles.cobbledstats

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import tech.sethi.pebbles.cobbledstats.CobbledStats.playerStatsSet

object StatsCommands {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val cobbledStatsCommand = literal("cobbledstats")

        val topCommand = literal("top")
            .then(literal("playtime")
                .executes { context -> executeTopPlaytime(context) }
            )
            .then(literal("pokemoncaught")
                .executes { context -> executeTopPokemonCaught(context) }
            )


        dispatcher.register(
            cobbledStatsCommand
                .then(topCommand)
        )
    }

    fun executeTopPlaytime(context: CommandContext<ServerCommandSource>): Int {
        val source = context.source

        val sortedStats = playerStatsSet.sortedBy { it.minecraftStats.playTime }

        sortedStats.forEach { playerStats ->
            source.sendFeedback(
                PM.returnStyledText("Player: ${playerStats.name}, Playtime: ${playerStats.minecraftStats.playTime}"),
                false
            )
        }

        return 1
    }

    fun executeTopPokemonCaught(context: CommandContext<ServerCommandSource>): Int {
        val source = context.source

        val sortedStats = playerStatsSet.sortedBy { it.cobblemonStats.pokemonCaught }

        sortedStats.forEach { playerStats ->
            source.sendFeedback(
                PM.returnStyledText("Player: ${playerStats.name}, Pokemon Caught: ${playerStats.cobblemonStats.pokemonCaught}"),
                false
            )
        }

        return 1
    }
}
