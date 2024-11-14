package pl.redstonefun.pcmd.listeners

//import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.block.CommandBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.material.*
import org.bukkit.event.player.PlayerInteractEvent
import pl.redstonefun.pcmd.PCMD

class PlayerInteractListener(private val plugin: PCMD) : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        println("BlockPlaceEvent $event");
//        val block = event.clickedBlock ?: return
//        if (block.type != Material.COMMAND_BLOCK) return
//
//        val commandBlock = block.state as? CommandBlock ?: return
//        val command = commandBlock.command
//        val match = Regex("/sendp (\\S+) (.+)").find(command) ?: return
//        val (targetName, message) = match.destructured
//
//        if (event.player.name != targetName) {
//            event.player.sendMessage("§cMożesz edytować tylko bloki PCMD skierowane do ciebie.")
//            return
//        }
//
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
