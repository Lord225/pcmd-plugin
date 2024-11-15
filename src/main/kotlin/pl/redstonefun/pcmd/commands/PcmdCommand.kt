package pl.redstonefun.pcmd.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import pl.redstonefun.pcmd.PCMD

class PcmdCommand : Command("pcmd") {
    val logger = PCMD().logger

    init {
        description = "Creates PCMD block"
        usageMessage = "/pcmd all/me <text>"
    }

    val DEDNY_PHRASES_1 = listOf(
        "Purrhaps you should try again, meow!",
        "That was a clawful attempt. Better luck next time, purrhaps",
        "Paws off, that’s not going to work, nyan!",
        "Yeah, that's not gonna happen.",
    )

    fun usageMessage() = Component.text("Use: /pcmd all/me <text/legacy/tellarawjson>").color(NamedTextColor.RED)

    fun sanitizeText(text: Component, player: Player): Component {
        // remove all events that can run commands
        fun sanitizeComponent(text: Component): Component {
            // send message to player if there is a command
            if (text.clickEvent()?.action() == ClickEvent.Action.RUN_COMMAND) {
                player.sendMessage(Component.text(DEDNY_PHRASES_1.random()).color(NamedTextColor.RED))
                logger.warning("User ${player.name} tried to use a command in PCMD: ${text.clickEvent()?.value()}")
            }

            val sanitizedClickEvent = text.clickEvent()?.takeIf {
                it.action() != ClickEvent.Action.RUN_COMMAND
            }

            // Handle hover events
            val sanitizedHoverEvent = text.hoverEvent()?.let { hover ->
                if (hover.action() == HoverEvent.Action.SHOW_TEXT) {
                    val sanitizedHoverValue = (hover.value() as? Component)?.let(::sanitizeComponent)
                    if (sanitizedHoverValue != null) HoverEvent.showText(sanitizedHoverValue) else null
                } else null
            }

            // Recursively sanitize children
            val sanitizedChildren = text.children().map(::sanitizeComponent)

            // Create and return a sanitized copy of the component
            return text
                .clickEvent(sanitizedClickEvent)
                .hoverEvent(sanitizedHoverEvent)
                .children(sanitizedChildren)
        }

        return sanitizeComponent(text)
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
                    sender.sendMessage(Component.text("Sorry, you need the [Z] role to access 'all'.").color(NamedTextColor.RED))
                    return true
                }

                "@a[distance=..100]"
            }
            "me" -> {
                if (!sender.hasPermission(PCMD.PERMISSION_USE_ME)) {
                    sender.sendMessage(Component.text("Oops, PCMD is out of paws' reach right now. ").color(NamedTextColor.RED))
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
            sanitizeText(jsonText, sender)
        } else if (legacyText != null) {
            sanitizeText(legacyText, sender)
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


        logger.info("User ${sender.name} created PCMD: $command")

        sender.inventory.addItem(item)

        sender.sendMessage(sanitizedMessage)

        return true
    }
}
