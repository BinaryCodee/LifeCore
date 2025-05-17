package it.blacked.lifestealcore.gui;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyMenu {

    private final LifeCore plugin;
    private final Player player;
    private final Inventory inventory;

    public BuyMenu(LifeCore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        Map<String, Object> buyMenuConfig = plugin.getConfigManager().getBuyMenuConfig();
        String title = ChatColor.translateAlternateColorCodes('&', (String) buyMenuConfig.get("title"));
        int size = (int) buyMenuConfig.get("size");
        this.inventory = Bukkit.createInventory(null, size, title);

        setupItems();
    }

    private void setupItems() {
        Map<String, Object> buyMenuConfig = plugin.getConfigManager().getBuyMenuConfig();
        Map<String, Object> unbanItemConfig = (Map<String, Object>) buyMenuConfig.get("unban_item");
        String unbanName = ChatColor.translateAlternateColorCodes('&', (String) unbanItemConfig.get("name"));
        Material unbanMaterial = Material.valueOf((String) unbanItemConfig.get("material"));
        List<String> unbanLore = (List<String>) unbanItemConfig.get("lore");
        int unbanSlot = (int) unbanItemConfig.get("slot");

        ItemStack unbanItem = new ItemStack(unbanMaterial);
        ItemMeta unbanMeta = unbanItem.getItemMeta();
        unbanMeta.setDisplayName(unbanName);

        List<String> formattedUnbanLore = new ArrayList<>();
        for (String line : unbanLore) {
            formattedUnbanLore.add(line.replace("{unban_price}", String.valueOf(plugin.getConfigManager().getUnbanPrice())));
        }

        unbanMeta.setLore(formattedUnbanLore);
        unbanItem.setItemMeta(unbanMeta);
        inventory.setItem(unbanSlot, unbanItem);

        Map<String, Object> heartItemConfig = (Map<String, Object>) buyMenuConfig.get("heart_item");
        String heartName = ChatColor.translateAlternateColorCodes('&', (String) heartItemConfig.get("name"));
        Material heartMaterial = Material.valueOf((String) heartItemConfig.get("material"));
        List<String> heartLore = (List<String>) heartItemConfig.get("lore");
        int heartSlot = (int) heartItemConfig.get("slot");

        ItemStack heartItem = new ItemStack(heartMaterial);
        ItemMeta heartMeta = heartItem.getItemMeta();
        heartMeta.setDisplayName(heartName);

        List<String> formattedHeartLore = new ArrayList<>();
        for (String line : heartLore) {
            formattedHeartLore.add(line.replace("{heart_price}", String.valueOf(plugin.getConfigManager().getHeartPrice())));
        }

        heartMeta.setLore(formattedHeartLore);
        heartItem.setItemMeta(heartMeta);

        inventory.setItem(heartSlot, heartItem);
    }

    public void open() {
        player.openInventory(inventory);
    }
}