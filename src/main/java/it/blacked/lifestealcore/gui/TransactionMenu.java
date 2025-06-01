package it.blacked.lifestealcore.gui;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.holder.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TransactionMenu {
    private final LifeCore plugin;

    public TransactionMenu(LifeCore plugin) {
        this.plugin = plugin;
    }

    public Inventory createTransactionInventory(String itemKey, Material material, String displayName, double buyPrice, double sellPrice, boolean isBuy, int quantity) {
        String buyTitleRaw = plugin.getConfigManager().getConfig().getString("shop.transaction.buy_title", "&2&lAcquista &8- &6&l%item%");
        String sellTitleRaw = plugin.getConfigManager().getConfig().getString("shop.transaction.sell_title", "&c&lVendi &8- &6&l%item%");
        buyTitleRaw = buyTitleRaw.replace("%item%", "%s");
        sellTitleRaw = sellTitleRaw.replace("%item%", "%s");
        String title = isBuy ? String.format(ChatColor.translateAlternateColorCodes('&', buyTitleRaw), ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', displayName != null ? displayName : itemKey)))
                : String.format(ChatColor.translateAlternateColorCodes('&', sellTitleRaw), ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', displayName != null ? displayName : itemKey)));
        Inventory inv = Bukkit.createInventory(new TransactionHolder(itemKey, material, isBuy, quantity), 54, title);
        ItemStack item = new ItemStack(material, quantity > 0 ? quantity : 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName != null ? displayName : itemKey));
        if (material == Material.SPAWNER) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
            CreatureSpawner spawner = (CreatureSpawner) blockStateMeta.getBlockState();
            EntityType spawnerType = getSpawnerTypeFromItem(itemKey);
            if (spawnerType != null) {
                spawner.setSpawnedType(spawnerType);
                blockStateMeta.setBlockState(spawner);
            }
        }
        itemMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "",
                isBuy ? ChatColor.DARK_GREEN + "Prezzo Acquisto: " + String.format("%.2f", buyPrice) + "$" :
                        ChatColor.RED + "Prezzo Vendita: " + String.format("%.2f", sellPrice) + "$",
                ChatColor.YELLOW + "Quantità: " + quantity,
                ChatColor.GRAY + "",
                ChatColor.YELLOW + "Seleziona con i blocchi di vetro la quantità!"
        ));
        item.setItemMeta(itemMeta);
        inv.setItem(22, item);
        inv.setItem(18, createGlassPane("&c-64"));
        inv.setItem(19, createGlassPane("&c-10"));
        inv.setItem(20, createGlassPane("&c-1"));
        inv.setItem(24, createGlassPane("&a+1"));
        inv.setItem(25, createGlassPane("&a+10"));
        inv.setItem(26, createGlassPane("&a+64"));
        inv.setItem(39, createGlassBlock("&aConferma"));
        inv.setItem(41, createGlassBlock("&cAnnulla"));
        ItemStack filler = createGlassPane(" ");
        for (int i = 0; i < 54; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, filler);
            }
        }
        return inv;
    }

    private ItemStack createGlassPane(String name) {
        ItemStack item = new ItemStack(name.contains("-") ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE);
        if (name.equals(" ")) item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createGlassBlock(String name) {
        ItemStack item = new ItemStack(name.equals("&aConferma") ? Material.GREEN_STAINED_GLASS : Material.RED_STAINED_GLASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    private EntityType getSpawnerTypeFromItem(String itemKey) {
        switch (itemKey.toLowerCase()) {
            case "zombie_spawner":
                return EntityType.ZOMBIE;
            case "skeleton_spawner":
                return EntityType.SKELETON;
            case "creeper_spawner":
                return EntityType.CREEPER;
            case "enderman_spawner":
                return EntityType.ENDERMAN;
            case "iron_golem_spawner":
                return EntityType.IRON_GOLEM;
            default:
                return null;
        }
    }
}