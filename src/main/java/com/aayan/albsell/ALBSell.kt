package com.aayan.albsell

import com.aayan.albcore.logging.DiscordLogger
import com.aayan.albsell.commands.AdminCommand
import com.aayan.albsell.commands.SellCommand
import com.aayan.albsell.managers.CategoryManager
import com.aayan.albsell.managers.ConfigManager
import com.aayan.albsell.managers.ConfigManager.discordUsername
import com.aayan.albsell.managers.ConfigManager.discordWebhook
import com.aayan.albsell.managers.WorthManager
import org.bukkit.plugin.java.JavaPlugin

class ALBSell : JavaPlugin() {


    companion object {
        lateinit var instance: ALBSell
        private set
    }


    override fun onEnable() {
        instance = this
        val green = "\u001B[32m"
        val reset = "\u001B[0m"

        println("$green=======================================================$reset")
        println("$green    _     _      ____    ____   _____  _      _     $reset")
        println("$green   / \\   | |    | __ )  / ___| | ____|| |    | |    $reset")
        println("$green  / _ \\  | |    |  _ \\  \\___ \\ |  _|  | |    | |    $reset")
        println("$green / ___ \\ | |___ | |_) |  ___) || |___ | |___ | |___ $reset")
        println("$green/_/   \\_\\|_____||____/  |____/ |_____||_____||_____|$reset")
        println("")
        println("$green  Version: ${description.version}$reset")
        println("$green  Author: Aayan$reset")
        println("$green  Status: Loaded successfully!$reset")
        println("$green=======================================================$reset")

        WorthManager.load(this)
        ConfigManager.load(this)
        CategoryManager.load(this)
        SellCommand.register(this)
        AdminCommand.register(this)
        if (discordWebhook.isNotEmpty()) {
            DiscordLogger.setup("sell",discordWebhook, discordUsername.ifEmpty { "ALBSell Logger" })
        }
    }

    override fun onDisable() {
    }
}