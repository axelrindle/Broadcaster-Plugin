package de.axelrindle.broadcaster

import de.axelrindle.pocketknife.util.ChatUtils.formatColors
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

/**
 * Helper-class for formatting messages.
 */
object Formatter {

    /**
     * Formats a message according to used placeholders and color codes.
     *
     * @param plugin The [Broadcaster] instance to get config values from.
     * @param msg The message to format.
     *
     * @return A formatted message.
     * @see ChatColor.translateAlternateColorCodes
     * @see JavaPlugin.getConfig
     */
    @Suppress("UNCHECKED_CAST")
    fun format(plugin: Broadcaster, msg: String): String {
        var message = msg
        // format placeholders
        val placeholders = plugin.config.access("config")!!
                .getList("Placeholders") as List<List<String>>

        for (holder in placeholders) {
            val key = holder[0]
            val value = holder[1]

            message = StringUtils.replace(message, key, value)
        }
        message = formatPredefinedPlaceholders(message)

        // translate color codes
        message = formatColors(message)

        return message
    }

    private fun formatPredefinedPlaceholders(msg: String): String {
        var message = msg

        // line break
        message = StringUtils.replace(message, "%n", "\n")

        // online players
        message = StringUtils.replace(message, "%online_players%", Bukkit.getServer().onlinePlayers.size.toString())

        // max players
        message = StringUtils.replace(message, "%max_players%", Bukkit.getServer().maxPlayers.toString())

        return message
    }
}