package de.axelrindle.broadcaster.model

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.chat.ComponentSerializer

/**
 * A `Message` describes a piece of information that will be broadcasted around the server.
 */
abstract class Message
/**
 * @param input The input text used for configuration.
 */
(protected val input: String)

/**
 * A `SimpleMessage` is just a container for holding a plain text message.
 */
class SimpleMessage(input: String) : Message(input) {

    /**
     * Returns just the input string to use as the message text.
     *
     * @return The [input].
     */
    fun getText(): String = input
}

/**
 * A `JsonMessage` is created from a json string and converted into an [Array] of [BaseComponent]s.
 *
 * @see ComponentSerializer.parse
 * @see BaseComponent
 */
class JsonMessage(input: String) : Message(input) {

    // test for class dependency
    init {
        Class.forName("net.md_5.bungee.chat.ComponentSerializer")
    }

    val components: Array<BaseComponent> = ComponentSerializer.parse(input)
}