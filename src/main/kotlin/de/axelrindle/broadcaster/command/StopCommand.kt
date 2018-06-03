package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.BroadcastingThread
import de.axelrindle.broadcaster.Formatter
import org.bukkit.command.CommandSender

class StopCommand(
        plugin: Broadcaster,
        parent: BrcCommand
) : SubCommand(plugin, parent, "start", "broadcaster.start") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (BroadcastingThread.running) {
            BroadcastingThread.stop()
            sender.sendMessage(
                    Formatter.formatColors(plugin.config!!.getString("Messages.Stopped"))
            )
        } else {
            sender.sendMessage(
                    Formatter.formatColors(plugin.config!!.getString("Messages.AlreadyStopped"))
            )
        }
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessage("§9/brc stop §f- §3Stop the Broadcast")
    }
}