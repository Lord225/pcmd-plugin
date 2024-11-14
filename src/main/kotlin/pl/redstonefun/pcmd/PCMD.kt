package pl.redstonefun.pcmd

import org.bukkit.Bukkit.getCommandMap
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import pl.redstonefun.pcmd.commands.PcmdCommand
import pl.redstonefun.pcmd.commands.SendPCommand
import pl.redstonefun.pcmd.listeners.BlockPlaceListener
import pl.redstonefun.pcmd.listeners.PlayerInteractListener

class PCMD : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.apply {
            registerEvents(BlockPlaceListener(this@PCMD), this@PCMD)
            registerEvents(PlayerInteractListener(this@PCMD), this@PCMD)
        }

        getCommandMap().apply {
            register("pcmd", PcmdCommand(this@PCMD))
        }

        logger.info("Siema! Plugin działa.")
    }

    override fun onDisable() {
        logger.info("Plugin wyłączony. Nara!")
    }
}
