package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnCommand implements CommandExecutor {

    private final LifeCore plugin;
    private final HashMap<UUID, BukkitRunnable> spawnTasks = new HashMap<>();

    public SpawnCommand(LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_only"));
            return true;
        }

        Player player = (Player) sender;

        Location spawnLocation = plugin.getSpawnManager().getSpawnLocation();
        if (spawnLocation == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("spawn_not_set"));
            return true;
        }

        if (plugin.getBanManager().isPlayerBanned(player.getUniqueId())) {
            teleportToSpawn(player);
            return true;
        }

        if (player.isDead()) {
            teleportToSpawn(player);
            return true;
        }

        if (player.hasPermission(plugin.getConfigManager().getCommandPermission("lifecore_spawn_bypass"))) {
            teleportToSpawn(player);
            return true;
        }
        int spawnDelay = plugin.getConfigManager().getSpawnDelay();
        cancelTeleport(player);
        if (spawnDelay <= 0) {
            teleportToSpawn(player);
            return true;
        }

        player.sendMessage(plugin.getConfigManager().getMessage("teleport_countdown_started",
                Map.of("seconds", String.valueOf(spawnDelay))));

        BukkitRunnable teleportTask = new BukkitRunnable() {
            private int remainingSeconds = spawnDelay;
            private final Location startLocation = player.getLocation().clone();

            @Override
            public void run() {
                if (hasPlayerMoved(player, startLocation)) {
                    player.sendMessage(plugin.getConfigManager().getMessage("teleport_cancelled_movement"));
                    cancelTeleport(player);
                    return;
                }

                if (remainingSeconds <= 0) {
                    teleportToSpawn(player);
                    cancelTeleport(player);
                    return;
                }

                String title = plugin.getConfigManager().getMessage("teleport_countdown_title",
                        Map.of("seconds", String.valueOf(remainingSeconds)));
                String subtitle = plugin.getConfigManager().getMessage("teleport_countdown_subtitle");
                player.sendTitle(title, subtitle, 5, 20, 5);

                if (plugin.getConfigManager().isSpawnCountdownSoundEnabled()) {
                    Sound sound = Sound.valueOf(plugin.getConfigManager().getSpawnCountdownSound());
                    float volume = (float) plugin.getConfigManager().getSpawnCountdownSoundVolume();
                    float pitch = (float) plugin.getConfigManager().getSpawnCountdownSoundPitch();
                    player.playSound(player.getLocation(), sound, volume, pitch);
                }

                remainingSeconds--;
            }
        };

        spawnTasks.put(player.getUniqueId(), teleportTask);
        teleportTask.runTaskTimer(plugin, 0L, 20L);

        return true;
    }

    public void teleportToSpawn(Player player) {
        Location spawnLocation = plugin.getSpawnManager().getSpawnLocation();
        if (spawnLocation == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("spawn_not_set"));
            return;
        }

        player.teleport(spawnLocation);
        player.sendMessage(plugin.getConfigManager().getMessage("teleported_to_spawn"));

        if (plugin.getConfigManager().isSpawnTeleportSoundEnabled()) {
            Sound sound = Sound.valueOf(plugin.getConfigManager().getSpawnTeleportSound());
            float volume = (float) plugin.getConfigManager().getSpawnTeleportSoundVolume();
            float pitch = (float) plugin.getConfigManager().getSpawnTeleportSoundPitch();
            player.playSound(player.getLocation(), sound, volume, pitch);
        }

        if (plugin.getConfigManager().isSpawnTeleportTitleEnabled()) {
            String title = plugin.getConfigManager().getMessage("teleport_complete_title");
            String subtitle = plugin.getConfigManager().getMessage("teleport_complete_subtitle");
            player.sendTitle(title, subtitle, 10, 40, 10);
        }
    }

    public void cancelTeleport(Player player) {
        BukkitRunnable task = spawnTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    private boolean hasPlayerMoved(Player player, Location startLocation) {
        Location currentLocation = player.getLocation();
        return currentLocation.getWorld() != startLocation.getWorld() ||
                currentLocation.getBlockX() != startLocation.getBlockX() ||
                currentLocation.getBlockY() != startLocation.getBlockY() ||
                currentLocation.getBlockZ() != startLocation.getBlockZ();
    }
}