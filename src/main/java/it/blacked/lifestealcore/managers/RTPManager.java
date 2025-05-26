package it.blacked.lifestealcore.managers;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.tasks.RTPTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RTPManager {

    private final LifeCore plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Long> cooldowns;
    private final Set<UUID> playersInRTP;
    private final Map<UUID, BukkitTask> rtpTasks;
    private String title;

    public RTPManager(LifeCore plugin) {
        this.plugin = plugin;
        this.configManager = ConfigManager.getConfigManager();
        this.cooldowns = new HashMap<>();
        this.playersInRTP = new HashSet<>();
        this.rtpTasks = new HashMap<>();
    }

    public void startRTP(Player player, String worldName) {
        if (!configManager.isRtpEnabled()) {
            player.sendMessage(configManager.getMessage("rtp_failed"));
            return;
        }
        if (plugin.getServer().getWorld(worldName) == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("world", worldName);
            player.sendMessage(configManager.getMessage("rtp_world_not_found", placeholders));
            return;
        }
        playersInRTP.add(player.getUniqueId());
        player.sendMessage(configManager.getMessage("rtp_searching"));
        BukkitTask task = new RTPTask(plugin, player, worldName, configManager).runTaskTimer(plugin, 0L, 20L);
        rtpTasks.put(player.getUniqueId(), task);
    }

    public void cancelRTP(Player player) {
        UUID uuid = player.getUniqueId();
        playersInRTP.remove(uuid);
        BukkitTask task = rtpTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    public boolean isPlayerInRTP(Player player) {
        return playersInRTP.contains(player.getUniqueId());
    }

    public long getCooldown(Player player) {
        Long cooldownEnd = cooldowns.get(player.getUniqueId());
        if (cooldownEnd == null) {
            return 0;
        }
        long timeLeft = (cooldownEnd - System.currentTimeMillis()) / 1000;
        return timeLeft > 0 ? timeLeft : 0;
    }

    public void cleanup() {
        for (BukkitTask task : rtpTasks.values()) {
            task.cancel();
        }
        rtpTasks.clear();
        playersInRTP.clear();
        cooldowns.clear();
    }

    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (configManager.getRtpCooldown() * 1000L));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}