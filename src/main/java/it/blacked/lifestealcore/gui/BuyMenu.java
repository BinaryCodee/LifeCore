package it.blacked.lifestealcore.gui;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BuyMenu {

    private final LifeCore plugin;
    private final Player player;
    private final Inventory inventory;
    private final int rows;

    public BuyMenu(LifeCore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        Map<String, Object> buyMenuConfig = plugin.getConfigManager().getBuyMenuConfig();

        if (buyMenuConfig == null) {
            plugin.getLogger().severe("BuyMenu non valido nel file config.yml.");
            this.rows = 3;
            this.inventory = Bukkit.createInventory(null, rows * 9, "Shop");
        } else {
            this.rows = Integer.parseInt(buyMenuConfig.getOrDefault("rows", "3").toString());
            String title = plugin.getConfigManager().getBuyMenuTitle();
            this.inventory = Bukkit.createInventory(null, rows * 9, title);
            setupItems();
        }
    }

    private void setupItems() {
        Map<String, Object> buyMenuConfig = plugin.getConfigManager().getBuyMenuConfig();
        if (buyMenuConfig == null) {
            plugin.getLogger().severe("Buy menu config nullo!");
            return;
        }

        double heartPrice = plugin.getConfigManager().getHeartPrice();
        double unbanPrice = plugin.getConfigManager().getUnbanPrice();
        double playerBalance = plugin.getEconomy().getBalance(player);

        Map<String, Object> heartItemConfig = (Map<String, Object>) buyMenuConfig.get("heart_item");
        if (heartItemConfig != null) {
            int slot = Integer.parseInt(heartItemConfig.getOrDefault("slot", "0").toString());
            String material = (String) heartItemConfig.getOrDefault("material", "NETHER_STAR");
            String name = ((String) heartItemConfig.getOrDefault("name", "&cCuore")).replace("&", "§");

            ItemStack item = new ItemStack(Material.valueOf(material));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);

            List<String> lore = (List<String>) heartItemConfig.getOrDefault("lore", new ArrayList<String>());
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                line = line.replace("&", "§");
                line = replacePlaceholders(line, heartPrice, unbanPrice, playerBalance);
                formattedLore.add(line);
            }

            meta.setLore(formattedLore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
        } else {
            plugin.getLogger().warning("Heart item non trovato nel config!");
        }

        Map<String, Object> unbanItemConfig = (Map<String, Object>) buyMenuConfig.get("unban_item");
        if (unbanItemConfig != null) {
            int slot = Integer.parseInt(unbanItemConfig.getOrDefault("slot", "1").toString());
            String material = (String) unbanItemConfig.getOrDefault("material", "END_CRYSTAL");
            String name = ((String) unbanItemConfig.getOrDefault("name", "&bUnBan")).replace("&", "§");

            ItemStack item = new ItemStack(Material.valueOf(material));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);

            List<String> lore = (List<String>) unbanItemConfig.getOrDefault("lore", new ArrayList<String>());
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                line = line.replace("&", "§");
                line = replacePlaceholders(line, heartPrice, unbanPrice, playerBalance);
                formattedLore.add(line);
            }

            meta.setLore(formattedLore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
        } else {
            plugin.getLogger().warning("Unban item non valido nel config!");
        }
    }

    private String replacePlaceholders(String text, double heartPrice, double unbanPrice, double playerBalance) {
        text = text.replace("%economy_heart_price%", formatNumber(heartPrice));
        text = text.replace("%economy_unban_price%", formatNumber(unbanPrice));
        text = text.replace("%economy_balance%", formatNumber(playerBalance));

        if (text.contains("%economy_enough_heart%")) {
            if (playerBalance >= heartPrice) {
                text = text.replace("%economy_enough_heart%", "§a✓ §7Clicca per acquistare un cuore!");
            } else {
                text = text.replace("%economy_enough_heart%", "§c✘ §7Non hai abbastanza soldi!");
            }
        }

        if (text.contains("%economy_enough_unban%")) {
            if (playerBalance >= unbanPrice) {
                text = text.replace("%economy_enough_unban%", "§a✓ §7Clicca per acquistare l'unban!");
            } else {
                text = text.replace("%economy_enough_unban%", "§c✘ §7Non hai abbastanza soldi!");
            }
        }

        return text;
    }

    private String formatNumber(double number) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.ITALY);
        return format.format(number);
    }

    public void open() {
        player.openInventory(inventory);
    }
}