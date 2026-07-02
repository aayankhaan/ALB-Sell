package com.aayan.albsell.managers

import com.aayan.albcore.utils.MessageUtil
import com.aayan.albcore.utils.PlayerDataUtil
import com.aayan.albcore.utils.SoundUtil
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

data class Tier(
    val required: Double,
    val multiplier: Double
)

data class Category(
    val id: String,
    val name: String,
    val items: Set<Material>,
    val tiers: List<Tier>,
    val tierUpMessage: String,
    val tierUpTitle: String,
    val tierUpSubtitle: String,
    val tierUpSound: String
)

object CategoryManager {

    private val materialToCategory = mutableMapOf<Material, Category>()
    private val categories = mutableMapOf<String, Category>()
    private val defaultCategories = listOf(
        "ore.yml",
        "farming.yml"
    )
    val categoryIds: Set<String> get() = categories.keys
    val loadedCategory: Int get() = categories.size

    fun load(plugin: JavaPlugin) {
        materialToCategory.clear()
        categories.clear()

        val folder = File(plugin.dataFolder, "categories")


        if (!folder.exists()) {
            folder.mkdirs()

            defaultCategories.forEach {
                plugin.saveResource("categories/$it", false)
            }
        }


        folder.listFiles { file -> file.extension == "yml" }?.forEach { file ->
            loadCategory(file)
        }

    }

    fun reload(plugin: JavaPlugin) {
        load(plugin)
        println("\u001B[32m  Loaded ${categories.size} categories.\u001B[0m")
    }

    fun getCategory(material: Material): Category? = materialToCategory[material]

    fun getMultiplier(player: Player, categoryId: String): Double {
        val category = categories[categoryId] ?: return 1.0
        val progress = PlayerDataUtil.getDouble(player, "ALBSell.multiplier.$categoryId.progress")

        var multiplier = 1.0
        for (tier in category.tiers) {
            if (progress >= tier.required) {
                multiplier = tier.multiplier
            } else break
        }

        return multiplier
    }

    fun addProgress(player: Player, categoryId: String, amount: Double, plugin: JavaPlugin) {
        val category = categories[categoryId] ?: return
        val key = "ALBSell.multiplier.$categoryId.progress"

        val current = PlayerDataUtil.getDouble(player, key)
        val new = current + amount
        PlayerDataUtil.set(player, key, new)

        val oldMultiplier = getMultiplierFromProgress(category, current)
        val newMultiplier = getMultiplierFromProgress(category, new)

        if (newMultiplier > oldMultiplier) {
            onTierUp(player, category, newMultiplier, plugin)
        }
    }

    fun getNextTier(player: Player, categoryId: String): Tier? {
        val category = categories[categoryId] ?: return null
        val progress = PlayerDataUtil.getDouble(player, "ALBSell.multiplier.$categoryId.progress")

        return category.tiers.firstOrNull { it.required > progress }
    }

    private fun loadCategory(file: File) {
        val config = YamlConfiguration.loadConfiguration(file)
        val id = file.nameWithoutExtension

        val name = config.getString("name", id) ?: id

        val items = config.getStringList("items").mapNotNull { key ->
            Material.matchMaterial(key).also {
                if (it == null) println("[ALBSell] Unknown material '$key' in ${file.name}")
            }
        }.toSet()

        val tiers = config.getMapList("tiers").mapNotNull { map ->
            val required = (map["required"] as? Number)?.toDouble() ?: return@mapNotNull null
            val multiplier = (map["multiplier"] as? Number)?.toDouble() ?: return@mapNotNull null
            Tier(required, multiplier)
        }.sortedBy { it.required }

        val tierUpMessage = config.getString("tier-up.message", "")!!
        val tierUpTitle = config.getString("tier-up.title", "")!!
        val tierUpSubtitle = config.getString("tier-up.subtitle", "")!!
        val tierUpSound = config.getString("tier-up.sound", "")!!

        val category = Category(
            id,
            name,
            items,
            tiers,
            tierUpMessage,
            tierUpTitle,
            tierUpSubtitle,
            tierUpSound
        )

        categories[id] = category
        items.forEach { materialToCategory[it] = category }
    }

    private fun getMultiplierFromProgress(category: Category, progress: Double): Double {
        var multiplier = 1.0
        for (tier in category.tiers) {
            if (progress >= tier.required) multiplier = tier.multiplier
            else break
        }
        return multiplier
    }

    private fun onTierUp(player: Player, category: Category, newMultiplier: Double, plugin: JavaPlugin) {
        val message = category.tierUpMessage
            .replace("%multiplier%", newMultiplier.toString())
            .replace("%category%", category.name)

        val title = category.tierUpTitle
            .replace("%multiplier%", newMultiplier.toString())
            .replace("%category%", category.name)

        val subtitle = category.tierUpSubtitle
            .replace("%multiplier%", newMultiplier.toString())
            .replace("%category%", category.name)

        if (message.isNotBlank()) {
            MessageUtil.send(player, message)
        }

        if (title.isNotBlank() || subtitle.isNotBlank()) {
            MessageUtil.sendTitle(player, title, subtitle)
        }

        if (category.tierUpSound.isNotBlank()) {
            SoundUtil.play(player, category.tierUpSound)
        }
    }

    fun resetCategory(player: Player, categoryId: String) {
        PlayerDataUtil.set(player, "multiplier.$categoryId.progress", 0.0)
    }
}