package it.blacked.lifestealcore.managers;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HeartItemManager {

    private final LifeCore plugin;
    private final Map<UUID, List<UUID>> heartItems = new HashMap<>();
    private final File heartItemsFile;
    private FileConfiguration heartItemsConfig;

    public HeartItemManager(LifeCore plugin) {
        this.plugin = plugin;
        this.heartItemsFile = new File(plugin.getDataFolder(), "heartitems.yml");
        loadHeartItems();
    }

    private void loadHeartItems() {
        if (!heartItemsFile.exists()) {
            try {
                heartItemsFile.getParentFile().mkdirs();
                heartItemsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossibile creare il file heartitems.yml!");
                e.printStackTrace();
            }
        }

        heartItemsConfig = YamlConfiguration.loadConfiguration(heartItemsFile);

        if (heartItemsConfig.getConfigurationSection("heartitems") != null) {
            for (String uuidStr : heartItemsConfig.getConfigurationSection("heartitems").getKeys(false)) {
                try {
                    UUID ownerUuid = UUID.fromString(uuidStr);
                    List<String> itemUuidStrings = heartItemsConfig.getStringList("heartitems." + uuidStr);

                    List<UUID> itemUuids = new ArrayList<>();
                    for (String itemUuidStr : itemUuidStrings) {
                        itemUuids.add(UUID.fromString(itemUuidStr));
                    }

                    heartItems.put(ownerUuid, itemUuids);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("UUID non valido nel file heartitems.yml: " + uuidStr);
                }
            }
        }
    }

    public void saveAllHeartItems() {
        for (Map.Entry<UUID, List<UUID>> entry : heartItems.entrySet()) {
            List<String> itemUuidStrings = new ArrayList<>();
            for (UUID itemUuid : entry.getValue()) {
                itemUuidStrings.add(itemUuid.toString());
            }

            heartItemsConfig.set("heartitems." + entry.getKey().toString(), itemUuidStrings);
        }

        try {
            heartItemsConfig.save(heartItemsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile salvare il file heartitems.yml!");
            e.printStackTrace();
        }
    }

    public void addHeartItem(UUID ownerUuid, UUID itemUuid) {
        List<UUID> playerHeartItems = heartItems.getOrDefault(ownerUuid, new ArrayList<>());
        playerHeartItems.add(itemUuid);
        heartItems.put(ownerUuid, playerHeartItems);
        saveAllHeartItems();
    }

    public void removeHeartItem(UUID ownerUuid, UUID itemUuid) {
        if (heartItems.containsKey(ownerUuid)) {
            List<UUID> playerHeartItems = heartItems.get(ownerUuid);
            playerHeartItems.remove(itemUuid);

            if (playerHeartItems.isEmpty()) {
                heartItems.remove(ownerUuid);
            }

            saveAllHeartItems();
        }
    }

    public boolean isHeartItem(UUID itemUuid) {
        for (List<UUID> items : heartItems.values()) {
            if (items.contains(itemUuid)) {
                return true;
            }
        }
        return false;
    }

    public UUID getHeartItemOwner(UUID itemUuid) {
        for (Map.Entry<UUID, List<UUID>> entry : heartItems.entrySet()) {
            if (entry.getValue().contains(itemUuid)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<UUID> getPlayerHeartItems(UUID ownerUuid) {
        return heartItems.getOrDefault(ownerUuid, new ArrayList<>());
    }
}