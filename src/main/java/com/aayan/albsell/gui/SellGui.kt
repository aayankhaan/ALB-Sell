package com.aayan.albsell.gui

import com.aayan.albcore.gui.GuiBuilder
import com.aayan.albcore.logging.DiscordColor
import com.aayan.albcore.logging.DiscordLogger
import com.aayan.albcore.utils.AnimationUtil
import com.aayan.albcore.utils.ItemBuilder
import com.aayan.albcore.utils.MessageUtil
import com.aayan.albcore.utils.SoundUtil
import com.aayan.albsell.managers.SellManager
import com.aayan.albsell.managers.WorthManager
import org.bukkit.Material
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.BlockStateMeta

object SellGui {

    private const val ROWS = 6
    private val SELL_SLOTS = 0..44
    private const val CLOSE_SLOT = 45
    private const val SELL_BUTTON_SLOT = 53

    fun open(player: Player) {
        val gui = GuiBuilder("&8Sell", ROWS)

        gui
            .onClose { p -> returnLeftoverItems(gui, p) }
            .refreshEvery(5L)
            .allowItemPlacement(SELL_SLOTS) { item -> isAllowedItem(item) }
            .setItem(CLOSE_SLOT, { p ->
                ItemBuilder(Material.BARRIER).name(p, "&cClose").build()
            }) { p ->
                p.closeInventory()
            }
            .setItem(SELL_BUTTON_SLOT, { p ->
                sellButtonItem(p, gui)
            }) { p ->
                handleSellClick(p, gui)
            }
            .open(player)
    }

    private fun isAllowedItem(item: org.bukkit.inventory.ItemStack): Boolean {
        val meta = item.itemMeta
        val isShulker = meta is BlockStateMeta && meta.blockState is ShulkerBox

        val hasNoLore = !item.hasItemMeta() || item.itemMeta?.lore().isNullOrEmpty()

        return hasNoLore && (WorthManager.hasWorth(item.type) || isShulker)
    }

    private fun sellButtonItem(player: Player, gui: GuiBuilder): org.bukkit.inventory.ItemStack {
        val result = SellManager.process(gui.getItems(SELL_SLOTS))

        return if (result.sellItems.isEmpty()) {
            ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name(player, "&cNo Items to sell :(")
                .build()
        } else {
            ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                .name(player, "&#0dff00$ &f${SellManager.formatTotal(result.total)}")
                .lore(player, "&7(( Click to sell ))")
                .build()
        }
    }

    private fun handleSellClick(player: Player, gui: GuiBuilder) {
        val result = SellManager.process(gui.getItems(SELL_SLOTS))

        if (result.sellItems.isEmpty()) {
            MessageUtil.send(player, "&cNo items to sell!")
            SoundUtil.play(player, "minecraft:entity.villager.no")
            return
        }

        MessageUtil.send(player, "&#0dff00+ $ &f${SellManager.formatTotal(result.total)}")

        logSaleToDiscord(player, result)

        gui.clearItems(SELL_SLOTS)
        result.returnItems.forEach { item -> gui.addItem(item) }

        player.closeInventory()
        SoundUtil.play(player, "minecraft:entity.player.levelup")

        AnimationUtil.animateTitleNumber(
            player,
            "&#0dff00&l+ $%amount%",
            to = result.total.toLong()
        )
    }


    private fun logSaleToDiscord(player: Player, result: com.aayan.albsell.managers.SellResult) {
        if (!DiscordLogger.isLoaded("sell")) return

        val breakdown = result.sellItems
            .groupBy { it.type }
            .mapValues { (_, items) -> items.sumOf { it.amount } }
            .toList()
            .sortedByDescending { (_, amount) -> amount }

        val breakdownText = breakdown.joinToString("\n") { (material, amount) ->
            "**${formatMaterialName(material)}** x$amount"
        }

        DiscordLogger.on("sell").embed {
            title = "Item Sold"
            description = "**${player.name}** sold ${breakdown.size} item type(s)."
            color = DiscordColor.GREEN
            timestamp = true
            field("Items Sold", breakdownText, inline = false)
            field("Total Earned", "$${SellManager.formatTotal(result.total)} coins", inline = true)
            field("Player", player.name, inline = true)
            footer = "ALBSell"
        }
    }
    private fun formatMaterialName(material: org.bukkit.Material): String {
        return material.name.lowercase()
            .split("_")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun returnLeftoverItems(gui: GuiBuilder, player: Player) {
        val items = gui.getItems(SELL_SLOTS)
        if (items.isEmpty()) return

        items.forEach { item ->
            val leftover = player.inventory.addItem(item)
            leftover.values.forEach { dropped ->
                player.world.dropItemNaturally(player.location, dropped)
            }
        }
    }
}