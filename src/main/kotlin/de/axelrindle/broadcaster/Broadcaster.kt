package de.axelrindle.broadcaster

import de.axelrindle.broadcaster.command.BrcCommand
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketConfig
import de.axelrindle.pocketknife.PocketLang
import net.milkbowl.vault.permission.Permission
import org.apache.commons.io.IOUtils
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * The main plugin class. Does initialization and config loading.
 */
class Broadcaster : JavaPlugin {

    constructor() : super()

    // Required for mocking
    constructor(loader: JavaPluginLoader, description: PluginDescriptionFile, dataFolder: File, file: File?) :
        super(
            loader,
            description,
            dataFolder,
            file ?: File(Broadcaster::class.java.protectionDomain.codeSource.location.path)
        )

    companion object {
        const val CHAT_PREFIX = "&2Broadcaster &f> &r"
        private var instance: Broadcaster? = null

        fun get() = instance!!
    }

    internal val config = PocketConfig(this)
    internal val localization = PocketLang(this, config)

    internal var hasPluginPlaceholderApi: Boolean = false
        private set
    internal var hasPluginVault: Boolean = false
        private set

    private var _permission: Permission? = null
    internal val permission: Permission
        get() = _permission!!

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

            // manually create example json file
            createJsonExample()
        } catch (e: IOException) {
            logger.severe("Failed to load configuration files!")
            logger.severe(e.message)
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        // localization init
        logger.info("Loading localization...")
        localization.addLanguages("en", "de")
        localization.init()

        logger.info("Initializing main functionality...")

        BroadcastingThread.loadMessages()
        logger.info("Loaded " + BroadcastingThread.messages.size + " messages.")

        // register command
        PocketCommand.register(this, BrcCommand())

        // check soft dependencies
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            hasPluginPlaceholderApi = true
            logger.info("Connected with PlaceholderAPI.")
        }
        if (setupPermissions()) {
            hasPluginVault = true
            logger.info("Connected with VaultAPI.")
        }

        // start casting if we should
        val mainConfig = config.access("config")!!
        val pauseOnEmptyServer = mainConfig.getBoolean("Cast.PauseOnEmptyServer")

        if (pauseOnEmptyServer) {
            Bukkit.getPluginManager().registerEvents(BroadcastingThread.EventListener(), this)
        }

        if (mainConfig.getBoolean("Cast.OnServerStart")) {

            // if the broadcast should be paused on empty server, start with an initial pause,
            // because no player will be online immediately after server start
            if (pauseOnEmptyServer) {
                BroadcastingThread.paused = true
            }

            // else just start the broadcast normally
            else {
                BroadcastingThread.start()
            }
        }

        logger.info("Done! v${description.version}")
    }

    private fun createJsonExample() {
        val indicatorFile = File(dataFolder, "examples_created")
        if (indicatorFile.exists().not()) {
            var success = true

            val jsonDir = File(dataFolder, "json")
            success = success && jsonDir.mkdir()

            val jsonFile = File(jsonDir, "visit-github.json")
            success = success && jsonFile.createNewFile()
            FileOutputStream(jsonFile).use { fos ->
                javaClass.getResourceAsStream("/json/visit-github.json").use { fis ->
                    fis.copyTo(fos)
                }
            }

            success = success && indicatorFile.createNewFile()
            FileOutputStream(indicatorFile).use { fos ->
                IOUtils.write("This file indicates that example files have been created " +
                        "and do not need to be created again. For now, this only includes " +
                        "the json/ directory.", fos, StandardCharsets.UTF_8)
            }

            if (success) {
                logger.info("Created example file at ${jsonFile.absolutePath}.")
            } else {
                logger.warning("Failed to create example file!")
            }
        }
    }

    private fun setupPermissions(): Boolean {
        val rsp: RegisteredServiceProvider<Permission>? = server.servicesManager.getRegistration(Permission::class.java)
        _permission = rsp?.provider
        return _permission != null
    }

    override fun onDisable() {
        try {
            BroadcastingThread.stop()
        } catch (e: RuntimeException) {
            // ignore
        }
    }
}