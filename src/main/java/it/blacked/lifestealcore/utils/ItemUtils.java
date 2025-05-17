package it.blacked.lifestealcore.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemUtils {

    public static ItemStack createItem(org.bukkit.Material material, String name, List<String> lore, Map<String, String> placeholders) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            String processedName = replacePlaceholders(name, placeholders);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', processedName));
        }

        if (lore != null && !lore.isEmpty()) {
            List<String> processedLore = new ArrayList<>();
            for (String line : lore) {
                String processedLine = replacePlaceholders(line, placeholders);
                processedLore.add(ChatColor.translateAlternateColorCodes('&', processedLine));
            }
            meta.setLore(processedLore);
        }

        item.setItemMeta(meta);
        return item;
    }

    private static String replacePlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null) {
            return text;
        }

        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return result;
    }
}