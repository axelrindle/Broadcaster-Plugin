package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.Formatter
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.util.ChatUtils.formatColors
import de.axelrindle.pocketknife.util.Extensions.sendMessageF
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * Enables a user to manually cast a message.
 */
class CastCommand(
        private val plugin: Broadcaster
) : PocketCommand() {

    override fun getName(): String {
        return "cast"
    }

    override fun getDescription(): String {
        return "Cast a message around the server."
    }

    override fun getPermission(): String {
        return "broadcaster.cast"
    }

    override fun getUsage(): String {
        return "/brc cast [message]"
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        val msg = Formatter.format(plugin, args.joinToString(" "))
        val config = plugin.config.access("config")!!
        val prefix = formatColors(config.getString("Cast.Prefix")!!)
        val needsPermission = config.getBoolean("Cast.NeedPermissionToSee")
        val final = "$prefix $msg"
        if (needsPermission) // only send to those having the required permission
            Bukkit.getServer().broadcast(final, "broadcaster.see")
        else // send to all
            Bukkit.getServer().broadcastMessage(final)
        return true
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessageF("&9${getUsage()} &f- &3${getDescription()}")
    }
}