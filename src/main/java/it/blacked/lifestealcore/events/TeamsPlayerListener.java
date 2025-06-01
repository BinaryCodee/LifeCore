package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.TeamsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeamsPlayerListener implements Listener {
    private final LifeCore plugin;
    private final TeamsManager teamsManager;

    public TeamsPlayerListener(LifeCore plugin) {
        this.plugin = plugin;
        this.teamsManager = LifeCore.getTeamsManager();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();
        if (teamsManager.arePlayersAllied(victim, attacker) || teamsManager.arePlayersInSameTeam(victim, attacker)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        teamsManager.disableTeamChat(event.getPlayer());
        teamsManager.disableAllyChat(event.getPlayer());
    }
}