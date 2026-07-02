package com.aayan.albsell.commands

import com.aayan.albcore.commands.ArgType
import com.aayan.albcore.commands.CommandUtil
import com.aayan.albcore.utils.MessageUtil
import com.aayan.albsell.managers.CategoryManager
import com.aayan.albsell.managers.ConfigManager
import com.aayan.albsell.managers.WorthManager
import org.bukkit.Art
import org.bukkit.plugin.java.JavaPlugin

object AdminCommand {

    fun register(plugin: JavaPlugin) {
        CommandUtil.registerCommand(plugin, "albsell") {
            description = "Main admin command"
            permission = "albsell.admin"

            onUnknownSubcommand { sender, _ ->
                MessageUtil.send(sender, "&cUsage: /albsell reload/reset")
            }

            subcommand("reload") {
                action { sender, _ ->
                    WorthManager.reload(plugin)
                    ConfigManager.reload(plugin)
                    CategoryManager.reload(plugin)
                    MessageUtil.send(sender, "&aReloaded!")
                }
            }

            subcommand("reset") {
                onMissingArgs { sender, _ ->
                    MessageUtil.send(sender, "&cUsage: /albsell reset <player> <category>")
                }

                arg("target", ArgType.PLAYER) {
                    suggestPlayers()
                    onInvalid { sender, raw ->
                        MessageUtil.send(sender, "&cPlayer '$raw' not found or not online.")
                    }
                }

                arg("category", ArgType.STRING) {
                    suggest { CategoryManager.categoryIds.toList() }
                    onInvalid { sender, raw ->
                        MessageUtil.send(sender, "&c'$raw' is not a valid category.")
                    }
                }

                action { sender, args ->
                    val target = args.player("target")
                    val categoryId = args.string("category")

                    if (!CategoryManager.categoryIds.contains(categoryId)) {
                        MessageUtil.send(sender, "&cUnknown category '&f$categoryId&c'. Available: &f${CategoryManager.categoryIds.joinToString(", ")}")
                        return@action
                    }

                    CategoryManager.resetCategory(target, categoryId)
                    MessageUtil.send(sender, "&aReset &f${target.name}&a progress in &f$categoryId&a.")
                }
            }
        }
    }
}