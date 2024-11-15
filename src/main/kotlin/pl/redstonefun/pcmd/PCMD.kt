package pl.redstonefun.pcmd

import org.bukkit.Bukkit.getCommandMap
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import pl.redstonefun.pcmd.commands.PcmdCommand
import pl.redstonefun.pcmd.listeners.BlockPlaceListener
import pl.redstonefun.pcmd.listeners.PlayerInteractListener

class PCMD : JavaPlugin() {

    companion object {
        val PCMD_KEY_TELLRAW = NamespacedKey("pcmd", "tellraw")
        val PCMD_KEY_ORIGINAL = NamespacedKey("pcmd", "pcmd")

        val PERMISSION_USE_ME = "rf2.pcmd.me"
        val PERMISSION_USE_ALL = "rf2.pcmd.all"
    }

    override fun onEnable() {
        server.pluginManager.apply {
            registerEvents(BlockPlaceListener(), this@PCMD)
            registerEvents(PlayerInteractListener(), this@PCMD)
        }

        getCommandMap().apply {
            register("pcmd", PcmdCommand())
        }

        logger.info("PCMD włączony. Cześć!")
    }

    override fun onDisable() {
        logger.info("PCMD wyłączony. Nara!")
    }
}
