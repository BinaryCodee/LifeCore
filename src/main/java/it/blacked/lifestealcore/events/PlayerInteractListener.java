package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PlayerInteractListener implements Listener {

    private final LifeCore plugin;

    public PlayerInteractListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Check for right click with heart item
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        Map<String, Object> heartConfig = plugin.getConfigManager().getHeartItemConfig();
        String material = (String) heartConfig.get("material");
        String displayName = ((String) heartConfig.get("name")).replace("&", "§");

        if (item.getType() == Material.valueOf(material) &&
                item.hasItemMeta() &&
                item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(displayName)) {

            int currentHearts = plugin.getHeartManager().getPlayerHearts(player.getUniqueId());
            int maxHearts = plugin.getConfigManager().getMaxHearts();

            if (currentHearts >= maxHearts) {
                player.sendMessage(plugin.getConfigManager().getMessage("max_hearts_reached"));
                return;
            }

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            plugin.getHeartManager().addPlayerHearts(player.getUniqueId(), 1);
            player.sendMessage(plugin.getConfigManager().getMessage("heart_redeemed"));
            event.setCancelled(true);
        }
    }
}