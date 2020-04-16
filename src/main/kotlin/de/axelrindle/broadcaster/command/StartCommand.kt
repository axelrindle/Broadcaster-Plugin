package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.BroadcastingThread
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * Starts the broadcasting thread.
 *
 * @see BroadcastingThread
 */
class StartCommand(
        private val plugin: Broadcaster
) : PocketCommand() {

    override fun getName(): String {
        return "start"
    }

    override fun getDescription(): String {
        return plugin.localization.localize("CommandHelp.Start")!!
    }

    override fun getUsage(): String {
        return "/brc start"
    }

    override fun getPermission(): String {
        return "broadcaster.start"
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        if (!BroadcastingThread.running) {
            BroadcastingThread.start()
            sender.sendMessageF(plugin.localization.localize("Started")!!)
        } else {
            sender.sendMessageF(plugin.localization.localize("Messages.AlreadyRunning")!!)
        }
        return true
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessageF("&9${getUsage()} &f- &3${getDescription()}")
    }
}