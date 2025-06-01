package it.blacked.lifestealcore.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class CreativeDestroySlotListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() != GameMode.CREATIVE) return;
        if (event.getInventory().getType() != InventoryType.CREATIVE) return;
        if (event.getRawSlot() != 45) return;
        ClickType clickType = event.getClick();
        if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
            player.getInventory().clear();
            event.setCancelled(true);
        } else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
            event.setCancelled(true);
        }
    }

}
