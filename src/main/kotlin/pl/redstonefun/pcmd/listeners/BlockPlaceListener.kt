package pl.redstonefun.pcmd.listeners

import org.bukkit.Material
import org.bukkit.block.CommandBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType
import pl.redstonefun.pcmd.PCMD

class BlockPlaceListener(private val plugin: PCMD) : Listener {

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val item = event.itemInHand
        val meta = item.itemMeta ?: return

        // Sprawdzenie, czy jest to blok PCMD
        if (meta.displayName == "PCMD" && meta.persistentDataContainer.has(PCMD.PCMD_KEY, PersistentDataType.STRING)) {
            val loreText = meta.persistentDataContainer.get(PCMD.PCMD_KEY, PersistentDataType.STRING) ?: return

            // Anulowanie zdarzenia, by zablokować blok złota
            event.isCancelled = true

            // Ustawienie bloku jako COMMAND_BLOCK bez ponownego przypisywania `val`
            val block = event.block
            block.type = Material.COMMAND_BLOCK

            // Aktualizacja stanu CommandBlock
            val commandBlockState = block.state as? CommandBlock ?: return
            commandBlockState.name = "PCMD"
            commandBlockState.command = "/sendp ${player.name} $loreText"
            commandBlockState.update(true, false)

            player.sendMessage("§aStworzono blok PCMD.")
        }
    }
}
