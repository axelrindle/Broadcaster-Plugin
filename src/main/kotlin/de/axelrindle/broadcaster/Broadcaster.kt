package de.axelrindle.broadcaster

import de.axelrindle.broadcaster.command.BrcCommand
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.PocketLang
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException

/**
 * The main plugin class. Does initialization and config loading.
 */
class Broadcaster : JavaPlugin() {

    companion object {
        const val CHAT_PREFIX = "&2Broadcaster &f> &r"
        var instance: Broadcaster? = null
    }

    internal val config = PocketConfig(this)
    internal var localization = PocketLang(this, config)

    internal var hasPluginPlaceholderApi: Boolean = false
        private set

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

        // localization init
        localization.addLanguages("en", "de")
        localization.init()

        // register command
        PocketCommand.register(this, BrcCommand(this))

        // check soft dependencies
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            hasPluginPlaceholderApi = true
        }

        // start casting if we should
        val mainConfig = config.access("config")!!
        val pauseOnEmptyServer = mainConfig.getBoolean("Cast.PauseOnEmptyServer")
        if (mainConfig.getBoolean("Cast.OnServerStart")) {

            // if the broadcast should be paused on empty server, start with an initial pause,
            // because no player will be online immediately after server start
            if (pauseOnEmptyServer) {
                Bukkit.getPluginManager().registerEvents(BroadcastingThread.EventListener(), this)
                BroadcastingThread.paused = true
            }

            // else just start the broadcast normally
            else {
                BroadcastingThread.start()
            }
        }

        logger.info("Done! v${description.version}")
    }

    override fun onDisable() {
        BroadcastingThread.stop()
        logger.info("Shutdown complete.")
    }
}