package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final LifeCore plugin;

    public PlayerMoveListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (plugin.getBanManager().isPlayerBanned(player.getUniqueId())) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                    event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                    event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                event.setCancelled(true);
                if (plugin.getConfigManager().isBanFreezeMessageEnabled()) {
                    player.sendMessage(plugin.getConfigManager().getMessage("banned_freeze_message"));
                }
            }
        }
    }
}