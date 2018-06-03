package de.axelrindle.broadcaster

import org.bukkit.Bukkit

/**
 * The [BroadcastingThread] class is responsible for starting and stopping for
 * scheduling a repeating task that broadcasts the configured messages.
 */
object BroadcastingThread {

    private var id: Int = 0
    private var index = 0
    private var maxIndex: Int = 0
    internal var running = false
        private set

    /**
     * Starts the scheduled message broadcast.
     *
     * @param plugin The [Broadcaster] instance to get config values from.
     * @param messages The [List] with all loaded messages.
     * @param interval How often the messages should be broadcasted.
     */
    fun start(plugin: Broadcaster, messages: List<String>, interval: Int) {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                getRunnable(plugin, messages),
                0L,
                interval * 20L // 20L is one "Tick" (Minecraft Second) in Minecraft. To calculate the period, we need to multiply the interval seconds with the length of one Tick.
        )
        if (id != -1) running = true
    }

    private fun getRunnable(plugin: Broadcaster, messages: List<String>): Runnable {
        maxIndex = messages.size
        return Runnable {
            var message = messages[index]
            message = Formatter.format(plugin, message)

            val prefix = Formatter.formatColors(plugin.configuration.getString("Cast.Prefix"))
            val needsPermission = plugin.configuration.getBoolean("Cast.NeedPermissionToSee")
            if (needsPermission) {
                Bukkit.getServer().broadcast(prefix + message, "broadcaster.see")
            } else {
                Bukkit.getServer().broadcastMessage(prefix + message)
            }

            index++
            if (index == maxIndex) index = 0
        }
    }

    /**
     * Stops the broadcasting task.
     */
    fun stop() {
        Bukkit.getScheduler().cancelTask(id)
        running = false
        index = 0
    }
}