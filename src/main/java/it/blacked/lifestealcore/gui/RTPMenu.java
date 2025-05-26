package it.blacked.lifestealcore.gui;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RTPMenu {

    private final LifeCore plugin;
    private final Player player;
    private final ConfigManager configManager;
    private final Inventory inventory;

    public RTPMenu(LifeCore plugin, Player player, ConfigManager configManager) {
        this.plugin = plugin;
        this.player = player;
        this.configManager = configManager;
        int rows = configManager.getRtpMenuRows() != 0 ? configManager.getRtpMenuRows() : 6;
        this.inventory = Bukkit.createInventory(null, rows * 9,
                ChatColor.translateAlternateColorCodes('&', configManager.getRtpMenuTitle()));

        initializeItems();
    }

    private void initializeItems() {
        ItemStack filler = createFillerItem();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }

        Map<String, Map<String, Object>> menuItems = configManager.getRtpMenuItems();

        for (Map.Entry<String, Map<String, Object>> entry : menuItems.entrySet()) {
            String itemKey = entry.getKey();
            Map<String, Object> itemConfig = entry.getValue();
            String worldName = (String) itemConfig.get("world");
            if (worldName == null) worldName = "";
            Object slotObj = itemConfig.get("slot");
            if (slotObj == null) continue;
            int slot = ((Number) slotObj).intValue();
            ItemStack item;
            if (worldName.isEmpty()) {
                item = createDecorationItem(itemConfig, itemKey);
            } else {
                item = createWorldItem(itemConfig, worldName);
            }

            if (slot >= 0 && slot < inventory.getSize()) {
                inventory.setItem(slot, item);
            }
        }
    }

    private ItemStack createFillerItem() {
        Map<String, Object> fillConfig = configManager.getRtpFillItem();
        Material material;
        try {
            material = Material.valueOf((String) fillConfig.get("material"));
        } catch (IllegalArgumentException e) {
            material = Material.BLACK_STAINED_GLASS_PANE;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = (String) fillConfig.get("name");
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createDecorationItem(Map<String, Object> itemConfig, String itemKey) {
        Material material;
        try {
            material = Material.valueOf((String) itemConfig.get("type"));
        } catch (IllegalArgumentException e) {
            if (itemKey.contains("decoration")) {
                material = itemKey.contains("cyan") ? Material.CYAN_STAINED_GLASS_PANE : Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            } else {
                material = Material.COMPASS;
            }
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = (String) itemConfig.get("name");
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            Object loreObj = itemConfig.get("lore");
            if (loreObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> configLore = (List<String>) loreObj;
                if (!configLore.isEmpty()) {
                    List<String> lore = new ArrayList<>();
                    for (String line : configLore) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', line));
                    }
                    meta.setLore(lore);
                }
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createWorldItem(Map<String, Object> itemConfig, String worldName) {
        Material material;
        try {
            material = Material.valueOf((String) itemConfig.get("type"));
        } catch (IllegalArgumentException e) {
            switch (worldName.toLowerCase()) {
                case "world":
                    material = Material.GRASS_BLOCK;
                    break;
                case "world_nether":
                    material = Material.NETHERRACK;
                    break;
                case "world_the_end":
                    material = Material.END_STONE;
                    break;
                default:
                    material = Material.COMPASS;
            }
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = (String) itemConfig.get("name");
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            Object loreObj = itemConfig.get("lore");
            if (loreObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> configLore = (List<String>) loreObj;
                List<String> lore = new ArrayList<>();
                for (String line : configLore) {
                    String status = plugin.getServer().getWorld(worldName) != null ?
                            "&a&lDisponibile" : "&c&lNon disponibile";
                    String processedLine = line
                            .replace("%status%", status)
                            .replace("%world%", worldName);
                    lore.add(ChatColor.translateAlternateColorCodes('&', processedLine));
                }
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public void open() {
        player.openInventory(inventory);
    }

    public String getWorldNameFromSlot(int slot) {
        Map<String, Map<String, Object>> menuItems = configManager.getRtpMenuItems();
        for (Map.Entry<String, Map<String, Object>> entry : menuItems.entrySet()) {
            Map<String, Object> itemConfig = entry.getValue();
            if (itemConfig.get("slot") != null && ((Number) itemConfig.get("slot")).intValue() == slot) {
                String world = (String) itemConfig.get("world");
                return world != null ? world : "";
            }
        }
        return "";
    }
}