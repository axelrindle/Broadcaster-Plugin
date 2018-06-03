package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.BroadcastingThread
import de.axelrindle.broadcaster.Formatter
import org.bukkit.command.CommandSender

/**
 * A [SubCommand] which stops the broadcasting thread.
 *
 * @see BroadcastingThread
 */
class StopCommand(
        plugin: Broadcaster,
        parent: BrcCommand
) : SubCommand(plugin, parent, "start", "broadcaster.start") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (BroadcastingThread.running) {
            BroadcastingThread.stop()
            sender.sendMessage(
                    Formatter.formatColors(plugin.configuration.getString("Messages.Stopped"))
            )
        } else {
            sender.sendMessage(
                    Formatter.formatColors(plugin.configuration.getString("Messages.AlreadyStopped"))
            )
        }
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessage("§9/brc stop §f- §3Stop the Broadcast")
    }
}