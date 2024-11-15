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

class PcmdCommand : Command("pcmd") {

    init {
        description = "Creates PCMD block"
        usageMessage = "/pcmd all/me <text>"
    }

    fun usageMessage() = Component.text("Use: /pcmd all/me <text/legacy/tellarawjson>").color(NamedTextColor.RED)

    fun sanitzeText(text: Component): Component {
        // remove all events that can run commands
        text.clickEvent(null)
        // TODO sanitze hover effect only if not text
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


    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): MutableList<String> {
        if (args.size == 1) {
            return mutableListOf("all", "me")
        }
        return mutableListOf()
    }


    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) {
            sender.sendMessage(usageMessage())
            return true
        }

        val selector = when(args[0]) {
            "all" -> {
                if (!sender.hasPermission(PCMD.PERMISSION_USE_ALL)) {
                    sender.sendMessage(Component.text("To access 'all' you need at least role [Z]").color(NamedTextColor.RED))
                    return true
                }

                "@a[distance=..100]"
            }
            "me" -> {
                if (!sender.hasPermission(PCMD.PERMISSION_USE_ME)) {
                    sender.sendMessage(Component.text("Access to pcmd is unavailable.").color(NamedTextColor.RED))
                    return true
                }

                sender.name
            }
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
                meta.persistentDataContainer.set(PCMD.PCMD_KEY_TELLRAW, PersistentDataType.STRING, command)
                meta.persistentDataContainer.set(PCMD.PCMD_KEY_ORIGINAL, PersistentDataType.STRING, "/pcmd ${args.joinToString(" ")}")
            }
        }

        println("User ${sender.name} created PCMD: $command")

        sender.inventory.addItem(item)

        sender.sendMessage(sanitizedMessage)

        return true
    }
}
