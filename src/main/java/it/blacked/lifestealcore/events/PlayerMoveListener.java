package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerMoveListener implements Listener {

    private final LifeCore plugin;
    private final Set<UUID> notified = new HashSet<>();

    public PlayerMoveListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (plugin.getBanManager().isPlayerBanned(uuid)) {
            event.setCancelled(true);

            if (!notified.contains(uuid)) {
                String remainingTime = plugin.getBanManager().getRemainingBanTime(uuid);
                player.sendMessage(plugin.getConfigManager().getMessage("banned_message")
                        .replace("{time}", remainingTime));
                notified.add(uuid);
            }
        } else {
            notified.remove(uuid);
        }
    }
}
