package de.axelrindle.broadcaster

import de.axelrindle.broadcaster.model.JsonMessage
import de.axelrindle.broadcaster.model.Message
import de.axelrindle.broadcaster.model.MessageMapper
import de.axelrindle.broadcaster.model.SimpleMessage
import de.axelrindle.broadcaster.util.Align
import de.axelrindle.broadcaster.util.Formatter
import de.axelrindle.pocketknife.util.ChatUtils.formatColors
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.apache.commons.lang.math.RandomUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.stream.Collectors

/**
 * The [BroadcastingThread] class is responsible for starting and stopping for
 * scheduling a repeating task that broadcasts the configured messages.
 */
object BroadcastingThread {

    private var id: Int = 0
    private var index = 0
    private var lastRandomIndex: Int = 0
    internal var running = false
        private set
    internal var paused = false

    private val spaceComponent = TextComponent.fromLegacyText(" ")

    /**
     * Starts the scheduled message broadcast.
     */
    fun start() {
        if (running) return
        paused = false

        // config options
        val plugin = Broadcaster.instance!!
        val interval = plugin.config.access("config")!!.getInt("Cast.Interval")
        val messages = plugin.config.access("messages")!!
                .getList("Messages", emptyList<Message>())!!
                .stream()
                .map(MessageMapper::mapConfigEntry)
                .filter(Objects::nonNull)
                .collect(Collectors.toList())

        @Suppress("UNCHECKED_CAST")
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                getRunnable(plugin, messages as List<Message>),
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

    private fun getRunnable(plugin: Broadcaster, messages: List<Message>): Runnable {
        val config = plugin.config.access("config")!!
        val prefix = formatColors(config.getString("Cast.Prefix")!!)
        val needsPermission = config.getBoolean("Cast.NeedPermissionToSee")
        val randomize = config.getBoolean("Cast.Randomize")
        val maxIndex = messages.size

        val prefixComponent = TextComponent.fromLegacyText(prefix)

        return Runnable {
            // get a message
            val theMessage = if (randomize) getRandomMessage(messages) else messages[index]

            // further actions depend on the message type
            if (theMessage is SimpleMessage) {
                val message = Formatter.format(plugin, theMessage.getText())

                // check for center alignment
                if (message.startsWith("%c")) {
                    broadcastCentered(prefix, message.replace("%c", ""), needsPermission)
                } else {
                    broadcast("$prefix $message", getPermission(needsPermission))
                }
            }
            else if (theMessage is JsonMessage) {
                val components = prefixComponent
                        .plus(spaceComponent)
                        .plus(theMessage.components)
                broadcastComponent(components, getPermission(needsPermission))
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

    private fun broadcast(message: String, permission: String?) {
        if (permission == null)
            Bukkit.broadcastMessage(message)
        else
            Bukkit.broadcast(message, permission)
    }

    private fun broadcastCentered(prefix: String, message: String, needsPermission: Boolean) {
        val permission = getPermission(needsPermission)
        val list = Align.center(message, prefix)
        list.forEach {
            broadcast("$prefix $it", permission)
        }
    }

    private fun broadcastComponent(components: Array<BaseComponent>, permission: String?) {
        Bukkit.getConsoleSender().spigot().sendMessage(*components)
        Bukkit.getServer().onlinePlayers.forEach { player ->
            if (permission == null || player.hasPermission(permission) || player.isOp) {
                player.spigot().sendMessage(ChatMessageType.CHAT, *components)
            }
        }
    }

    /**
     * Returns a random message from a list.
     *
     * @param messages A [List] of messages.
     * @return A randomly selected message.
     */
    private fun getRandomMessage(messages: List<Message>): Message {
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

        /**
         * Called when a [Player] joins the server. Used to resume the broadcast if it has
         * been paused due to no players being online.
         */
        @EventHandler
        fun onPlayerJoin(event: PlayerJoinEvent) {
            if (paused) {
                start()
            }
        }

        /**
         * Called when a [Player] leaves the server. Used to pause the broadcast if no players
         * are online anymore.
         */
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