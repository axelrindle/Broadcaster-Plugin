package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.BroadcastingThread
import de.axelrindle.broadcaster.Formatter
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.io.IOException

class ReloadCommand(
        plugin: Broadcaster,
        parent: BrcCommand
) : SubCommand(plugin, parent, "reload", "broadcaster.reload") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        // stop broadcasting first
        if (BroadcastingThread.running) {
            BroadcastingThread.stop()
            sender.sendMessage(
                    Formatter.formatColors(plugin.config!!.getString("Messages.ReloadStopped"))
            )
        }

        // reload configurations
        try {
            plugin.reloadConfig()
            plugin.loadMessages()
        } catch (e: IOException) {
            sender.sendMessage("An error occurred! Check the console! Disabling now...")
            e.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(plugin)
            return
        }
        sender.sendMessage(Formatter.formatColors("&aSuccessfully reloaded."))
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessage("§9/brc reload §f- §3Reload the plugin! (Stop's the Broadcast!)")
    }
}