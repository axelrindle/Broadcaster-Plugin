package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.Formatter
import org.apache.commons.lang.ArrayUtils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class CastCommand(
        plugin: Broadcaster,
        parent: BrcCommand
) : SubCommand(plugin, parent, "cast", "broadcaster.cast") {

    override fun execute(sender: CommandSender, args: Array<String>) {
        val subArray = ArrayUtils.subarray(args, 1, args.size)
        var s = ""
        for (s1 in subArray) s += "$s1 "
        s = Formatter.format(plugin, s)

        val prefix = Formatter.formatColors(plugin.config!!.getString("Cast.Prefix"))
        val needsPermission = plugin.config!!.getBoolean("Cast.NeedPermissionToSee")
        if (needsPermission) // only send to those having the required permission
            Bukkit.getServer().broadcast(prefix + s, "broadcaster.see")
        else // send to all
            Bukkit.getServer().broadcastMessage(prefix + s)
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessage("§9/brc cast [message] §f- §3Cast's a message around the server")
    }
}