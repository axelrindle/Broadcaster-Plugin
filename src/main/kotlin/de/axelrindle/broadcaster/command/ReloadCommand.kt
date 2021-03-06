package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.BroadcastingThread
import de.axelrindle.broadcaster.plugin
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.io.IOException

/**
 * Reloads the plugin configuration from disk.
 */
class ReloadCommand : PocketCommand() {

    override fun getName(): String {
        return "reload"
    }

    override fun getDescription(): String {
        return plugin.localization.localize("CommandHelp.Reload")!!
    }

    override fun getUsage(): String {
        return "/brc reload"
    }

    override fun getPermission(): String {
        return "broadcaster.reload"
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        // stop broadcasting first
        if (BroadcastingThread.running) {
            BroadcastingThread.stop()
            sender.sendMessageF(plugin.localization.localize("ReloadStopped")!!)
        }

        // reload configurations
        try {
            plugin.config.reloadAll()
            BroadcastingThread.loadMessages()
            sender.sendMessageF("&aSuccessfully reloaded.")
        } catch (e: IOException) {
            sender.sendMessageF("&cAn error occurred! Check the console! Disabling now...")
            e.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(plugin)
        }
        return true
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessageF("&9${getUsage()} &f- &3${getDescription()}")
    }
}