package it.blacked.lifestealcore.gui;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.holder.CategoryHolder;
import it.blacked.lifestealcore.holder.MainShopHolder;
import it.blacked.lifestealcore.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopMenu {
    private final LifeCore plugin;
    private final ConfigManager configManager;

    public ShopMenu(LifeCore plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    public Inventory createMainShopInventory() {
        Map<String, Object> mainConfig = (Map<String, Object>) configManager.getShopMainConfig();
        String title = ChatColor.translateAlternateColorCodes('&', (String) mainConfig.get("title"));
        int size = (int) mainConfig.get("size");
        Inventory inv = Bukkit.createInventory(new MainShopHolder(), size, title);
        for (String key : mainConfig.keySet()) {
            if (key.equals("title") || key.equals("size")) continue;
            Map<String, Object> itemConfig = (Map<String, Object>) mainConfig.get(key);
            int slot = (int) itemConfig.get("slot");
            Material material = Material.getMaterial((String) itemConfig.get("material"));
            if (material == null) continue;
            String name = ChatColor.translateAlternateColorCodes('&', (String) itemConfig.get("name"));
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Clicca per aprire la categoria!");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }
        inv.setItem(size - 1, createCloseItem());
        return inv;
    }

    public Inventory createCategoryInventory(String category, int page) {
        Map<String, Map<String, Object>> categories = configManager.getShopCategories();
        Map<String, Object> categoryConfig = categories.get(category);
        if (categoryConfig == null) return null;
        int size = (int) categoryConfig.get("size");
        String title = ChatColor.translateAlternateColorCodes('&', category + " - Page " + page);
        Inventory inv = Bukkit.createInventory(new CategoryHolder(category, page), size, title);
        Map<String, Object> items;
        if (categoryConfig.containsKey("pages")) {
            Map<Integer, Map<String, Object>> pages = (Map<Integer, Map<String, Object>>) categoryConfig.get("pages");
            items = pages.get(page);
            if (pages.containsKey(page + 1)) inv.setItem(53, createNavigationItem(Material.ARROW, "Pagina Successiva"));
            if (page > 1) inv.setItem(45, createNavigationItem(Material.ARROW, "Pagina Precedente"));
        } else {
            items = (Map<String, Object>) categoryConfig.get("items");
        }
        if (items != null) {
            for (String itemKey : items.keySet()) {
                Map<String, Object> itemConfig = (Map<String, Object>) items.get(itemKey);
                int slot = (int) itemConfig.get("slot");
                Material material = Material.getMaterial((String) itemConfig.get("material"));
                if (material == null) continue;
                double buy = (double) itemConfig.get("buy");
                double sell = (double) itemConfig.get("sell");
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + itemKey);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "");
                if (buy >= 0) lore.add(ChatColor.GREEN + "Compra: " + buy + "$");
                if (sell >= 0) lore.add(ChatColor.RED + "Vendi: " + sell + "$");
                lore.add(ChatColor.GRAY + "");
                lore.add(ChatColor.YELLOW + "Click-Destro per Vendere!");
                lore.add(ChatColor.YELLOW + "Click-Sinistro per Acquistare!");
                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(slot, item);
            }
        }
        inv.setItem(size - 1, createCloseItem());
        inv.setItem(size - 9, createBackItem());
        return inv;
    }

    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Chiudi");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Indietro");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createNavigationItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        item.setItemMeta(meta);
        return item;
    }
}