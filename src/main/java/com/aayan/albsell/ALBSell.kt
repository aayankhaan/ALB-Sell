package com.aayan.albsell

import com.aayan.albsell.commands.AdminCommand
import com.aayan.albsell.commands.SellCommand
import com.aayan.albsell.managers.CategoryManager
import com.aayan.albsell.managers.ConfigManager
import com.aayan.albsell.managers.WorthManager
import org.bukkit.plugin.java.JavaPlugin

class ALBSell : JavaPlugin() {


    companion object {
        lateinit var instance: ALBSell
        private set
    }


    override fun onEnable() {
        instance = this
        WorthManager.load(this)
        ConfigManager.load(this)
        CategoryManager.load(this)
        SellCommand.register(this)
        AdminCommand.register(this)

        val green = "\u001B[32m"
        val reset = "\u001B[0m"

        println("$green=======================================================$reset")
        println("$green    _     _      ____    ____   _____  _      _     $reset")
        println("$green   / \\   | |    | __ )  / ___| | ____|| |    | |    $reset")
        println("$green  / _ \\  | |    |  _ \\  \\___ \\ |  _|  | |    | |    $reset")
        println("$green / ___ \\ | |___ | |_) |  ___) || |___ | |___ | |___ $reset")
        println("$green/_/   \\_\\|_____||____/  |____/ |_____||_____||_____|$reset")
        println("")
        println("$green Version: ${description.version}$reset")
        println("$green Author: Aayan$reset")
        println("$green Status: Loaded successfully!$reset")
        println("")
        println("$green Loaded ${WorthManager.loadedPrices} sell prices.$reset")
        println("$green Loaded ${CategoryManager.loadedCategory} categories.$reset")
        println("$green=======================================================$reset")

    }

    override fun onDisable() {
    }
}