package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import org.bukkit.command.CommandSender

/**
 * A [SubCommand] is an argument to a command which is treated as an own command.
 */
abstract class SubCommand(
        protected val plugin: Broadcaster,
        protected val parent: BrcCommand,
        val signature: String,
        val permission: String? = null
) {

    /**
     * The handler for this command.
     *
     * @param sender The [CommandSender] who wants to execute this command.
     * @param args An [Array] of arguments supplied to this command.
     */
    abstract fun execute(sender: CommandSender, args: Array<String>)

    /**
     * Sends a help message which shows how to use this command.
     *
     * @param sender The [CommandSender] who needs help.
     */
    abstract fun sendHelp(sender: CommandSender)
}