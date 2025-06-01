package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.holder.SellGuiHolder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SellGuiListener implements Listener {
    private final LifeCore plugin;
    private final Economy economy;

    public SellGuiListener(LifeCore lifeCore) {
        plugin = lifeCore;
        economy = plugin.getEconomy();
    }

    @EventHandler
    void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SellGuiHolder)) return;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof SellGuiHolder)) return;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof SellGuiHolder)) return;
        if (!(event.getView().getPlayer() instanceof Player)) return;

        Player player = (Player) event.getView().getPlayer();
        Inventory inv = event.getInventory();
        Map<Material, Integer> itemQuantities = new HashMap<>();
        Map<String, Map.Entry<String, Double>> sellableItems = new HashMap<>();
        Map<Material, Integer> nonSellableItems = new HashMap<>();

        ItemStack[] contents = inv.getContents();
        if (contents == null) return;
        for (ItemStack item : contents) {
            if (item != null && item.getType() != Material.AIR) {
                itemQuantities.merge(item.getType(), item.getAmount(), Integer::sum);
            }
        }

        try {
            Map<String, Map<String, Object>> categories = plugin.getConfigManager().getShopCategories();
            if (categories == null) {
                player.sendMessage(ChatColor.RED + "Errore: Impossibile caricare le categorie dello shop.");
                return;
            }

            for (Map.Entry<Material, Integer> entry : itemQuantities.entrySet()) {
                Material material = entry.getKey();
                int quantity = entry.getValue();
                boolean isSellable = false;
                String itemKey = null;
                double sellPrice = -1.0;
                String displayName = null;

                for (Map.Entry<String, Map<String, Object>> category : categories.entrySet()) {
                    Map<String, Object> items = category.getValue().containsKey("pages") ?
                            ((Map<Integer, Map<String, Object>>) category.getValue().get("pages")).values().stream()
                                    .findFirst().orElse(new HashMap<>()) :
                            (Map<String, Object>) category.getValue().get("items");
                    for (Map.Entry<String, Object> itemEntry : items.entrySet()) {
                        Map<String, Object> itemConfig = (Map<String, Object>) itemEntry.getValue();
                        String materialName = (String) itemConfig.get("material");
                        if (materialName != null && materialName.equalsIgnoreCase(material.name())) {
                            sellPrice = (double) itemConfig.getOrDefault("sell", -1.0);
                            if (sellPrice >= 0) {
                                isSellable = true;
                                itemKey = itemEntry.getKey();
                                displayName = (String) itemConfig.get("name");
                                break;
                            }
                        }
                    }
                    if (isSellable) break;
                }

                if (isSellable && itemKey != null) {
                    sellableItems.put(itemKey, new HashMap.SimpleEntry<>(
                            displayName != null ? ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', displayName)) : itemKey,
                            sellPrice));
                } else {
                    nonSellableItems.put(material, quantity);
                }
            }

            double totalGain = 0.0;
            for (Map.Entry<String, Map.Entry<String, Double>> sellable : sellableItems.entrySet()) {
                String itemKey = sellable.getKey();
                String displayName = sellable.getValue().getKey();
                double sellPrice = sellable.getValue().getValue();
                Material material = Material.getMaterial(getMaterialFromItemKey(itemKey));
                int quantity = itemQuantities.getOrDefault(material, 0);
                if (quantity > 0) {
                    double gain = sellPrice * quantity;
                    totalGain += gain;

                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("quantity", String.valueOf(quantity));
                    placeholders.put("item", displayName);
                    placeholders.put("price", String.format("%.2f", gain));
                    String message = plugin.getConfigManager().getMessage("shop_sell_success", placeholders);
                    if (message != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            }

            if (totalGain > 0) {
                if (economy != null) {
                    economy.depositPlayer(player, totalGain);
                } else {
                    player.sendMessage(ChatColor.RED + "Errore: Sistema economico non disponibile.");
                }
            }

            for (Map.Entry<Material, Integer> nonSellable : nonSellableItems.entrySet()) {
                ItemStack item = new ItemStack(nonSellable.getKey(), nonSellable.getValue());
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), item);
                } else {
                    player.getInventory().addItem(item);
                }
                String message = plugin.getConfigManager().getMessage("shop_item_not_sellable");
                if (message != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }

            inv.clear();
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Errore durante la vendita degli oggetti.");
        }
    }

    private String getMaterialFromItemKey(String itemKey) {
        Map<String, Map<String, Object>> categories = plugin.getConfigManager().getShopCategories();
        if (categories == null) return null;
        for (Map<String, Object> category : categories.values()) {
            Map<String, Object> items = category.containsKey("pages") ?
                    ((Map<Integer, Map<String, Object>>) category.get("pages")).values().stream().findFirst().orElse(new HashMap<>()) :
                    (Map<String, Object>) category.get("items");
            if (items.containsKey(itemKey)) {
                return (String) ((Map<String, Object>) items.get(itemKey)).get("material");
            }
        }
        return null;
    }
}