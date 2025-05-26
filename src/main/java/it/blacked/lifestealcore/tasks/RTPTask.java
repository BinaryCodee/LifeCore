package it.blacked.lifestealcore.tasks;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.ConfigManager;
import it.blacked.lifestealcore.managers.RTPManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RTPTask extends BukkitRunnable {

    private final LifeCore plugin;
    private final Player player;
    private final String worldName;
    private final ConfigManager configManager;
    private final Random random;
    private int attempts;
    private int countdown;

    public RTPTask(LifeCore plugin, Player player, String worldName, ConfigManager configManager) {
        this.plugin = plugin;
        this.player = player;
        this.worldName = worldName;
        this.configManager = configManager;
        this.random = new Random();
        this.attempts = 0;
        this.countdown = configManager.getRtpDelay();
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }

        RTPManager rtpManager = LifeCore.getRtpManager();

        if (countdown > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(countdown));
            player.sendMessage(configManager.getMessage("rtp_countdown", placeholders));
            player.sendTitle(
                    configManager.getMessage("rtp_title", placeholders),
                    configManager.getMessage("rtp_countdown_subtitle", placeholders),
                    10, 20, 10
            );

            if (configManager.isSpawnCountdownSoundEnabled()) {
                try {
                    Sound sound = Sound.valueOf(configManager.getSpawnCountdownSound());
                    player.playSound(player.getLocation(), sound,
                            (float) configManager.getSpawnCountdownSoundVolume(),
                            (float) configManager.getSpawnCountdownSoundPitch());
                } catch (IllegalArgumentException ignored) {
                }
            }

            countdown--;
            return;
        }

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            player.sendMessage(configManager.getMessage("rtp_failed"));
            player.sendTitle(
                    configManager.getMessage("rtp_failed_title"),
                    configManager.getMessage("rtp_failed_subtitle"),
                    10, 20, 10
            );
            rtpManager.cancelRTP(player);
            cancel();
            return;
        }

        Map<String, Object> worldConfig = configManager.getRtpWorldsConfig().getOrDefault(worldName, new HashMap<>());
        if (!(boolean) worldConfig.getOrDefault("enabled", true)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("world", worldName);
            player.sendMessage(configManager.getMessage("rtp_world_not_found", placeholders));
            player.sendTitle(
                    configManager.getMessage("rtp_failed_title"),
                    configManager.getMessage("rtp_failed_subtitle"),
                    10, 20, 10
            );
            rtpManager.cancelRTP(player);
            cancel();
            return;
        }

        if (attempts >= configManager.getRtpMaxAttempts()) {
            player.sendMessage(configManager.getMessage("rtp_failed"));
            player.sendTitle(
                    configManager.getMessage("rtp_failed_title"),
                    configManager.getMessage("rtp_failed_subtitle"),
                    10, 20, 10
            );
            rtpManager.cancelRTP(player);
            cancel();
            return;
        }

        attempts++;
        Location location = findSafeLocation(world, worldConfig);

        if (location != null) {
            world.loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4, true);
            player.teleport(location);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("x", String.valueOf(location.getBlockX()));
            placeholders.put("y", String.valueOf(location.getBlockY()));
            placeholders.put("z", String.valueOf(location.getBlockZ()));
            placeholders.put("world", world.getName());
            player.sendMessage(configManager.getMessage("rtp_success", placeholders));
            player.sendTitle(
                    configManager.getMessage("rtp_success_title", placeholders),
                    configManager.getMessage("rtp_success_subtitle", placeholders),
                    10, 20, 10
            );

            if (configManager.isRtpSoundEnabled()) {
                try {
                    Sound sound = Sound.valueOf(configManager.getRtpSound());
                    player.playSound(location, sound,
                            (float) configManager.getRtpSoundVolume(),
                            (float) configManager.getRtpSoundPitch());
                } catch (IllegalArgumentException ignored) {
                }
            }

            rtpManager.setCooldown(player);
            rtpManager.cancelRTP(player);
            cancel();
        }
    }

    private Location findSafeLocation(World world, Map<String, Object> worldConfig) {
        int minRadius = (int) worldConfig.getOrDefault("min_radius", configManager.getRtpMinRadius());
        int maxRadius = (int) worldConfig.getOrDefault("max_radius", configManager.getRtpMaxRadius());
        int minY = (int) worldConfig.getOrDefault("min_y", 64);
        int maxY = (int) worldConfig.getOrDefault("max_y", 256);

        int x = random.nextInt(maxRadius - minRadius) + minRadius;
        int z = random.nextInt(maxRadius - minRadius) + minRadius;
        if (random.nextBoolean()) x = -x;
        if (random.nextBoolean()) z = -z;

        int y = world.getHighestBlockYAt(x, z);
        if (y < minY || y > maxY) {
            y = random.nextInt(maxY - minY) + minY;
        }

        Location location = new Location(world, x + 0.5, y + 1, z + 0.5);

        if (isSafeLocation(location, worldConfig)) {
            return location;
        }

        return null;
    }

    private boolean isSafeLocation(Location location, Map<String, Object> worldConfig) {
        World world = location.getWorld();
        if (world == null) return false;

        Block block = location.getBlock();
        Block above = block.getRelative(0, 1, 0);
        Block below = block.getRelative(0, -1, 0);

        if (!block.getType().isAir() || !above.getType().isAir()) {
            return false;
        }

        if (below.getType().isAir() || below.isLiquid()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        List<String> safeBlocks = (List<String>) worldConfig.getOrDefault("safe_blocks", new ArrayList<>());
        @SuppressWarnings("unchecked")
        List<String> unsafeBlocks = (List<String>) worldConfig.getOrDefault("unsafe_blocks", new ArrayList<>());

        Material belowType = below.getType();
        if (unsafeBlocks.contains(belowType.name())) {
            return false;
        }

        if (!safeBlocks.isEmpty() && !safeBlocks.contains(belowType.name())) {
            return false;
        }

        return true;
    }
}