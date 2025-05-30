package it.blacked.lifestealcore.holder;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TransactionHolder implements InventoryHolder {
    private final String itemKey;
    private final Material material;
    private final boolean isBuy;
    private int quantity;

    public TransactionHolder(String itemKey, Material material, boolean isBuy) {
        this(itemKey, material, isBuy, 0);
    }

    public TransactionHolder(String itemKey, Material material, boolean isBuy, int quantity) {
        this.itemKey = itemKey;
        this.material = material;
        this.isBuy = isBuy;
        this.quantity = quantity;
    }

    public String getItemKey() {
        return itemKey;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}