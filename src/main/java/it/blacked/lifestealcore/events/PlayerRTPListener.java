package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.ConfigManager;
import it.blacked.lifestealcore.managers.RTPManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerRTPListener implements Listener {

    private final LifeCore plugin;
    private final ConfigManager configManager;
    private final RTPManager rtpManager;

    public PlayerRTPListener(LifeCore plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.rtpManager = LifeCore.getRtpManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (rtpManager.isPlayerInRTP(player)) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                    event.getFrom().getBlockZ() != event.getTo().getBlockZ() ||
                    event.getFrom().getBlockY() != event.getTo().getBlockY()) {
                rtpManager.cancelRTP(player);
                player.sendMessage(configManager.getMessage("teleport_cancelled_movement"));
            }
        }
    }
}