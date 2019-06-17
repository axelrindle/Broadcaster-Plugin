package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.BroadcastingThread
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * Stops the broadcasting thread.
 *
 * @see BroadcastingThread
 */
class StopCommand(
        private val plugin: Broadcaster
) : PocketCommand() {

    override fun getName(): String {
        return "stop"
    }

    override fun getDescription(): String {
        return "Stop the broadcast."
    }

    override fun getUsage(): String {
        return "/brc stop"
    }

    override fun getPermission(): String {
        return "broadcaster.stop"
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        val config = plugin.config.access("config")!!
        if (BroadcastingThread.running) {
            BroadcastingThread.stop()
            sender.sendMessageF(config.getString("Messages.Stopped")!!)
        } else {
            sender.sendMessageF(config.getString("Messages.AlreadyStopped")!!)
        }
        return true
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessageF("&9${getUsage()} &f- &3${getDescription()}")
    }
}