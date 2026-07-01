package com.aayan.albsell.managers

import com.aayan.albcore.logging.DiscordLogger
import org.bukkit.plugin.java.JavaPlugin

object ConfigManager {

    var discordWebhook: String = ""
    var discordUsername: String = ""

    fun load(plugin: JavaPlugin) {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        discordWebhook = plugin.config.getString("discord_webhook", "")!!
        discordUsername = plugin.config.getString("discord_username", "")!!

    }

    fun reload(plugin: JavaPlugin) {
        load(plugin)
        if (discordWebhook.isNotEmpty()) {
            DiscordLogger.setup("sell", discordWebhook, discordUsername.ifEmpty { "ALBSell Logger" })
        }
    }
}