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
import java.util.List;
import java.util.Map;

public class UnbanMenu {

    private final LifeCore plugin;
    private final Player player;
    private final Inventory inventory;

    public UnbanMenu(LifeCore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        Map<String, Object> unbanMenuConfig = plugin.getConfigManager().getUnbanMenuConfig();
        String title = ChatColor.translateAlternateColorCodes('&', (String) unbanMenuConfig.get("title"));

        int size = (int) unbanMenuConfig.get("size");
        this.inventory = Bukkit.createInventory(null, size, title);
        setupItems();
    }

    private void setupItems() {
        Map<String, Object> unbanMenuConfig = plugin.getConfigManager().getUnbanMenuConfig();
        Map<String, Object> crystalItemConfig = (Map<String, Object>) unbanMenuConfig.get("crystal_item");
        String crystalName = ChatColor.translateAlternateColorCodes('&', (String) crystalItemConfig.get("name"));
        Material crystalMaterial = Material.valueOf((String) crystalItemConfig.get("material"));
        List<String> crystalLore = (List<String>) crystalItemConfig.get("lore");
        int crystalSlot = (int) crystalItemConfig.get("slot");

        ItemStack crystalItem = new ItemStack(crystalMaterial);
        ItemMeta crystalMeta = crystalItem.getItemMeta();
        crystalMeta.setDisplayName(crystalName);

        List<String> formattedCrystalLore = new ArrayList<>();
        for (String line : crystalLore) {
            formattedCrystalLore.add(line.replace("{unban_price}", String.valueOf(plugin.getConfigManager().getUnbanPrice())));
        }

        crystalMeta.setLore(formattedCrystalLore);
        crystalItem.setItemMeta(crystalMeta);
        inventory.setItem(crystalSlot, crystalItem);
    }

    public void open() {
        player.openInventory(inventory);
    }
}