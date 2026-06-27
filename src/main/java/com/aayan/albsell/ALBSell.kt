package com.aayan.albsell

import com.aayan.albcore.commands.CommandUtil
import com.aayan.albcore.gui.GuiBuilder
import com.aayan.albcore.utils.ItemBuilder
import com.aayan.albcore.utils.MessageUtil
import com.aayan.albcore.utils.ShulkerUtil
import com.aayan.albcore.utils.SoundUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ALBSell : JavaPlugin() {

    override fun onEnable() {
        logger.info("ALBSell loaded!")
        registerSellCommand()
    }

    override fun onDisable() {
    }

    private fun registerSellCommand() {
        CommandUtil.registerCommand(this, "sell") {
            playerOnly = true

            action { sender, _ ->
                val player = sender as Player
                val gui = GuiBuilder("&8Sell", 6)
                gui
                    .onClose { p ->
                        val items = gui.getItems(0..44)
                        if (items.isEmpty()) return@onClose
                        items.forEach { item ->
                            val leftover = p.inventory.addItem(item)
                            leftover.values.forEach { dropped ->
                                p.world.dropItemNaturally(p.location, dropped)
                            }
                        }
                    }
                    .refreshEvery(5L)
                    .allowItemPlacement(0..44) { item ->
                        !item.hasItemMeta() || item.itemMeta?.lore().isNullOrEmpty()
                    }
                    .setItem(45, { p ->
                        ItemBuilder(Material.BARRIER).name(p, "&cClose").build()
                    }) { p ->
                        p.closeInventory()
                    }
                    .setItem(53, { p ->
                        val items = ShulkerUtil.flatten(gui.getItems(0..44))
                        if (items.isEmpty())
                            ItemBuilder(Material.RED_STAINED_GLASS_PANE).name(p, "&cNo Items to sell :(").build()
                        else
                            ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name(p, "&#0dff00$ &f10").lore(p, "&7(( Click to sell )))").build()
                    }) { p ->
                        val items = ShulkerUtil.flatten(gui.getItems(0..44))
                        if (items.isEmpty()) {
                            MessageUtil.send(p, "&cNo items to sell!")
                            SoundUtil.play(p, "minecraft:entity.villager.no")
                        } else {
                            MessageUtil.send(p, items.toString())
                        }
                    }
                    .open(player)
            }
        }
    }
}