package it.blacked.lifestealcore.managers;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.gui.ShopMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShopManager {
    private final LifeCore plugin;
    private final ShopMenu shopMenu;

    public ShopManager(LifeCore plugin) {
        this.plugin = plugin;
        this.shopMenu = new ShopMenu(plugin);
    }

    public void openMainShop(Player player) {
        Inventory inv = shopMenu.createMainShopInventory();
        player.openInventory(inv);
    }

    public void openCategory(Player player, String category, int page) {
        Inventory inv = shopMenu.createCategoryInventory(category, page);
        if (inv != null) {
            player.openInventory(inv);
        }
    }
}