package de.axelrindle.broadcaster

import com.google.common.io.Files
import de.axelrindle.broadcaster.command.BrcCommand
import org.apache.commons.io.IOUtils
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.logging.Logger

/**
 * The main plugin class. Does initialization and config loading.
 */
class Broadcaster : JavaPlugin() {

    companion object {
        const val CONSOLE_PREFIX = "[Broadcaster]"
        const val CHAT_PREFIX = "&2Broadcaster &f>"

        private val logger = Logger.getLogger("Broadcaster")

        /**
         * Wrapper function around [Logger.info] which includes the [CONSOLE_PREFIX].
         */
        fun info(msg: String) {
            logger.info("$CONSOLE_PREFIX $msg")
        }
    }

    internal lateinit var configuration: FileConfiguration
    internal lateinit var messages: List<String>

    override fun onEnable() {
        info("Startup...")

        // loading configuration files
        info("Loading configuration...")
        try {
            reloadConfig()
            loadMessages()
        } catch (e: IOException) {
            e.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        // register command
        getCommand("brc").executor = BrcCommand(this)

        // start casting if we should
        if (configuration.getBoolean("Cast.OnServerStart")) startBroadcast()

        info("Done! ${description.version}")
    }

    override fun onDisable() {
        BroadcastingThread.stop()
        info("Shutdown complete.")
    }

    @Throws(IOException::class)
    override fun reloadConfig() {
        val file = File("plugins/Broadcaster/config.yml")
        if (!file.exists()) {
            Files.createParentDirs(file)
            file.createNewFile()
            IOUtils.copy(getResource("config.yml"), FileOutputStream(file))
        }

        configuration = YamlConfiguration.loadConfiguration(file)
    }

    @Throws(IOException::class)
    internal fun loadMessages() {
        val file = File("plugins/Broadcaster/messages.yml")
        if (!file.exists()) {
            Files.createParentDirs(file)
            file.createNewFile()
            IOUtils.copy(getResource("messages.yml"), FileOutputStream(file))
        }

        val config = YamlConfiguration.loadConfiguration(file)
        messages = config.getStringList("Messages")
    }

    private fun startBroadcast() {
        val interval = configuration.getInt("Cast.Interval")
        BroadcastingThread.start(this, messages, interval)
    }

}