package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryClickListener implements Listener {

    private final LifeCore plugin;

    public InventoryClickListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        String inventoryTitle = event.getView().getTitle();
        Map<String, Object> buyMenuConfig = plugin.getConfigManager().getBuyMenuConfig();
        String buyMenuTitle = ((String) buyMenuConfig.get("title")).replace("&", "§");
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (inventoryTitle.equals(buyMenuTitle)) {
            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            Map<String, Object> heartItemConfig = (Map<String, Object>) buyMenuConfig.get("heart_item");
            int heartSlot = Integer.parseInt(heartItemConfig.get("slot").toString());
            Map<String, Object> unbanItemConfig = (Map<String, Object>) buyMenuConfig.get("unban_item");
            int unbanSlot = Integer.parseInt(unbanItemConfig.get("slot").toString());

            if (event.getSlot() == heartSlot) {
                handleHeartPurchase(player);
            } else if (event.getSlot() == unbanSlot) {
                handleDirectUnban(player);
            }
        }
    }

    private void handleHeartPurchase(Player player) {
        if (!plugin.getConfigManager().isEconomyEnabled()) {
            player.closeInventory();
            return;
        }

        int heartPrice = (int) plugin.getConfigManager().getHeartPrice();

        if (plugin.getEconomy().getBalance(player) < heartPrice) {
            player.sendMessage(plugin.getConfigManager().getMessage("not_enough_money"));
            player.closeInventory();
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getConfigManager().getMessage("inventory_full"));
            player.closeInventory();
            return;
        }

        plugin.getEconomy().withdrawPlayer(player, heartPrice);

        ItemStack heartItem = createHeartItem();
        player.getInventory().addItem(heartItem);

        player.sendMessage(plugin.getConfigManager().getMessage("heart_bought"));
        player.closeInventory();
    }

    private ItemStack createHeartItem() {
        Map<String, Object> heartConfig = plugin.getConfigManager().getHeartItemConfig();
        String material = (String) heartConfig.get("material");
        String name = ((String) heartConfig.get("name")).replace("&", "§");

        ItemStack item = new ItemStack(Material.valueOf(material));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    private void handleDirectUnban(Player player) {
        if (!plugin.getConfigManager().isEconomyEnabled()) {
            player.closeInventory();
            return;
        }

        int unbanPrice = (int) plugin.getConfigManager().getUnbanPrice();

        if (plugin.getEconomy().getBalance(player) < unbanPrice) {
            player.sendMessage(plugin.getConfigManager().getMessage("not_enough_money"));
            player.closeInventory();
            return;
        }
        if (!plugin.getBanManager().isPlayerBanned(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("already_unbanned"));
            player.closeInventory();
            return;
        }

        plugin.getEconomy().withdrawPlayer(player, unbanPrice);
        plugin.getBanManager().unbanPlayer(player.getUniqueId());
        player.sendMessage(plugin.getConfigManager().getMessage("unban_bought"));
        player.closeInventory();
    }
}