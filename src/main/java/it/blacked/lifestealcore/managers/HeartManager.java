package it.blacked.lifestealcore.managers;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeartManager {

    private final LifeCore plugin;
    private final Map<UUID, Integer> playerHearts = new HashMap<>();
    private final Map<UUID, Integer> suicideCounter = new HashMap<>();
    private final File heartsFile;
    private FileConfiguration heartsConfig;

    public HeartManager(LifeCore plugin) {
        this.plugin = plugin;
        this.heartsFile = new File(plugin.getDataFolder(), "hearts.yml");
        loadHearts();
    }

    private void loadHearts() {
        if (!heartsFile.exists()) {
            try {
                heartsFile.getParentFile().mkdirs();
                heartsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create hearts.yml file!");
                e.printStackTrace();
            }
        }

        heartsConfig = YamlConfiguration.loadConfiguration(heartsFile);

        if (heartsConfig.getConfigurationSection("hearts") != null) {
            for (String uuidStr : heartsConfig.getConfigurationSection("hearts").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    int hearts = heartsConfig.getInt("hearts." + uuidStr);
                    playerHearts.put(uuid, hearts);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in hearts.yml: " + uuidStr);
                }
            }
        }
    }

    public void saveAllHearts() {
        for (Map.Entry<UUID, Integer> entry : playerHearts.entrySet()) {
            heartsConfig.set("hearts." + entry.getKey().toString(), entry.getValue());
        }

        try {
            heartsConfig.save(heartsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save hearts.yml file!");
            e.printStackTrace();
        }
    }

    public int getPlayerHearts(UUID uuid) {
        return playerHearts.getOrDefault(uuid, plugin.getConfigManager().getDefaultHearts());
    }

    public void setPlayerHearts(UUID uuid, int hearts) {
        playerHearts.put(uuid, hearts);
        updatePlayerMaxHealth(uuid);
    }

    public void addPlayerHearts(UUID uuid, int amount) {
        int currentHearts = getPlayerHearts(uuid);
        int maxHearts = plugin.getConfigManager().getMaxHearts();

        int newHearts = Math.min(currentHearts + amount, maxHearts);
        setPlayerHearts(uuid, newHearts);
    }

    public void removePlayerHearts(UUID uuid, int amount) {
        int currentHearts = getPlayerHearts(uuid);
        int newHearts = Math.max(currentHearts - amount, 0);
        setPlayerHearts(uuid, newHearts);
    }

    public void updatePlayerMaxHealth(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            int hearts = getPlayerHearts(uuid);
            double maxHealth = hearts * 2.0;
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);

            if (player.getHealth() > maxHealth) {
                player.setHealth(maxHealth);
            }
        }
    }

    public void updateAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerMaxHealth(player.getUniqueId());
        }
    }

    public void incrementSuicideCounter(UUID uuid) {
        int count = suicideCounter.getOrDefault(uuid, 0) + 1;
        suicideCounter.put(uuid, count);

        if (count >= plugin.getConfigManager().getSuicideBanCount()) {
            suicideCounter.remove(uuid);
            plugin.getBanManager().banPlayer(uuid, plugin.getConfigManager().getSuicideBanTime());
        }
    }

    public int getSuicideCounter(UUID uuid) {
        return suicideCounter.getOrDefault(uuid, 0);
    }

    public void resetSuicideCounter(UUID uuid) {
        suicideCounter.remove(uuid);
    }

    public Map<UUID, Integer> getAllHearts() {
        return new HashMap<>(playerHearts);
    }
}