package com.aayan.albsell

import com.aayan.albcore.logging.DiscordLogger
import com.aayan.albsell.commands.AdminCommand
import com.aayan.albsell.commands.SellCommand
import com.aayan.albsell.managers.WorthManager
import org.bukkit.plugin.java.JavaPlugin

class ALBSell : JavaPlugin() {

    override fun onEnable() {
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
        SellCommand.register(this)
        AdminCommand.register(this)
        DiscordLogger.setup("sell", "https://discord.com/api/webhooks/1521485737055485993/cCnkufZVvbiv6Jk2Gi5PPuN_VTmTyb1QsynxrIsc8SmyhkU22tUg8V8jXmpNuSu50Ewz")
    }

    override fun onDisable() {
    }
}