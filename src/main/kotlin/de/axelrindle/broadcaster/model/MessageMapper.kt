package de.axelrindle.broadcaster.model

import de.axelrindle.broadcaster.plugin
import org.bukkit.configuration.ConfigurationSection
import java.io.File
import java.util.*

/**
 * Utility class for mapping entries in the `messages.yml` file to [Message] objects.
 */
object MessageMapper {

    /**
     * Defines supported types of messages that are not of type [String].
     */
    private enum class ExtraSupportedMessageTypes {
        JSON
    }

    /**
     * Tries to map an entry from the `messages.yml` file.
     *
     * @param entry The entry object from [ConfigurationSection.getList]
     */
    fun mapConfigEntry(entry: Any?): Message? {
        return when (entry) {
            is String -> SimpleMessage(entry)
            is LinkedHashMap<*, *> -> mapEntryPerType(entry)
            else -> null
        }
    }

    /**
     * Maps config entries that are not plain string entries. For a list of supported types
     * see [ExtraSupportedMessageTypes].
     *
     * @param entry A [LinkedHashMap] defining the `Type` and the `Definition` file.
     */
    private fun mapEntryPerType(entry: LinkedHashMap<*, *>): Message? {
        if (entry.containsKey("Type").not() || entry.containsKey("Definition").not()) {
            plugin.logger.warning("Invalid message definition found! ($entry)")
        }

        val type = entry["Type"].toString().toUpperCase(Locale.ENGLISH)
        val definitionFile = entry["Definition"].toString()

        // check for supported message type
        try {
            when (ExtraSupportedMessageTypes.valueOf(type)) {
                ExtraSupportedMessageTypes.JSON -> {
                    val content = File(plugin.dataFolder, "json/$definitionFile.json").readText()
                    return JsonMessage(content)
                }
            }
        } catch (e: ClassNotFoundException) {
            plugin.logger.warning("Please make sure you're using Spigot or a fork of it!")
        } catch (e: IllegalArgumentException) {
            plugin.logger.warning("Invalid message type \"$type\"! " +
                    "Currently only \"${ExtraSupportedMessageTypes.values().joinToString(", ")}\" are supported.")
        } catch (e: NullPointerException) {
            plugin.logger.warning("Unsupported message definition in \"json/$definitionFile.json\"! " +
                    "Make sure your Spigot version is compatible.")
        }

        return null
    }
}