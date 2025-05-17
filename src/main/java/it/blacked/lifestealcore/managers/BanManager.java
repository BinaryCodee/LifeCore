package it.blacked.lifestealcore.managers;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BanManager {

    private final LifeCore plugin;
    private final Map<UUID, Long> bannedPlayers = new HashMap<>();
    private final File bansFile;
    private FileConfiguration bansConfig;

    public BanManager(LifeCore plugin) {
        this.plugin = plugin;
        this.bansFile = new File(plugin.getDataFolder(), "bans.yml");
        loadBans();
    }

    private void loadBans() {
        if (!bansFile.exists()) {
            try {
                bansFile.getParentFile().mkdirs();
                bansFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossibile creare il file bans.yml!");
                e.printStackTrace();
            }
        }

        bansConfig = YamlConfiguration.loadConfiguration(bansFile);

        if (bansConfig.getConfigurationSection("bans") != null) {
            for (String uuidStr : bansConfig.getConfigurationSection("bans").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    long expiry = bansConfig.getLong("bans." + uuidStr);

                    if (expiry > System.currentTimeMillis()) {
                        bannedPlayers.put(uuid, expiry);
                    } else {
                        bansConfig.set("bans." + uuidStr, null);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("UUID non valido nel file bans.yml: " + uuidStr);
                }
            }
        }

        try {
            bansConfig.save(bansFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile salvare il file bans.yml!");
            e.printStackTrace();
        }
    }

    public void saveAllBans() {
        for (Map.Entry<UUID, Long> entry : bannedPlayers.entrySet()) {
            bansConfig.set("bans." + entry.getKey().toString(), entry.getValue());
        }

        try {
            bansConfig.save(bansFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile salvare il file bans.yml!");
            e.printStackTrace();
        }
    }

    public void banPlayer(UUID uuid, String time) {
        long duration = TimeUtils.parseDuration(time);
        long expiry = System.currentTimeMillis() + duration;

        bannedPlayers.put(uuid, expiry);
        saveAllBans();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", time);
            player.sendMessage(plugin.getConfigManager().getMessage("banned_message", placeholders));
        }
    }

    public void unbanPlayer(UUID uuid) {
        bannedPlayers.remove(uuid);
        bansConfig.set("bans." + uuid.toString(), null);
        saveAllBans();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            player.sendMessage(plugin.getConfigManager().getMessage("unbanned_message"));

            int hearts = plugin.getConfigManager().getStartingHeartsAfterUnban();
            plugin.getHeartManager().setPlayerHearts(uuid, hearts);
        }
    }

    public boolean isPlayerBanned(UUID uuid) {
        if (!bannedPlayers.containsKey(uuid)) {
            return false;
        }

        long expiry = bannedPlayers.get(uuid);
        if (System.currentTimeMillis() > expiry) {
            bannedPlayers.remove(uuid);
            bansConfig.set("bans." + uuid.toString(), null);
            saveAllBans();
            return false;
        }

        return true;
    }

    public long getBanExpiry(UUID uuid) {
        return bannedPlayers.getOrDefault(uuid, 0L);
    }

    public String getRemainingBanTime(UUID uuid) {
        if (!isPlayerBanned(uuid)) {
            return "0";
        }

        long expiry = bannedPlayers.get(uuid);
        long remaining = expiry - System.currentTimeMillis();

        return TimeUtils.formatDuration(remaining);
    }
}