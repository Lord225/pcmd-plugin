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
        val clickEvent = Component.text("Kliknij aby skopiować")
            .clickEvent(ClickEvent.copyToClipboard(originalCommand))
            .color(NamedTextColor.YELLOW)
            .decorate(TextDecoration.UNDERLINED)

        return Component.text("Usunięto blok PCMD. ")
                .color(NamedTextColor.RED)
                .append(clickEvent)
    }
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock.takeIf { it?.type == Material.COMMAND_BLOCK } ?: return
        val commandBlock = (block.state as? CommandBlock) ?: return
        val original = commandBlock.persistentDataContainer.get(PCMD.PCMD_KEY_ORIGINAL, PersistentDataType.STRING) ?: return

        if (event.action == Action.LEFT_CLICK_BLOCK) {
            commandBlock.persistentDataContainer.remove(PCMD.PCMD_KEY_ORIGINAL)
            block.type = Material.AIR
            commandBlock.update()

            // Create and send the clickable message
            event.player.sendMessage(feedbackMessage(original))
        }

////        AnvilGUI.Builder()
////            .onComplete { player: Player, text: String ->  // Określamy typy jawnie
////                commandBlock.command = "/sendp $targetName $text"
////                commandBlock.update()
////                player.sendMessage("§aZaktualizowano wiadomość w bloku PCMD.")
////                AnvilGUI.Response.close()
////            }
////            .onClose { player: Player ->  // Określamy typ jawnie
////                player.sendMessage("§cZamknięto edycję bloku PCMD.")
////            }
////            .text(message)
////            .plugin(plugin)
////            .open(event.player)
    }


}
