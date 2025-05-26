package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.ConfigManager;
import it.blacked.lifestealcore.managers.SpawnManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PlayerSpawnListener implements Listener {

    private final LifeCore plugin;
    private final SpawnManager spawnManager;
    private final ConfigManager configManager;

    public PlayerSpawnListener(LifeCore plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.spawnManager = plugin.getSpawnManager();
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            teleportToSpawn(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location spawnLocation = spawnManager.getSpawnLocation();
        Player player = event.getPlayer();
        if (spawnLocation != null) {
            event.setRespawnLocation(spawnLocation);
            teleportToSpawn(event.getPlayer());
        } else {
            player.sendMessage(configManager.getMessage("spawn_not_set"));
        }
    }

    private void teleportToSpawn(Player player) {
        Location spawnLocation = spawnManager.getSpawnLocation();
        if (spawnLocation == null) {
            player.sendMessage(configManager.getMessage("spawn_not_set"));
            return;
        }
        int spawnDelay = configManager.getSpawnDelay();
        if (spawnDelay <= 0) {
            performTeleport(player, spawnLocation);
        } else {
            new BukkitRunnable() {
                int countdown = spawnDelay;
                Location initialLocation = player.getLocation();

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    if (hasPlayerMoved(player, initialLocation)) {
                        player.sendMessage(configManager.getMessage("teleport_cancelled_movement"));
                        cancel();
                        return;
                    }
                    if (countdown <= 0) {
                        performTeleport(player, spawnLocation);
                        cancel();
                        return;
                    }
                    if (configManager.isSpawnCountdownTitleEnabled()) {
                        player.sendTitle(
                                configManager.getMessage("teleport_countdown_title",
                                        new HashMap<String, String>() {{
                                            put("seconds", String.valueOf(countdown));
                                        }}),
                                configManager.getMessage("teleport_countdown_subtitle"),
                                0, 20, 0
                        );
                    }
                    if (configManager.isSpawnCountdownSoundEnabled()) {
                        try {
                            player.playSound(
                                    player.getLocation(),
                                    Sound.valueOf(configManager.getSpawnCountdownSound()),
                                    (float) configManager.getSpawnCountdownSoundVolume(),
                                    (float) configManager.getSpawnCountdownSoundPitch()
                            );
                        } catch (IllegalArgumentException ignored) {}
                    }
                    countdown--;
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    private void performTeleport(Player player, Location spawnLocation) {
        player.teleport(spawnLocation);
        if (configManager.isSpawnTeleportTitleEnabled()) {
            player.sendTitle(
                    configManager.getMessage("teleport_complete_title"),
                    configManager.getMessage("teleport_complete_subtitle"),
                    10, 70, 20
            );
        }
        if (configManager.isSpawnTeleportSoundEnabled()) {
            try {
                player.playSound(
                        spawnLocation,
                        Sound.valueOf(configManager.getSpawnTeleportSound()),
                        (float) configManager.getSpawnTeleportSoundVolume(),
                        (float) configManager.getSpawnTeleportSoundPitch()
                );
            } catch (IllegalArgumentException ignored) {}
        }
        player.sendMessage(configManager.getMessage("teleported_to_spawn"));
    }

    private boolean hasPlayerMoved(Player player, Location initialLocation) {
        Location currentLocation = player.getLocation();
        return currentLocation.getBlockX() != initialLocation.getBlockX() ||
                currentLocation.getBlockZ() != initialLocation.getBlockZ() ||
                currentLocation.getBlockY() != initialLocation.getBlockY();
    }
}