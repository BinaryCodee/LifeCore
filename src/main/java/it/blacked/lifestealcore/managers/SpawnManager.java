package it.blacked.lifestealcore.managers;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnManager {

    private final LifeCore plugin;
    private final File spawnFile;
    private FileConfiguration spawnConfig;
    private Location spawnLocation;

    public SpawnManager(LifeCore plugin) {
        this.plugin = plugin;
        this.spawnFile = new File(plugin.getDataFolder(), "spawn.yml");
        loadSpawnLocation();
    }

    private void loadSpawnLocation() {
        if (!spawnFile.exists()) {
            try {
                spawnFile.getParentFile().mkdirs();
                spawnFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossibile creare il file spawn.yml!");
                e.printStackTrace();
            }
        }

        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);

        if (spawnConfig.contains("spawn.world")) {
            String worldName = spawnConfig.getString("spawn.world");
            World world = Bukkit.getWorld(worldName);

            if (world != null) {
                double x = spawnConfig.getDouble("spawn.x");
                double y = spawnConfig.getDouble("spawn.y");
                double z = spawnConfig.getDouble("spawn.z");
                float yaw = (float) spawnConfig.getDouble("spawn.yaw");
                float pitch = (float) spawnConfig.getDouble("spawn.pitch");

                spawnLocation = new Location(world, x, y, z, yaw, pitch);
                plugin.getLogger().info("Spawn salvato e caricato.");
            } else {
                plugin.getLogger().warning("Impossibile caricare lo spawn nel mondo: '" + worldName + "', mondo non trovato!");
                spawnLocation = null;
            }
        } else {
            plugin.getLogger().info("Spawn non trovato");
            spawnLocation = null;
        }
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location.clone();
        spawnConfig.set("spawn.world", location.getWorld().getName());
        spawnConfig.set("spawn.x", location.getX());
        spawnConfig.set("spawn.y", location.getY());
        spawnConfig.set("spawn.z", location.getZ());
        spawnConfig.set("spawn.yaw", location.getYaw());
        spawnConfig.set("spawn.pitch", location.getPitch());

        try {
            spawnConfig.save(spawnFile);
            plugin.getLogger().info("Spawn salvato.");
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile salvare lo spawn!");
            e.printStackTrace();
        }
    }

    public Location getSpawnLocation() {
        return spawnLocation != null ? spawnLocation.clone() : null;
    }
}