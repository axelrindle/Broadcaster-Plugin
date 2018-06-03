package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import org.bukkit.command.CommandSender

abstract class SubCommand(
        protected val plugin: Broadcaster,
        protected val parent: BrcCommand,
        val signature: String,
        val permission: String? = null
) {
    abstract fun execute(sender: CommandSender, args: Array<String>)
    abstract fun sendHelp(sender: CommandSender)
}