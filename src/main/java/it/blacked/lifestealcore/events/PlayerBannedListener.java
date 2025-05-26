package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerBannedListener implements Listener {

    private final LifeCore plugin;

    public PlayerBannedListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (plugin.getBanManager().isPlayerBanned(player.getUniqueId()) &&
                plugin.getConfigManager().isBanCommandBlockedEnabled()) {
            String command = event.getMessage().toLowerCase();
            if (!command.startsWith("/lifecore buy") && !command.startsWith("/lc buy") && !command.startsWith("/life buy")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("banned_command_blocked")));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (plugin.getBanManager().isPlayerBanned(player.getUniqueId()) &&
                plugin.getConfigManager().isBanCustomChatFormatEnabled()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", player.getName());
            placeholders.put("message", event.getMessage());

            String customFormat = plugin.getConfigManager().getMessage("banned_chat_format", placeholders);
            event.setFormat(ChatColor.translateAlternateColorCodes('&', customFormat));
        }
    }
}