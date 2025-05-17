package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private final LifeCore plugin;

    public PlayerDeathListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || killer.getUniqueId().equals(victim.getUniqueId())) {
            handleSuicide(victim);
            return;
        }

        handleKill(killer, victim);
    }

    private void handleSuicide(Player player) {
        UUID uuid = player.getUniqueId();
        plugin.getHeartManager().incrementSuicideCounter(uuid);
        int suicideCount = plugin.getHeartManager().getSuicideCounter(uuid);
        int maxSuicides = plugin.getConfigManager().getSuicideBanCount();
        int remaining = maxSuicides - suicideCount;

        if (remaining > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("remaining", String.valueOf(remaining));

            player.sendMessage(plugin.getConfigManager().getMessage("suicide_warning", placeholders));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("suicide_banned"));
        }

        plugin.getHeartManager().removePlayerHearts(uuid, 1);

        if (plugin.getHeartManager().getPlayerHearts(uuid) <= 0) {
            player.sendMessage(plugin.getConfigManager().getMessage("no_hearts_remaining"));
            plugin.getBanManager().banPlayer(uuid, plugin.getConfigManager().getDeathBanTime());
        }
    }

    private void handleKill(Player killer, Player victim) {
        UUID killerUuid = killer.getUniqueId();
        UUID victimUuid = victim.getUniqueId();

        plugin.getHeartManager().resetSuicideCounter(killerUuid);
        plugin.getHeartManager().resetSuicideCounter(victimUuid);
        plugin.getHeartManager().addPlayerHearts(killerUuid, 1);

        Map<String, String> killerPlaceholders = new HashMap<>();
        killerPlaceholders.put("victim", victim.getName());
        killer.sendMessage(plugin.getConfigManager().getMessage("killer_heart_gained", killerPlaceholders));
        plugin.getHeartManager().removePlayerHearts(victimUuid, 1);
        Map<String, String> victimPlaceholders = new HashMap<>();
        victimPlaceholders.put("killer", killer.getName());
        victim.sendMessage(plugin.getConfigManager().getMessage("victim_heart_lost", victimPlaceholders));

        if (plugin.getHeartManager().getPlayerHearts(victimUuid) <= 0) {
            victim.sendMessage(plugin.getConfigManager().getMessage("no_hearts_remaining"));
            plugin.getBanManager().banPlayer(victimUuid, plugin.getConfigManager().getDeathBanTime());
        }
    }
}