package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PingCommand implements CommandExecutor {
    private final LifeCore plugin;

    public PingCommand(LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("player_only")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/ping [player]")));
            return true;
        }

        Player target;
        String messageKey;
        boolean isSelf;

        if (args.length == 0) {
            if (!sender.hasPermission("lifecore.ping")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("no_permission")));
                return true;
            }
            target = (Player) sender;
            messageKey = "ping_self";
            isSelf = true;
        } else {
            if (!sender.hasPermission("lifecore.ping.others")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("no_permission")));
                return true;
            }
            target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("player_not_found")));
                return true;
            }
            messageKey = "ping_other";
            isSelf = false;
        }

        int ping = target.getPing();
        String pingColor = getPingColor(ping);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName());
        placeholders.put("ping", String.valueOf(ping));
        placeholders.put("ping_color", pingColor);
        String message = plugin.getConfigManager().getMessage(messageKey, placeholders);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }

    private String getPingColor(int ping) {
        if (ping <= 50) {
            return "&a";
        } else if (ping <= 100) {
            return "&e";
        } else if (ping <= 200) {
            return "&6";
        } else {
            return "&c";
        }
    }
}