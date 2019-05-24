package de.axelrindle.broadcaster

import de.axelrindle.broadcaster.command.BrcCommand
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketConfig
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException

/**
 * The main plugin class. Does initialization and config loading.
 */
class Broadcaster : JavaPlugin() {

    companion object {
        const val CHAT_PREFIX = "&2Broadcaster &f> "
        var instance: Broadcaster? = null
    }

    internal val config = PocketConfig(this)

    override fun onEnable() {
        logger.info("Startup...")

        // instance setup
        if (instance == null) {
            instance = this
        } else {
            logger.severe("An instance already exists!?")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        // loading configuration files
        logger.info("Loading configuration...")
        try {
            config.register("config", getResource("config.yml")!!)
            config.register("messages", getResource("messages.yml")!!)
        } catch (e: IOException) {
            logger.severe("Failed to load configuration files!")
            logger.severe(e.message)
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        // register command
        PocketCommand.register(this, BrcCommand(this))

        // start casting if we should
        if (config.access("config")!!.getBoolean("Cast.OnServerStart"))
            BroadcastingThread.start()

        logger.info("Done! v${description.version}")
    }

    override fun onDisable() {
        BroadcastingThread.stop()
        logger.info("Shutdown complete.")
    }
}