package pl.redstonefun.pcmd.commands

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import pl.redstonefun.pcmd.PCMD

class PcmdCommand(private val plugin: PCMD) : CommandExecutor {

    companion object {
        private val PCMD_KEY = NamespacedKey("pcmdplugin", "pcmd_lore")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) {
            sender.sendMessage("§cUżycie: /pcmd <tekst>")
            return true
        }

        val loreText = args.joinToString(" ")
        val item = ItemStack(Material.GOLD_BLOCK)
        val meta = item.itemMeta as ItemMeta

        // Ustawienie nazwy i dodanie `PersistentData` z `NamespacedKey`
        meta.setDisplayName("PCMD")
        meta.persistentDataContainer.set(PCMD_KEY, PersistentDataType.STRING, loreText)
        item.itemMeta = meta

        // Dodanie przedmiotu do ekwipunku gracza
        sender.inventory.addItem(item)
        sender.sendMessage("§aDodano blok PCMD do twojego ekwipunku.")
        return true
    }
}
