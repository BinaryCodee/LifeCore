package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;
import java.io.IOException;

public class PlayerStatsDeathListener implements Listener {
    private final LifeCore plugin;
    private final File playersFile;
    private final FileConfiguration playersConfig;

    public PlayerStatsDeathListener(LifeCore plugin) {
        this.plugin = plugin;
        this.playersFile = new File(plugin.getDataFolder(), "players.yml");
        this.playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        String victimUuid = victim.getUniqueId().toString();
        int deaths = playersConfig.getInt("players." + victimUuid + ".deaths", 0) + 1;
        playersConfig.set("players." + victimUuid + ".deaths", deaths);

        if (killer != null && killer != victim) {
            String killerUuid = killer.getUniqueId().toString();
            int kills = playersConfig.getInt("players." + killerUuid + ".kills", 0) + 1;
            playersConfig.set("players." + killerUuid + ".kills", kills);
        }

        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile salvare il file players.yml: " + e.getMessage());
        }
    }
}