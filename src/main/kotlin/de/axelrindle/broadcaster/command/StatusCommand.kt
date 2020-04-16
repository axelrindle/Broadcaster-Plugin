package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.BroadcastingThread
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * Display some status information about the plugin, e.g. thread status and version.
 */
class StatusCommand(
        private val plugin: Broadcaster
) : PocketCommand() {

    override fun getName(): String {
        return "status"
    }

    override fun getDescription(): String {
        return plugin.localization.localize("CommandHelp.Status")!!
    }

    override fun getPermission(): String {
        return "broadcaster.status"
    }

    override fun getUsage(): String {
        return "/brc status"
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        val helpText = plugin.localization.localize("Words.Status")!!
        sender.sendMessageF(Broadcaster.CHAT_PREFIX + helpText)

        sender.sendMessageF("Running: §u" + BroadcastingThread.running)
        sender.sendMessageF("Paused: §u" + BroadcastingThread.paused)

        val amountMessages = plugin.config.access("messages")!!.getStringList("Messages").size
        sender.sendMessageF("Total messages loaded: §u$amountMessages")

        val description = plugin.description
        sender.sendMessageF("Version: " + description.version)
        sender.sendMessageF("API Version: " + description.apiVersion)

        return true
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessageF("&9${getUsage()} &f- &3${getDescription()}")
    }

}