package de.axelrindle.broadcasterplugin;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Helper-class for formatting messages.
 */
class Formatter {

    /**
     * Formats a message according to used placeholders and color codes.
     *
     * @param plugin The {@link JavaPlugin} to get config values from.
     * @param message The message to format.
     *
     * @return A formatted message.
     * @see ChatColor#translateAlternateColorCodes(char, String)
     * @see JavaPlugin#getConfig()
     */
    @SuppressWarnings("unchecked")
    static String format(JavaPlugin plugin, String message) {
        // format placeholders
        List<List<String>> placeholders = (List<List<String>>) plugin.getConfig().getList("Placeholders");
        for (List<String> holder : placeholders) {
            String key = holder.get(0);
            String value = holder.get(1);

            message = StringUtils.replace(message, key, value);
        }

        // translate color codes
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    static String formatColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
