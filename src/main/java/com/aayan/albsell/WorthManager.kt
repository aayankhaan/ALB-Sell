package com.aayan.albsell

import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object WorthManager {

    private lateinit var file: File
    private lateinit var config: YamlConfiguration

    private val prices = mutableMapOf<Material, Double>()

    fun load(plugin: JavaPlugin) {
        file = File(plugin.dataFolder, "worth.yml")

        if (!file.exists()) {
            plugin.saveResource("worth.yml", false)
        }

        config = YamlConfiguration.loadConfiguration(file)
        prices.clear()

        val invalidMaterials = mutableListOf<String>()

        config.getKeys(false).forEach { key ->
            val material = Material.matchMaterial(key)

            if (material == null) {
                invalidMaterials.add(key)
                return@forEach
            }

            prices[material] = config.getDouble(key)
        }

        plugin.logger.info("Loaded ${prices.size} sell prices.")

        if (invalidMaterials.isNotEmpty()) {
            plugin.logger.warning("${invalidMaterials.size} invalid material(s) found in worth.yml:")

            invalidMaterials.forEach {
                plugin.logger.warning(" - $it")
            }
        }
    }

    fun reload(plugin: JavaPlugin) = load(plugin)

    fun getWorth(material: Material): Double? =
        prices[material]

    fun hasWorth(material: Material): Boolean =
        material in prices
}