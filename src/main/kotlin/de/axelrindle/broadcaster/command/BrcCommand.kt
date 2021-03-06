package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster.Companion.CHAT_PREFIX
import de.axelrindle.broadcaster.plugin
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.util.ChatUtils.formatColors
import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

/**
 * The main [CommandExecutor] for the plugin.
 */
class BrcCommand : PocketCommand() {

    override val subCommands: ArrayList<PocketCommand> = arrayListOf(
            StartCommand(),
            StopCommand(),
            ReloadCommand(),
            CastCommand(),
            StatusCommand()
    )

    override fun getName(): String {
        return "brc"
    }

    override fun getDescription(): String {
        return plugin.localization.localize("CommandHelp.Brc")!!
    }

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
        subCommands.forEach {
            if (it.getPermission() == null || sender.hasPermission(it.getPermission()!!)) {
                it.sendHelp(sender)
                i++
            }
        }

        // nothing sent, send no permission message
        if (i == 0) sender.sendMessage(messageNoPermission())
    }
}