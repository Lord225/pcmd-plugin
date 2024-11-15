package pl.redstonefun.pcmd.commands

import com.google.gson.GsonBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.Material
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import pl.redstonefun.pcmd.PCMD

class PcmdCommand(private val plugin: PCMD) : Command("pcmd") {

    init {
        description = "Sends a hello message to the player."
        usageMessage = "/hello"
    }

    companion object {
        val PCMD_KEY = NamespacedKey("pcmd", "pcmd")
    }
//
//    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
//        if (sender !is Player) return false
//        if (args.isEmpty()) {
//            sender.sendMessage("§cUżycie: /pcmd <tekst>")
//            return true
//        }
//
////        val loreText = args.joinToString(" ")
////        val item = ItemStack(Material.GOLD_BLOCK)
////        val meta = item.itemMeta as ItemMeta
////
////        // Ustawienie nazwy i dodanie `PersistentData` z `NamespacedKey`
////        meta.setDisplayName("PCMD")
////        meta.persistentDataContainer.set(PCMD_KEY, PersistentDataType.STRING, loreText)
////        item.itemMeta = meta
////
////        // Dodanie przedmiotu do ekwipunku gracza
////        sender.inventory.addItem(item)
////        sender.sendMessage("§aDodano blok PCMD do twojego ekwipunku.")
////        return true
//
//        return true
//    }

    fun usageMessage() = Component.text("Użycie: /pcmd all/me <tekst>").color(NamedTextColor.RED)

    fun sanitzeText(text: Component): Component {
        // remove all events that can run commands
        text.clickEvent(null)
        text.hoverEvent(null)
        for (event in text.children()) {
            sanitzeText(event)
        }

        return text
    }

    fun getFromLegacyText(legacyText: String): Component? {
        return try {
            LegacyComponentSerializer.legacy('&').deserialize(legacyText)
        } catch (e: Exception) {
            null;
        }
    }

    fun getFromJsonText(jsonText: String): Component? {
        return try {
            GsonComponentSerializer.gson().deserialize(jsonText)
        } catch (e: Exception) {
            null;
        }
    }

    fun getTellrawCommand(text: Component, selector: String): String {
        val jsonText = GsonComponentSerializer.gson().serialize(text)
        return "/tellraw $selector $jsonText"
    }



    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) {
            sender.sendMessage(usageMessage())
            return true
        }

        val selector = when(args[0]) {
            "all" -> "@a[r=100]"
            "me" -> sender.name
            else -> {
                sender.sendMessage(usageMessage())
                return true
            }
        }

        val rawText = args.drop(1).joinToString(" ")

        val jsonText = getFromJsonText(rawText)
        val legacyText = getFromLegacyText(rawText)

        val sanitizedMessage = if (jsonText != null) {
            sanitzeText(jsonText)
        } else if (legacyText != null) {
            sanitzeText(legacyText)
        } else {
            Component.text(rawText)
        }

        val command = getTellrawCommand(sanitizedMessage, selector)

        val item = ItemStack(Material.GOLD_BLOCK).apply {
            itemMeta = itemMeta?.also { meta ->
                meta.displayName(sanitizedMessage)
                meta.persistentDataContainer.set(PCMD_KEY, PersistentDataType.STRING, command)
            }
        }

        println("User ${sender.name} created PCMD: $command")

        sender.inventory.addItem(item)

        sender.sendMessage(sanitizedMessage)

        return true
    }
}
