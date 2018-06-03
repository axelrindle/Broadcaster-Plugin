package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.Formatter
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

/**
 * The main [CommandExecutor] for the plugin.
 */
class BrcCommand(
        private val plugin: Broadcaster
) : CommandExecutor {

    /**
     * A list of registered [SubCommand]s.
     */
    private val commandList: List<SubCommand> = listOf(
            StartCommand(plugin, this),
            StopCommand(plugin, this),
            ReloadCommand(plugin, this),
            CastCommand(plugin, this)
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if (sender.hasPermission("broadcaster.brc")) {
            if (args.isEmpty()) {
                sendHelp(sender)
                return true
            } else {
                // loop through the list to find a match
                val arg0 = args[0]
                var found = false
                for (sub in commandList)
                    if (arg0 == sub.signature)
                        if (sender.hasPermission(sub.permission)) {
                            sub.execute(sender, args)
                            found = true
                            break
                        } else noPermission(sender)

                // if nothing was found, inform the sender
                if (!found)
                    sender.sendMessage("Nothing matched your input: '${args.joinToString()}'")

                return found
            }
        }

        return false
    }

    /**
     * Sends the [CommandSender] a message indicating that he has no permission to execute a command.
     *
     * @param sender The [CommandSender] to inform.
     */
    private fun noPermission(sender: CommandSender) {
        sender.sendMessage(
                Formatter.formatColors(plugin.config!!.getString("Messages.NoPermission"))
        )
    }

    /**
     * Sends the [CommandSender] an overview over all commands he may execute.
     *
     * @param sender The [CommandSender] to inform.
     */
    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Formatter.formatColors(Broadcaster.CHAT_PREFIX) + "Help")
        var i = 0
        if (sender.hasPermission("broadcaster.brc")) {
            sender.sendMessage("§9/brc §f- §3Shows all commands")
            i++
        }
        commandList.forEach {
            if (sender.hasPermission(it.permission)) {
                it.sendHelp(sender)
                i++
            }
        }

        // nothing sent, sent no permission message
        if (i == 0) noPermission(sender)
    }
}