package de.axelrindle.broadcaster

import de.axelrindle.broadcaster.util.Align
import de.axelrindle.broadcaster.util.Formatter
import de.axelrindle.pocketknife.util.ChatUtils.formatColors
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
     */
    fun start() {
        // config options
        val plugin = Broadcaster.instance!!
        val messages = plugin.config.access("messages")!!.getStringList("Messages")
        val interval = plugin.config.access("config")!!.getInt("Cast.Interval")

        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                getRunnable(plugin, messages),
                0L,
                interval * 20L // 20L is one "Tick" (Minecraft Second) in Minecraft. To calculate the period, we need to multiply the interval seconds with the length of one Tick.
        )
        if (id != -1) running = true
    }

    /**
     * Stops the broadcasting task.
     */
    fun stop() {
        Bukkit.getScheduler().cancelTask(id)
        running = false
        index = 0
    }

    private fun getRunnable(plugin: Broadcaster, messages: List<String>): Runnable {
        maxIndex = messages.size
        randomize = plugin.config.access("config")!!.getBoolean("Cast.Randomize")
        return Runnable {
            // get a message
            var message = if (randomize) getRandomMessage(messages) else messages[index]
            message = Formatter.format(plugin, message)

            // config
            val prefix = formatColors(plugin.config.access("config")!!.getString("Cast.Prefix")!!)
            val needsPermission = plugin.config.access("config")!!.getBoolean("Cast.NeedPermissionToSee")

            // check for center alignment
            if (message.startsWith("%c")) {
                sendCentered(prefix, message.replace("%c", ""), needsPermission)
            } else {
                broadcast("$prefix $message", getPermission(needsPermission))
            }

            // don't change index if randomizing
            if (randomize) return@Runnable
            index++
            if (index == maxIndex) index = 0
        }
    }

    private fun getPermission(needsPermission: Boolean): String? {
        return if (needsPermission) "broadcaster.see" else null
    }

    private fun sendCentered(prefix: String, message: String, needsPermission: Boolean) {
        val permission = getPermission(needsPermission)
        val list = Align.center(message, prefix)
        list.forEach {
            broadcast("$prefix $it", permission)
        }
    }

    private fun broadcast(message: String, permission: String? = null) {
        if (permission == null)
            Bukkit.broadcastMessage(message)
        else
            Bukkit.broadcast(message, permission)
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
}