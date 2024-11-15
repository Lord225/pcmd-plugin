package pl.redstonefun.pcmd.listeners

//import net.wesjd.anvilgui.AnvilGUI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.block.CommandBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.Material;
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import pl.redstonefun.pcmd.PCMD

class PlayerInteractListener : Listener {

    fun feedbackMessage(originalCommand: String): Component {
        val clickEvent = Component.text("Click to copy")
            .clickEvent(ClickEvent.copyToClipboard(originalCommand))
            .color(NamedTextColor.YELLOW)
            .decorate(TextDecoration.UNDERLINED)

        return Component.text("pcmd block removed. ")
                .color(NamedTextColor.RED)
                .append(clickEvent)
    }
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock.takeIf { it?.type == Material.COMMAND_BLOCK } ?: return
        val commandBlock = (block.state as? CommandBlock) ?: return
        val original = commandBlock.persistentDataContainer.get(PCMD.PCMD_KEY_ORIGINAL, PersistentDataType.STRING) ?: return
        val user = commandBlock.persistentDataContainer.get(PCMD.PCMD_KEY_USER, PersistentDataType.STRING) ?: return

        if (event.action == Action.LEFT_CLICK_BLOCK) {
            if (event.player.name != user && event.player.hasPermission(PCMD.PERMISSION_USE_ALL).not()) {
                event.player.sendMessage(Component.text("Paws off! This PCMD block was made by $user, and you don't have claws to remove it. meow!").color(NamedTextColor.RED))
                return
            }

            commandBlock.persistentDataContainer.remove(PCMD.PCMD_KEY_ORIGINAL)
            block.type = Material.AIR
            commandBlock.update()

            event.player.sendMessage(feedbackMessage(original))
        }
    }


}
