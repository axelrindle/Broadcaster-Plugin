package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.util.ChatUtils.formatColors
import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import de.axelrindle.broadcaster.Broadcaster.Companion.CHAT_PREFIX

/**
 * The main [CommandExecutor] for the plugin.
 */
class BrcCommand(
        private val plugin: Broadcaster
) : PocketCommand() {

    private val subCommands = arrayListOf(
            StartCommand(plugin),
            StopCommand(plugin),
            ReloadCommand(plugin),
            CastCommand(plugin),
            StatusCommand(plugin)
    )

    override fun getName(): String {
        return "brc"
    }

    override fun getDescription(): String {
        return plugin.localization.localize("CommandHelp.Brc")!!
    }

    override fun getSubCommands(): ArrayList<PocketCommand> = subCommands

    override fun messageNoPermission(): String {
        return formatColors(plugin.localization.localize("NoPermission")!!)
    }

    override fun sendHelp(sender: CommandSender) {
        val helpText = plugin.localization.localize("Words.Help")!!
        sender.sendMessageF(CHAT_PREFIX + helpText)
        var i = 0
        if (sender.hasPermission("broadcaster.brc")) {
            sender.sendMessageF("&9/${getName()} &f- &3${getDescription()}")
            i++
        }
        getSubCommands().forEach {
            if (it.getPermission() == null || sender.hasPermission(it.getPermission()!!)) {
                it.sendHelp(sender)
                i++
            }
        }

        // nothing sent, send no permission message
        if (i == 0) sender.sendMessage(messageNoPermission())
    }
}