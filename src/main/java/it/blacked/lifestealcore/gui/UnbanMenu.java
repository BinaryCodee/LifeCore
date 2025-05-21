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

public class UnbanMenu {

    private final LifeCore plugin;
    private final Player player;
    private final Inventory inventory;
    private final int rows;

    public UnbanMenu(LifeCore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        Map<String, Object> unbanMenuConfig = plugin.getConfigManager().getUnbanMenuConfig();

        if (unbanMenuConfig == null) {
            plugin.getLogger().severe("Unban Menu non valido nel file config.yml.");
            this.rows = 3;
            this.inventory = Bukkit.createInventory(null, rows * 9, "Unban Menu");
        } else {
            this.rows = Integer.parseInt(unbanMenuConfig.getOrDefault("rows", "3").toString());
            String title = plugin.getConfigManager().getUnbanMenuTitle();
            this.inventory = Bukkit.createInventory(null, rows * 9, title);
            setupItems();
        }
    }

    private void setupItems() {
        Map<String, Object> unbanMenuConfig = plugin.getConfigManager().getUnbanMenuConfig();
        if (unbanMenuConfig == null) {
            plugin.getLogger().severe("Unban menu config nullo!");
            return;
        }
        double unbanPrice = plugin.getConfigManager().getUnbanPrice();
        double playerBalance = plugin.getEconomy().getBalance(player);

        Map<String, Object> crystalItemConfig = (Map<String, Object>) unbanMenuConfig.get("crystal_item");
        if (crystalItemConfig != null) {
            int slot = Integer.parseInt(crystalItemConfig.getOrDefault("slot", "4").toString());
            String material = (String) crystalItemConfig.getOrDefault("material", "END_CRYSTAL");
            String name = ((String) crystalItemConfig.getOrDefault("name", "&bUnBan")).replace("&", "§");

            ItemStack item = new ItemStack(Material.valueOf(material));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);

            List<String> lore = (List<String>) crystalItemConfig.getOrDefault("lore", new ArrayList<String>());
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                line = line.replace("&", "§");
                line = replacePlaceholders(line, unbanPrice, playerBalance);
                formattedLore.add(line);
            }

            meta.setLore(formattedLore);
            item.setItemMeta(meta);

            inventory.setItem(slot, item);
        } else {
            plugin.getLogger().warning("Crystal item non trovato nel config!");
        }
    }

    private String replacePlaceholders(String text, double unbanPrice, double playerBalance) {
        text = text.replace("%economy_unban_price%", formatNumber(unbanPrice));
        text = text.replace("%economy_balance%", formatNumber(playerBalance));
        if (text.contains("%economy_enough_unban%")) {
            if (playerBalance >= unbanPrice) {
                text = text.replace("%economy_enough_unban%", "§a✓ §7Clicca per acquistare il tuo unban!");
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