package com.aayan.albsell.commands

import com.aayan.albcore.commands.CommandUtil
import com.aayan.albsell.gui.SellGui
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object SellCommand {

    fun register(plugin: JavaPlugin) {
        CommandUtil.registerCommand(plugin, "sell") {
            playerOnly = true

            action { sender, _ ->
                SellGui.open(sender as Player)
            }
        }
    }
}