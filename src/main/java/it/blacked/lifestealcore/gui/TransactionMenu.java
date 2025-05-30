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
        String sellTitleRaw = plugin.getConfigManager().getConfig().getString("shop.transaction.sell_title", "&c%lVendi &8- &6&l%item%");
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
            EntityType spawnerType = getSpawnerTypeFromItemKey(itemKey);
            if (spawnerType != null) {
                spawner.setSpawnedType(spawnerType);
                blockStateMeta.setBlockState(spawner);
            }
        }
        itemMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "",
                isBuy ? ChatColor.GREEN + "Prezzo: " + buyPrice + "$" : ChatColor.RED + "Prezzo: " + sellPrice + "$",
                ChatColor.YELLOW + "Quantità: " + quantity,
                ChatColor.GRAY + "",
                ChatColor.YELLOW + "Seleziona con i blocchi di vetro la quantità!"
        ));
        item.setItemMeta(itemMeta);
        inv.setItem(22, item);
        inv.setItem(18, createGlassPane(Material.RED_STAINED_GLASS_PANE, "-64"));
        inv.setItem(19, createGlassPane(Material.RED_STAINED_GLASS_PANE, "-10"));
        inv.setItem(20, createGlassPane(Material.RED_STAINED_GLASS_PANE, "-1"));
        inv.setItem(24, createGlassPane(Material.GREEN_STAINED_GLASS_PANE, "+1"));
        inv.setItem(25, createGlassPane(Material.GREEN_STAINED_GLASS_PANE, "+10"));
        inv.setItem(26, createGlassPane(Material.GREEN_STAINED_GLASS_PANE, "+64"));
        inv.setItem(39, createGlassBlock(Material.GREEN_STAINED_GLASS, ChatColor.translateAlternateColorCodes('&',
                plugin.getConfigManager().getConfig().getString("shop.transaction.confirm", "&aConferma"))));
        inv.setItem(41, createGlassBlock(Material.RED_STAINED_GLASS, ChatColor.translateAlternateColorCodes('&',
                plugin.getConfigManager().getConfig().getString("shop.transaction.cancel", "&cAnnulla"))));

        ItemStack filler = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, filler);
            }
        }

        return inv;
    }

    private ItemStack createGlassPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createGlassBlock(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private EntityType getSpawnerTypeFromItemKey(String itemKey) {
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