package com.aayan.albsell.managers

import com.aayan.albcore.utils.NumberUtil
import com.aayan.albcore.utils.ShulkerUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class SellResult(
    val sellItems: List<ItemStack>,
    val returnItems: List<ItemStack>,
    val total: Double
)

object SellManager {

    fun calculateTotal(items: List<ItemStack>, player: Player): Double {
        return items.sumOf { item ->
            val basePrice = WorthManager.getWorth(item.type) ?: 0.0
            val category = CategoryManager.getCategory(item.type)
            val multiplier = if (category != null) CategoryManager.getMultiplier(player, category.id) else 1.0
            basePrice * item.amount * multiplier
        }
    }

    fun process(player: Player, items: List<ItemStack>): SellResult {
        val result = ShulkerUtil.process(items) { item ->
            WorthManager.hasWorth(item.type)
        }

        val total = calculateTotal(result.sellItems, player)

        return SellResult(
            sellItems = result.sellItems,
            returnItems = result.returnItems,
            total = total
        )
    }

    fun formatTotal(total: Double): String {
        return NumberUtil.formatNumber(total.toLong())
    }
}