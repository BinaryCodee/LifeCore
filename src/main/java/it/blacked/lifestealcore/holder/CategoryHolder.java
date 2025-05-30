package it.blacked.lifestealcore.holder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CategoryHolder implements InventoryHolder {
    private final String category;
    private final int page;

    public CategoryHolder(String category, int page) {
        this.category = category;
        this.page = page;
    }

    public String getCategory() {
        return category;
    }

    public int getPage() {
        return page;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}