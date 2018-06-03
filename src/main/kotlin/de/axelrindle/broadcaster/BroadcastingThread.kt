package de.axelrindle.broadcaster

import org.apache.commons.lang.math.RandomUtils
import org.bukkit.Bukkit

/**
 * The [BroadcastingThread] class is responsible for starting and stopping for
 * scheduling a repeating task that broadcasts the configured messages.
 */
object BroadcastingThread {

    private var id: Int = 0
    private var index = 0
    private var maxIndex: Int = 0
    private var randomize: Boolean = false
    private var lastRandomIndex: Int = 0
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
        randomize = plugin.configuration.getBoolean("Cast.Randomize")
        return Runnable {
            var message = if (randomize) getRandomMessage(messages) else messages[index]
            message = Formatter.format(plugin, message)

            val prefix = Formatter.formatColors(plugin.configuration.getString("Cast.Prefix"))
            val needsPermission = plugin.configuration.getBoolean("Cast.NeedPermissionToSee")
            if (needsPermission) {
                Bukkit.getServer().broadcast(prefix + message, "broadcaster.see")
            } else {
                Bukkit.getServer().broadcastMessage(prefix + message)
            }

            // don't change index if randomizing
            if (randomize) return@Runnable
            index++
            if (index == maxIndex) index = 0
        }
    }

    /**
     * Returns a random message from a list.
     *
     * @param messages A [List] of messages.
     * @return A randomly selected message.
     */
    private fun getRandomMessage(messages: List<String>): String {
        // get a random index
        val rand = RandomUtils.nextInt(messages.size)

        // make sure no message appears twice in a row
        return if (rand == lastRandomIndex)
            // if we got the same index as before, generate a new one
            // Note: using recursion here
            getRandomMessage(messages)
        else {
            // we got a different index, so save it and return the appropriate message
            lastRandomIndex = rand
            messages[rand]
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