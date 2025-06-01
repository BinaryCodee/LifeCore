package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.TeamsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TeamsChatListener implements Listener {
    private final LifeCore plugin;
    private final TeamsManager teamsManager;

    public TeamsChatListener(LifeCore plugin) {
        this.plugin = plugin;
        this.teamsManager = LifeCore.getTeamsManager();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (teamsManager.isTeamChatEnabled(event.getPlayer())) {
            event.setCancelled(true);
            teamsManager.sendTeamChat(event.getPlayer(), event.getMessage());
        } else if (teamsManager.isAllyChatEnabled(event.getPlayer())) {
            event.setCancelled(true);
            teamsManager.sendAllyChat(event.getPlayer(), event.getMessage());
        }
    }
}