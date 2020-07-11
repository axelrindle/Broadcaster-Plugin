package de.axelrindle.broadcaster

import de.axelrindle.broadcaster.util.Align
import de.axelrindle.broadcaster.util.Formatter
import de.axelrindle.pocketknife.util.ChatUtils.formatColors
import org.apache.commons.lang.math.RandomUtils
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

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
    internal var paused = false

    /**
     * Starts the scheduled message broadcast.
     */
    fun start() {
        if (running) return
        paused = false

        // config options
        val plugin = Broadcaster.instance!!
        val messages = plugin.config.access("messages")!!.getStringList("Messages")
        val interval = plugin.config.access("config")!!.getInt("Cast.Interval")

        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                getRunnable(plugin, messages),
                interval * 20L,
                interval * 20L // 20L is one "Tick" (Minecraft Second) in Minecraft. To calculate the period, we need to multiply the interval seconds with the length of one Tick.
        )
        if (id != -1) running = true
    }

    /**
     * Stops the broadcasting task.
     *
     * @param pause Whether to pause instead of stopping. Pausing will not reset the message index.
     */
    fun stop(pause: Boolean = false) {
        Bukkit.getScheduler().cancelTask(id)
        running = false
        if (pause)
            paused = true
        else
            index = 0
    }

    private fun getRunnable(plugin: Broadcaster, messages: List<String>): Runnable {
        val config = plugin.config.access("config")!!
        val prefix = formatColors(config.getString("Cast.Prefix")!!)
        val needsPermission = config.getBoolean("Cast.NeedPermissionToSee")
        val randomize = config.getBoolean("Cast.Randomize")
        val maxIndex = messages.size

        return Runnable {
            // get a message
            var message = if (randomize) getRandomMessage(messages) else messages[index]
            message = Formatter.format(plugin, message)

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

    /**
     * Listens for player events to pause or resume the broadcast.
     */
    class EventListener : Listener {

        @EventHandler
        fun onPlayerJoin(event: PlayerJoinEvent) {
            if (paused) {
                start()
            }
        }

        @EventHandler
        fun onPlayerQuit(event: PlayerQuitEvent) {
            val pauseOnEmpty = Broadcaster.instance!!.config.access("config")!!
                    .getBoolean("Cast.PauseOnEmptyServer")
            val onlinePlayers = Bukkit.getOnlinePlayers().size - 1
            if (pauseOnEmpty && onlinePlayers <= 0) {
                Broadcaster.instance!!.logger.info("Broadcasting paused.")
                stop(true)
            }
        }
    }
}