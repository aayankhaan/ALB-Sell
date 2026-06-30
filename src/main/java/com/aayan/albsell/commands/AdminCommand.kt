package com.aayan.albsell.commands

import com.aayan.albcore.commands.CommandUtil
import com.aayan.albcore.utils.MessageUtil
import com.aayan.albsell.managers.WorthManager
import org.bukkit.plugin.java.JavaPlugin

object AdminCommand {

    fun register(plugin: JavaPlugin) {
        CommandUtil.registerCommand(plugin, "albsell") {
            description = "Main admin command"
            permission = "albsell.admin"

            onUnknownSubcommand { sender, _ ->
                MessageUtil.send(sender, "&cUsage: /albsell reload")
            }

            subcommand("reload") {
                action { sender, _ ->
                    WorthManager.reload(plugin)
                    MessageUtil.send(sender, "&aReloaded!")
                }
            }
        }
    }
}