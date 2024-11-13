package pl.redstonefun.pcmd.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pl.redstonefun.pcmd.PCMD

class SendPCommand(private val plugin: PCMD) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("§cUżycie: /sendp <nick> <tekst>")
            return true
        }

        val targetPlayer: Player? = Bukkit.getPlayer(args[0])
        if (targetPlayer == null || !targetPlayer.isOnline) {
            sender.sendMessage("§cGracz nie jest online.")
            return true
        }

        val message = args.drop(1).joinToString(" ")
        targetPlayer.sendMessage("§7[§aPCMD§7] §f$message")
        return true
    }
}
