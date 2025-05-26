package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.commands.SpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private final LifeCore plugin;
    private final Map<UUID, UUID> lastKillerMap = new HashMap<>();

    public PlayerDeathListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer != null && killer != victim) {
            lastKillerMap.put(victim.getUniqueId(), killer.getUniqueId());
            int heartsToTransfer = plugin.getConfigManager().getHeartTransferCount();
            plugin.getHeartManager().removePlayerHearts(victim.getUniqueId(), heartsToTransfer);
            boolean success = plugin.getHeartManager().addPlayerHearts(killer.getUniqueId(), heartsToTransfer);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("victim", victim.getName());
            placeholders.put("killer", killer.getName());
            placeholders.put("hearts", String.valueOf(heartsToTransfer));
            victim.sendMessage(plugin.getConfigManager().getMessage("heart_lost", placeholders));
            if (success) {
                killer.sendMessage(plugin.getConfigManager().getMessage("heart_gained", placeholders));
            } else {
                killer.sendMessage(plugin.getConfigManager().getMessage("hearts_limit_reached",
                        Map.of("max_hearts", String.valueOf(plugin.getConfigManager().getMaxHearts()))));
            }
            checkForBan(victim);
        } else {
            int suicideHeartLoss = plugin.getConfigManager().getSuicideHeartLoss();
            if (suicideHeartLoss > 0) {
                plugin.getHeartManager().removePlayerHearts(victim.getUniqueId(), suicideHeartLoss);
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("hearts", String.valueOf(suicideHeartLoss));
                victim.sendMessage(plugin.getConfigManager().getMessage("heart_lost_suicide", placeholders));
                checkForBan(victim);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (victim.isOnline()) {
                    SpawnCommand spawnCommand = new SpawnCommand(plugin);
                    spawnCommand.teleportToSpawn(victim);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    private void checkForBan(Player player) {
        int currentHearts = plugin.getHeartManager().getPlayerHearts(player.getUniqueId());

        if (currentHearts <= 0) {
            String banTime = plugin.getConfigManager().getDeathBanTime();
            plugin.getBanManager().banPlayer(player.getUniqueId(), banTime);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", banTime);
            player.sendMessage(plugin.getConfigManager().getMessage("ban_death", placeholders));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    SpawnCommand spawnCommand = new SpawnCommand(plugin);
                    spawnCommand.teleportToSpawn(player);
                }
            }, 1L);
        }
    }
}