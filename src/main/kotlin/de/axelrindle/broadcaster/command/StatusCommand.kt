package de.axelrindle.broadcaster.command

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.BroadcastingThread
import de.axelrindle.broadcaster.plugin
import de.axelrindle.pocketknife.PocketCommand
import de.axelrindle.pocketknife.PocketInventory
import de.axelrindle.pocketknife.util.InventoryUtils.makeStack
import de.axelrindle.pocketknife.util.sendMessageF
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

/**
 * Display some status information about the plugin, e.g. thread status and version.
 */
class StatusCommand : PocketCommand() {

    private val title = plugin.localization.localize("Words.Status")!!
    private val pocketInventory = PocketInventory(plugin, title, 3)

    init {
        val pane = makeStack(Material.BLUE_STAINED_GLASS_PANE, "-")
        for (i in 0..8) {
            pocketInventory.setItem(i, pane)
            pocketInventory.setItem(i + 18, pane)
        }
        pocketInventory.setItem(9, pane)
        pocketInventory.setItem(17, pane)

        // status item
        setStatusItem()

        // message amount
        val amountMessages = BroadcastingThread.messages.size
        val messagesLore = arrayOf(
                "$amountMessages loaded"
        )
        val messagesItem = makeStack(Material.PAPER, "Broadcast Messages", *messagesLore)
        pocketInventory.setItem(13, messagesItem)

        // version info
        val desc = plugin.description
        val versionLore = arrayOf(
                "Plugin : " + desc.version,
                "API    : " + desc.apiVersion
        )
        val versionItem = makeStack(Material.NAME_TAG, "Versions", *versionLore)
        pocketInventory.setItem(14, versionItem)

        // github
        val githubItem = makeStack(Material.ENDER_EYE, "Github", "View the source code")
        pocketInventory.setItem(15, githubItem) {
            it.whoClicked.sendMessage("https://github.com/axelrindle/Broadcaster-Plugin")
        }
    }

    private fun setStatusItem(inventory: Inventory? = null) {
        val runningMaterial = if (BroadcastingThread.running) Material.GREEN_CONCRETE else Material.RED_CONCRETE
        val runningTitle = "The broadcast is ${if (BroadcastingThread.running.not()) "not " else ""}running"
        val lore = arrayOf(
                "Pause: " + BroadcastingThread.paused,
                "Click to toggle"
        )
        val runningStack = makeStack(runningMaterial, runningTitle, *lore)
        val listener = fun(e: InventoryClickEvent) {
            val player = e.whoClicked as Player
            if (BroadcastingThread.running)
                player.performCommand("brc stop")
            else
                player.performCommand("brc start")

            Bukkit.getScheduler().runTask(plugin, Runnable {
                setStatusItem(e.inventory)
                player.updateInventory()
            })
        }

        inventory?.setItem(11, runningStack)
        pocketInventory.setItem(11, runningStack, listener)
    }

    override fun getName(): String {
        return "status"
    }

    override fun getDescription(): String {
        return plugin.localization.localize("CommandHelp.Status")!!
    }

    override fun getPermission(): String {
        return "broadcaster.status"
    }

    override fun getUsage(): String {
        return "/brc status"
    }

    override fun handle(sender: CommandSender, command: Command, args: Array<out String>): Boolean {
        if (sender is Player) {
            setStatusItem()
            pocketInventory.open(sender)
        } else {
            val helpText = plugin.localization.localize("Words.Status")!!
            sender.sendMessageF(Broadcaster.CHAT_PREFIX + helpText)

            sender.sendMessageF("Running: " + formatBoolean(BroadcastingThread.running))
            sender.sendMessageF("Paused: " + formatBoolean(BroadcastingThread.paused))

            val amountMessages = BroadcastingThread.messages.size
            sender.sendMessageF("Total messages loaded: &6$amountMessages")

            val description = plugin.description
            sender.sendMessageF("Version: &a" + description.version)
            sender.sendMessageF("API Version: &a" + description.apiVersion)
        }
        return true
    }

    override fun sendHelp(sender: CommandSender) {
        sender.sendMessageF("&9${getUsage()} &f- &3${getDescription()}")
    }

    private fun formatBoolean(
            boolean: Boolean,
            colorTrue: ChatColor = ChatColor.GREEN,
            colorFalse: ChatColor = ChatColor.RED
    ): String {
        return if (boolean) "${colorTrue}${boolean}" else "${colorFalse}${boolean}"
    }
}