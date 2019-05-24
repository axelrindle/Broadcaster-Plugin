package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.util.ChatUtils.formatColors
import de.axelrindle.pocketknife.util.Extensions.sendMessageF
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import de.axelrindle.broadcaster.Broadcaster.Companion.CHAT_PREFIX

/**
 * The main [CommandExecutor] for the plugin.
 */
class BrcCommand(
        private val plugin: Broadcaster
) : PocketCommand() {

    override fun getName(): String {
        return "brc"
    }

    override fun getSubCommands(): ArrayList<PocketCommand> {
        return arrayListOf(
                StartCommand(plugin),
                StopCommand(plugin),
                ReloadCommand(plugin),
                CastCommand(plugin)
        )
    }

    override fun messageNoPermission(): String {
        return formatColors(plugin.config.access("config")!!
                .getString("Messages.NoPermission")!!)
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessageF(CHAT_PREFIX + "Help")
        var i = 0
        if (sender.hasPermission("broadcaster.brc")) {
            sender.sendMessageF("&9/brc &f- &3Shows all commands")
            i++
        }
        getSubCommands().forEach {
            if (it.getPermission() == null || sender.hasPermission(it.getPermission()!!)) {
                it.sendHelp(sender)
                i++
            }
        }

        // nothing sent, sent no permission message
        if (i == 0) sender.sendMessage(messageNoPermission())
    }
}