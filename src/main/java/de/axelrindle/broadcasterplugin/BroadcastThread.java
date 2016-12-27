package de.axelrindle.broadcasterplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Helper-class for broadcasting the messages to the server.
 */
class BroadcastThread {

    private static int id;
    private static int index = 0;
    private static int maxIndex;
    private static boolean running = false;

    /**
     * Starts the scheduled message broadcast.
     *
     * @param plugin The {@link JavaPlugin} to get config values from.
     * @param messages The {@link List<String>} with all loaded messages.
     * @param interval How often the messages should be broadcasted.
     */
    static void start(JavaPlugin plugin, List<String> messages, int interval) {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                getRunnable(plugin, messages),
                0L,
                interval * 20L // 20L is one "Tick" (Minecraft Second) in Minecraft. To calculate the period, we need to multiply the interval seconds with the length of one Tick.
        );
        if(id != -1) running = true;
    }

    private static Runnable getRunnable(JavaPlugin plugin, List<String> messages) {
        maxIndex = messages.size();
        return () -> {
            running = false;
            String message = messages.get(index);
            message = Formatter.format(plugin, message);

            String prefix = Formatter.formatColors(plugin.getConfig().getString("Cast.Prefix"));
            boolean needsPermission = plugin.getConfig().getBoolean("Cast.NeedPermissionToSee");
            if(needsPermission) {
                Bukkit.getServer().broadcast(prefix + message, "broadcaster.see");
            } else {
                Bukkit.getServer().broadcastMessage(prefix + message);
            }

            index++;
            if(index == maxIndex) index = 0;
            running = true;
        };
    }

    static void stop() {
        Bukkit.getScheduler().cancelTask(id);
        running = false;
        index = 0;
    }

    static boolean isRunning() {
        return running;
    }
}
