package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.gui.UnbanMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
        Map<String, Object> unbanMenuConfig = plugin.getConfigManager().getUnbanMenuConfig();
        String buyMenuTitle = ((String) buyMenuConfig.get("title")).replace("&", "§");
        String unbanMenuTitle = ((String) unbanMenuConfig.get("title")).replace("&", "§");
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (inventoryTitle.equals(buyMenuTitle)) {
            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            Map<String, Object> heartItemConfig = (Map<String, Object>) buyMenuConfig.get("heart_item");
            int heartSlot = (int) heartItemConfig.get("slot");
            Map<String, Object> unbanItemConfig = (Map<String, Object>) buyMenuConfig.get("unban_item");
            int unbanSlot = (int) unbanItemConfig.get("slot");

            if (event.getSlot() == heartSlot) {
                handleHeartPurchase(player);
            } else if (event.getSlot() == unbanSlot) {
                new UnbanMenu(plugin, player).open();
            }
        } else if (inventoryTitle.equals(unbanMenuTitle)) {
            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            Map<String, Object> crystalItemConfig = (Map<String, Object>) unbanMenuConfig.get("crystal_item");
            int crystalSlot = (int) crystalItemConfig.get("slot");

            if (event.getSlot() == crystalSlot) {
                handleUnbanPurchase(player);
            }
        }
    }

    private void handleHeartPurchase(Player player) {
        if (!plugin.getConfigManager().isEconomyEnabled()) {
            player.closeInventory();
            return;
        }

        int heartPrice = plugin.getConfigManager().getHeartPrice();
        int currentHearts = plugin.getHeartManager().getPlayerHearts(player.getUniqueId());
        int maxHearts = plugin.getConfigManager().getMaxHearts();

        if (currentHearts >= maxHearts) {
            player.sendMessage(plugin.getConfigManager().getMessage("max_hearts_reached"));
            player.closeInventory();
            return;
        }

        if (plugin.getEconomy().getBalance(player) < heartPrice) {
            player.sendMessage(plugin.getConfigManager().getMessage("not_enough_money"));
            return;
        }

        plugin.getEconomy().withdrawPlayer(player, heartPrice);
        plugin.getHeartManager().addPlayerHearts(player.getUniqueId(), 1);
        player.sendMessage(plugin.getConfigManager().getMessage("heart_bought"));
        player.closeInventory();
    }

    private void handleUnbanPurchase(Player player) {
        if (!plugin.getConfigManager().isEconomyEnabled()) {
            player.closeInventory();
            return;
        }

        int unbanPrice = plugin.getConfigManager().getUnbanPrice();

        if (plugin.getEconomy().getBalance(player) < unbanPrice) {
            player.sendMessage(plugin.getConfigManager().getMessage("not_enough_money"));
            return;
        }

        plugin.getEconomy().withdrawPlayer(player, unbanPrice);
        plugin.getBanManager().unbanPlayer(player.getUniqueId());
        player.sendMessage(plugin.getConfigManager().getMessage("unban_bought"));
        player.closeInventory();
    }
}