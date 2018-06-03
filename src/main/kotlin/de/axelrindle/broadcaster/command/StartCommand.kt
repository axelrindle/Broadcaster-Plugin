package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.BroadcastingThread
import de.axelrindle.broadcaster.Formatter
import org.bukkit.command.CommandSender

/**
 * A [SubCommand] which starts the broadcasting thread.
 *
 * @see BroadcastingThread
 */
class StartCommand(
        plugin: Broadcaster,
        parent: BrcCommand
) : SubCommand(plugin, parent, "start", "broadcaster.start") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (!BroadcastingThread.running) {
            BroadcastingThread.start(plugin, plugin.messages!!, plugin.config!!.getInt("Cast.Interval"))
            sender.sendMessage(
                    Formatter.formatColors(plugin.config!!.getString("Messages.Started"))
            )
        } else {
            sender.sendMessage(
                    Formatter.formatColors(plugin.config!!.getString("Messages.AlreadyRunning"))
            )
        }
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessage("§9/brc start §f- §3Start the Broadcast")
    }
}