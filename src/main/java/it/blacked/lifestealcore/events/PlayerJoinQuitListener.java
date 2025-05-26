package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.commands.SpawnCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PlayerJoinQuitListener implements Listener {

    private final LifeCore plugin;

    public PlayerJoinQuitListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getHeartManager().hasPlayerHearts(player.getUniqueId())) {
            plugin.getHeartManager().setPlayerHearts(player.getUniqueId(), plugin.getConfigManager().getDefaultHearts());
        }
        plugin.getHeartManager().updatePlayerMaxHealth(player);
        if (plugin.getBanManager().isPlayerBanned(player.getUniqueId())) {
            long remainingTime = Long.parseLong(String.valueOf(plugin.getBanManager().getRemainingBanTime(player.getUniqueId())));
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", plugin.getBanManager().formatTime(remainingTime));
            player.sendMessage(plugin.getConfigManager().getMessage("banned_join_message", placeholders));
            if (plugin.getConfigManager().isBanTitleEnabled()) {
                String title = plugin.getConfigManager().getMessage("banned_title", placeholders);
                String subtitle = plugin.getConfigManager().getMessage("banned_subtitle", placeholders);

                player.sendTitle(
                        ChatColor.translateAlternateColorCodes('&', title),
                        ChatColor.translateAlternateColorCodes('&', subtitle),
                        20, 100, 20
                );
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    SpawnCommand spawnCommand = new SpawnCommand(plugin);
                    spawnCommand.teleportToSpawn(player);
                }
            }.runTaskLater(plugin, 5L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SpawnCommand spawnCommand = new SpawnCommand(plugin);
        spawnCommand.cancelTeleport(player);
    }
}