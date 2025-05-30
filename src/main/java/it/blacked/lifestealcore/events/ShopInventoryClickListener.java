package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.holder.CategoryHolder;
import it.blacked.lifestealcore.holder.MainShopHolder;
import it.blacked.lifestealcore.holder.TransactionHolder;
import it.blacked.lifestealcore.managers.ConfigManager;
import it.blacked.lifestealcore.managers.ShopManager;
import it.blacked.lifestealcore.gui.TransactionMenu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class ShopInventoryClickListener implements Listener {
    private final LifeCore plugin;
    private final ShopManager shopManager;
    private final Economy economy;

    public ShopInventoryClickListener(LifeCore plugin) {
        this.plugin = plugin;
        this.shopManager = new ShopManager(plugin);
        this.economy = plugin.getEconomy();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        ConfigManager configManager = plugin.getConfigManager();
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        ItemStack clickedItem = event.getCurrentItem();
        if (holder instanceof MainShopHolder || holder instanceof CategoryHolder || holder instanceof TransactionHolder) {
            event.setCancelled(true);
        } else {
            return;
        }

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (holder instanceof MainShopHolder) {
            String category = configManager.getCategoryFromSlot(slot);
            if (category != null) {
                shopManager.openCategory(player, category, 1);
            } else if (clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
            }
        } else if (holder instanceof CategoryHolder) {
            CategoryHolder catHolder = (CategoryHolder) holder;
            String category = catHolder.getCategory();
            int page = catHolder.getPage();

            if (clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
            } else if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().contains("Indietro")) {
                shopManager.openMainShop(player);
            } else if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().contains("Pagina Successiva")) {
                shopManager.openCategory(player, category, page + 1);
            } else if (clickedItem.getType() == Material.ARROW && clickedItem.getItemMeta().getDisplayName().contains("Pagina Precedente")) {
                shopManager.openCategory(player, category, page - 1);
            } else {
                Material material = clickedItem.getType();
                Map<String, Map<String, Object>> categories = configManager.getShopCategories();
                Map<String, Object> categoryConfig = categories.get(category);
                if (categoryConfig != null) {
                    Map<String, Object> items = categoryConfig.containsKey("pages") ?
                            ((Map<Integer, Map<String, Object>>) categoryConfig.get("pages")).get(page) :
                            (Map<String, Object>) categoryConfig.get("items");
                    if (items != null) {
                        for (String itemKey : items.keySet()) {
                            Map<String, Object> itemConfig = (Map<String, Object>) items.get(itemKey);
                            if (Material.getMaterial((String) itemConfig.get("material")) == material) {
                                double buy = (double) itemConfig.get("buy");
                                double sell = (double) itemConfig.getOrDefault("sell", -1.0);
                                String displayName = (String) itemConfig.get("name");
                                TransactionMenu transactionMenu = new TransactionMenu(plugin);
                                Inventory transactionInv = transactionMenu.createTransactionInventory(itemKey, material, displayName, buy, sell, buy >= 0, 0);
                                player.openInventory(transactionInv);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (holder instanceof TransactionHolder) {
            TransactionHolder transHolder = (TransactionHolder) holder;
            if (slot == 18) transHolder.setQuantity(transHolder.getQuantity() - 64);
            else if (slot == 19) transHolder.setQuantity(transHolder.getQuantity() - 10);
            else if (slot == 20) transHolder.setQuantity(transHolder.getQuantity() - 1);
            else if (slot == 24) transHolder.setQuantity(transHolder.getQuantity() + 1);
            else if (slot == 25) transHolder.setQuantity(transHolder.getQuantity() + 10);
            else if (slot == 26) transHolder.setQuantity(transHolder.getQuantity() + 64);
            if (transHolder.getQuantity() < 0) transHolder.setQuantity(0);
            if (slot == 18 || slot == 19 || slot == 20 || slot == 24 || slot == 25 || slot == 26) {
                Inventory newInv = new TransactionMenu(plugin).createTransactionInventory(
                        transHolder.getItemKey(), transHolder.getMaterial(),
                        getItemDisplayName(transHolder.getItemKey()),
                        getItemPrice(transHolder.getItemKey(), true),
                        getItemPrice(transHolder.getItemKey(), false),
                        transHolder.isBuy(),
                        transHolder.getQuantity()
                );
                player.openInventory(newInv);
            } else if (slot == 39) {
                if (transHolder.getQuantity() <= 0) {
                    player.sendMessage(configManager.getMessage("shop_invalid_quantity"));
                    player.closeInventory();
                    return;
                }
                if (transHolder.isBuy()) {
                    double price = getItemPrice(transHolder.getItemKey(), true);
                    double totalCost = price * transHolder.getQuantity();
                    if (price < 0) {
                        player.sendMessage(configManager.getMessage("shop_item_not_purchasable"));
                        player.closeInventory();
                        return;
                    }
                    if (!economy.has(player, totalCost)) {
                        player.sendMessage(configManager.getMessage("shop_not_enough_money"));
                        player.closeInventory();
                        return;
                    }
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(configManager.getMessage("shop_inventory_full"));
                        player.closeInventory();
                        return;
                    }
                    economy.withdrawPlayer(player, totalCost);
                    ItemStack item = createItemStack(transHolder.getItemKey(), transHolder.getMaterial(), transHolder.getQuantity());
                    player.getInventory().addItem(item);
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("quantity", String.valueOf(transHolder.getQuantity()));
                    String displayName = getItemDisplayName(transHolder.getItemKey());
                    placeholders.put("item", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', displayName != null ? displayName : transHolder.getItemKey())));
                    placeholders.put("price", String.format("%.2f", totalCost));
                    player.sendMessage(configManager.getMessage("shop_buy_success", placeholders));
                } else {
                    double price = getItemPrice(transHolder.getItemKey(), false);
                    double totalGain = price * transHolder.getQuantity();
                    if (price < 0) {
                        player.sendMessage(configManager.getMessage("shop_item_not_sellable"));
                        player.closeInventory();
                        return;
                    }
                    if (!hasEnoughItems(player, transHolder.getMaterial(), transHolder.getQuantity())) {
                        player.sendMessage(configManager.getMessage("shop_not_enough_items"));
                        player.closeInventory();
                        return;
                    }
                    removeItems(player, transHolder.getMaterial(), transHolder.getQuantity());
                    economy.depositPlayer(player, totalGain);
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("quantity", String.valueOf(transHolder.getQuantity()));
                    String displayName = getItemDisplayName(transHolder.getItemKey());
                    placeholders.put("item", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', displayName != null ? displayName : transHolder.getItemKey())));
                    placeholders.put("price", String.format("%.2f", totalGain));
                    player.sendMessage(configManager.getMessage("shop_sell_success", placeholders));
                }
                player.closeInventory();
            } else if (slot == 41) {
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof MainShopHolder ||
                event.getInventory().getHolder() instanceof CategoryHolder ||
                event.getInventory().getHolder() instanceof TransactionHolder) {
            event.setCancelled(true);
        }
    }

    private double getItemPrice(String itemKey, boolean isBuy) {
        Map<String, Map<String, Object>> categories = plugin.getConfigManager().getShopCategories();
        for (Map<String, Object> category : categories.values()) {
            Map<String, Object> items = category.containsKey("pages") ?
                    ((Map<Integer, Map<String, Object>>) category.get("pages")).values().stream().findFirst().orElse(null) :
                    (Map<String, Object>) category.get("items");
            if (items != null && items.containsKey(itemKey)) {
                Map<String, Object> itemConfig = (Map<String, Object>) items.get(itemKey);
                return isBuy ? (double) itemConfig.get("buy") : (double) itemConfig.getOrDefault("sell", -1.0);
            }
        }
        return -1.0;
    }

    private String getItemDisplayName(String itemKey) {
        Map<String, Map<String, Object>> categories = plugin.getConfigManager().getShopCategories();
        for (Map<String, Object> category : categories.values()) {
            Map<String, Object> items = category.containsKey("pages") ?
                    ((Map<Integer, Map<String, Object>>) category.get("pages")).values().stream().findFirst().orElse(null) :
                    (Map<String, Object>) category.get("items");
            if (items != null && items.containsKey(itemKey)) {
                Map<String, Object> itemConfig = (Map<String, Object>) items.get(itemKey);
                return (String) itemConfig.get("name");
            }
        }
        return null;
    }

    private boolean hasEnoughItems(Player player, Material material, int quantity) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count >= quantity;
    }

    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                int currentAmount = item.getAmount();
                if (currentAmount <= remaining) {
                    item.setAmount(0);
                    remaining -= currentAmount;
                } else {
                    item.setAmount(currentAmount - remaining);
                    remaining = 0;
                }
                if (remaining == 0) break;
            }
        }
    }

    private ItemStack createItemStack(String itemKey, Material material, int quantity) {
        ItemStack item = new ItemStack(material, quantity);
        ItemMeta meta = item.getItemMeta();
        String displayName = getItemDisplayName(itemKey);
        if (displayName != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }
        if (material == Material.SPAWNER) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
            CreatureSpawner spawner = (CreatureSpawner) blockStateMeta.getBlockState();
            EntityType spawnerType = getSpawnerTypeFromItemKey(itemKey);
            if (spawnerType != null) {
                spawner.setSpawnedType(spawnerType);
                blockStateMeta.setBlockState(spawner);
            }
        }
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