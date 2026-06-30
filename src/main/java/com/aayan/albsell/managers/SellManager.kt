package com.aayan.albsell.managers

import com.aayan.albcore.utils.NumberUtil
import com.aayan.albcore.utils.ShulkerUtil
import org.bukkit.inventory.ItemStack

data class SellResult(
    val sellItems: List<ItemStack>,
    val returnItems: List<ItemStack>,
    val total: Double
)

object SellManager {

    fun calculateTotal(items: List<ItemStack>): Double {
        return items.sumOf { item ->
            (WorthManager.getWorth(item.type) ?: 0.0) * item.amount
        }
    }

    fun process(items: List<ItemStack>): SellResult {
        val result = ShulkerUtil.process(items) { item ->
            WorthManager.hasWorth(item.type)
        }

        val total = calculateTotal(result.sellItems)

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