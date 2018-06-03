package de.axelrindle.broadcaster

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

object BroadcastingThread {

    private var id: Int = 0
    private var index = 0
    private var maxIndex: Int = 0
    private var _running = false

    var running: Boolean = false
        get() = _running

    /**
     * Starts the scheduled message broadcast.
     *
     * @param plugin The [JavaPlugin] to get config values from.
     * @param messages The [List] with all loaded messages.
     * @param interval How often the messages should be broadcasted.
     */
    fun start(plugin: JavaPlugin, messages: List<String>, interval: Int) {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                getRunnable(plugin, messages),
                0L,
                interval * 20L // 20L is one "Tick" (Minecraft Second) in Minecraft. To calculate the period, we need to multiply the interval seconds with the length of one Tick.
        )
        if (id != -1) running = true
    }

    private fun getRunnable(plugin: JavaPlugin, messages: List<String>): Runnable {
        maxIndex = messages.size
        return Runnable {
            running = false
            var message = messages[index]
            message = Formatter.format(plugin, message)

            val prefix = Formatter.formatColors(plugin.config.getString("Cast.Prefix"))
            val needsPermission = plugin.config.getBoolean("Cast.NeedPermissionToSee")
            if (needsPermission) {
                Bukkit.getServer().broadcast(prefix + message, "broadcaster.see")
            } else {
                Bukkit.getServer().broadcastMessage(prefix + message)
            }

            index++
            if (index == maxIndex) index = 0
            running = true
        }
    }

    fun stop() {
        Bukkit.getScheduler().cancelTask(id)
        running = false
        index = 0
    }
}