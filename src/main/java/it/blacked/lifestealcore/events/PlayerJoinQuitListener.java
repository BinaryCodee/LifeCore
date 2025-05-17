package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerJoinQuitListener implements Listener {

    private final LifeCore plugin;

    public PlayerJoinQuitListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (plugin.getBanManager().isPlayerBanned(uuid)) {
            String remainingTime = plugin.getBanManager().getRemainingBanTime(uuid);
            player.sendMessage(plugin.getConfigManager().getMessage("banned_message")
                    .replace("{time}", remainingTime));
        }

        plugin.getHeartManager().updatePlayerMaxHealth(uuid);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        plugin.getHeartManager().saveAllHearts();
    }
}