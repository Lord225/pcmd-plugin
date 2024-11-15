package pl.redstonefun.pcmd.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.block.CommandBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.Material
import pl.redstonefun.pcmd.PCMD


class BlockPlaceListener : Listener {
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        println("BlockPlaceEvent $event");
        val player = event.player
        val item = event.itemInHand
        val meta = item.itemMeta ?: return

        val command = meta.persistentDataContainer.get(PCMD.PCMD_KEY_TELLRAW, PersistentDataType.STRING) ?: return
        val original = meta.persistentDataContainer.get(PCMD.PCMD_KEY_ORIGINAL, PersistentDataType.STRING) ?: return

        if(!(command.startsWith("/tellraw"))) {
            player.sendMessage(Component.text("Niepoprawny blok PCMD!").color(NamedTextColor.RED))
            return
        }

        val block = event.block
        block.type = Material.COMMAND_BLOCK

        val commandBlockState = block.state as? CommandBlock ?: return
        commandBlockState.setCommand(command)
        commandBlockState.persistentDataContainer.set(PCMD.PCMD_KEY_ORIGINAL, PersistentDataType.STRING, original)
        commandBlockState.update()

        player.sendMessage(Component.text("Postawiłeś blok PCMD.").color(NamedTextColor.GREEN))
    }
}
