package pl.redstonefun.pcmd

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import pl.redstonefun.pcmd.commands.PcmdCommand
import pl.redstonefun.pcmd.commands.SendPCommand
import pl.redstonefun.pcmd.listeners.BlockPlaceListener
import pl.redstonefun.pcmd.listeners.PlayerInteractListener

class PCMD : JavaPlugin() {

    companion object {
        val PCMD_KEY = NamespacedKey("pcmdplugin", "pcmd_lore")
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(BlockPlaceListener(this), this)
        server.pluginManager.registerEvents(PlayerInteractListener(this), this)
        getCommand("pcmd")?.setExecutor(PcmdCommand(this))
        getCommand("sendp")?.setExecutor(SendPCommand(this))
        logger.info("Siema! Plugin działa.")
    }

    override fun onDisable() {
        logger.info("Plugin wyłączony. Nara!")
    }
}
