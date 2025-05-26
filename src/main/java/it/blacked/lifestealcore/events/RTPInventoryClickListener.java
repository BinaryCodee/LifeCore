package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.gui.RTPMenu;
import it.blacked.lifestealcore.managers.ConfigManager;
import it.blacked.lifestealcore.managers.RTPManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

public class RTPInventoryClickListener implements Listener {

    private final LifeCore plugin;
    private final ConfigManager configManager;

    public RTPInventoryClickListener(LifeCore plugin) {
        this.plugin = plugin;
        this.configManager = ConfigManager.getConfigManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = ChatColor.stripColor(event.getView().getTitle());
        String rtpTitle = ChatColor.stripColor(configManager.getRtpMenuTitle());

        if (!title.equals(rtpTitle)) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
            return;
        }

        RTPManager rtpManager = LifeCore.getRtpManager();
        if (rtpManager == null) {
            player.sendMessage(ChatColor.RED + "RTP system is not available right now. Please try again later.");
            return;
        }

        int slot = event.getRawSlot();
        RTPMenu rtpMenu = new RTPMenu(plugin, player, configManager);
        String worldName = rtpMenu.getWorldNameFromSlot(slot);

        if (worldName.isEmpty()) {
            return;
        }

        if (rtpManager.isPlayerInRTP(player)) {
            player.sendMessage(configManager.getMessage("rtp_already_in_progress"));
            return;
        }

        long cooldown = rtpManager.getCooldown(player);
        if (cooldown > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(cooldown));
            player.sendMessage(configManager.getMessage("rtp_cooldown", placeholders));
            return;
        }

        if (plugin.getServer().getWorld(worldName) == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("world", worldName);
            player.sendMessage(configManager.getMessage("rtp_world_not_found", placeholders));
            return;
        }

        if (configManager.isRtpEconomyEnabled()) {
            Economy economy = plugin.getEconomy();
            if (economy != null) {
                double cost = configManager.getRtpCost();
                if (cost > 0) {
                    if (!economy.has(player, cost)) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("cost", String.valueOf(cost));
                        player.sendMessage(configManager.getMessage("rtp_not_enough_money", placeholders));
                        return;
                    }
                    economy.withdrawPlayer(player, cost);
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("cost", String.valueOf(cost));
                    player.sendMessage(configManager.getMessage("rtp_money_taken", placeholders));
                }
            }
        }

        player.closeInventory();
        rtpManager.startRTP(player, worldName);
    }
}