package de.axelrindle.broadcaster.util

import com.google.common.collect.Lists
import org.apache.commons.lang.SystemUtils
import org.apache.commons.lang.WordUtils
import org.bukkit.ChatColor

/**
 * Utilities for aligning a message in the chat.
 *
 * @see <a href="https://github.com/AmoebaMan/Utils/blob/master/src/main/java/net/amoebaman/amoebautils/chat/Align.java">Source</a>
 */
object Align {

    /** The default Minecraft chat box width, in font-pixels  */
    private const val SCREEN_WIDTH = 316

    /** Most characters in Minecraft's default font are this many font-pixels wide  */
    private const val DEFAULT_CHAR_WIDTH = 6

    /** A map of the width of all irregular characters in Minecraft's default font */
    private val IRREG_CHAR_WIDTH: HashMap<Char, Int> = hashMapOf(
            Pair(' ', 4),
            Pair('i', 2),
            Pair('I', 4),
            Pair('k', 5),
            Pair('l', 3),
            Pair('t', 4),
            Pair('!', 2),
            Pair('(', 5),
            Pair(')', 5),
            Pair('~', 7),
            Pair(',', 2),
            Pair('.', 2),
            Pair('<', 5),
            Pair('>', 5),
            Pair(':', 2),
            Pair(';', 2),
            Pair('"', 5),
            Pair('[', 4),
            Pair(']', 4),
            Pair('{', 5),
            Pair('}', 5),
            Pair('|', 2),
            Pair('`', 0),
            Pair('\'', 2),
            Pair('\u2591', 8),
            Pair('\u2592', 9),
            Pair('\u2593', 9),
            Pair('\u2588', 9),
            Pair(ChatColor.COLOR_CHAR, 0)
    )

    /**
     * Gets the width of a character in Minecraft's default font, in font-pixels.
     * @param value a character
     * @param bold whether this character is in bold style (+1 px)
     * @return the width of the character
     */
    private fun getCharWidth(value: Char, bold: Boolean): Int {
        return if (IRREG_CHAR_WIDTH.containsKey(value))
            IRREG_CHAR_WIDTH[value]!! + (if (bold) 1 else 0)
        else
            DEFAULT_CHAR_WIDTH + if (bold) 1 else 0
    }

    /**
     * Gets the total width of some text in font-pixels, the sum of its characters.
     * @param str some text
     * @return the width of the text
     */
    private fun getStringWidth(str: String): Int {
        var length = 0
        var bold = false
        for (i in 0 until str.length)
            if (str[i] != ChatColor.COLOR_CHAR)
                if (i == 0)
                    length += getCharWidth(str[i], bold)
                else if (str[i - 1] != ChatColor.COLOR_CHAR)
                    length += getCharWidth(str[i], bold)
                else if (str[i] == 'l')
                    bold = true
                else if (!Lists.newArrayList('m', 'n', 'o').contains(str[i]))
                    bold = false
        return length
    }

    /**
     * Centers a single message.
     *
     * @param message The message to center.
     * @param prefix An optional prefix to include in the calculation.
     */
    private fun centerSingle(message: String, prefix: String?): String {
        var text = message.trim()
        val prefixLength = ChatColor.stripColor(prefix)?.length ?: 0
        val numSpaces = ((SCREEN_WIDTH - getStringWidth(text)) / getCharWidth(' ', false)) / 2
        for (i in 0 .. numSpaces - prefixLength)
            text = " $text"
        for (i in 0 .. numSpaces)
            text = "$text "
        return text
    }

    /**
     * Splits a long message into multiple short parts and centers them all.
     *
     * @see centerSingle
     */
    fun center(message: String, prefix: String?): List<String> {
        return WordUtils
                .wrap(message, 50) // split on multiple lines when too long
                .split(SystemUtils.LINE_SEPARATOR)
                .filter(String::isNotBlank) // remove blank lines
                .map { return@map centerSingle(it, prefix) } // center pieces
    }
}