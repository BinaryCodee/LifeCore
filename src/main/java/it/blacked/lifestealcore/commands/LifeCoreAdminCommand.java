package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LifeCoreAdminCommand implements CommandExecutor {

    private final LifeCore plugin;

    public LifeCoreAdminCommand(LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(plugin.getConfigManager().getCommandPermission("lifecoreadmin"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("heart")) {
            handleHeartCommand(sender, args);
            return true;
        }

        if (args[0].equalsIgnoreCase("ban")) {
            handleBanCommand(sender, args);
            return true;
        }

        if (args[0].equalsIgnoreCase("unban")) {
            handleUnbanCommand(sender, args);
            return true;
        }

        sendHelpMessage(sender);
        return true;
    }

    private void handleHeartCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid_usage", Map.of("usage", "/lifecoreadmin heart [add/remove/list]")));
            return;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "add":
                if (!sender.hasPermission(plugin.getConfigManager().getCommandPermission("lifecoreadmin_heart_add"))) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
                    return;
                }

                if (args.length < 4) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid_usage", Map.of("usage", "/lifecoreadmin heart add [player] [amount]")));
                    return;
                }

                String playerName = args[2];
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

                try {
                    int amount = Integer.parseInt(args[3]);
                    plugin.getHeartManager().addPlayerHearts(target.getUniqueId(), amount);

                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("player", playerName);
                    placeholders.put("amount", String.valueOf(amount));

                    sender.sendMessage(plugin.getConfigManager().getMessage("heart_added", placeholders));
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid_amount"));
                }
                break;

            case "remove":
                if (!sender.hasPermission(plugin.getConfigManager().getCommandPermission("lifecoreadmin_heart_remove"))) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
                    return;
                }

                if (args.length < 4) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid_usage", Map.of("usage", "/lifecoreadmin heart remove [player] [amount]")));
                    return;
                }

                playerName = args[2];
                target = Bukkit.getOfflinePlayer(playerName);

                try {
                    int amount = Integer.parseInt(args[3]);
                    plugin.getHeartManager().removePlayerHearts(target.getUniqueId(), amount);

                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("player", playerName);
                    placeholders.put("amount", String.valueOf(amount));

                    sender.sendMessage(plugin.getConfigManager().getMessage("heart_removed", placeholders));
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid_amount"));
                }
                break;

            case "list":
                if (!sender.hasPermission(plugin.getConfigManager().getCommandPermission("lifecoreadmin_heart_list"))) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
                    return;
                }

                if (args.length < 3) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid_usage", Map.of("usage", "/lifecoreadmin heart list [player/all]")));
                    return;
                }

                if (args[2].equalsIgnoreCase("all")) {
                    Map<UUID, Integer> allHearts = plugin.getHeartManager().getAllHearts();

                    sender.sendMessage(plugin.getConfigManager().getMessage("heart_list_header"));

                    for (Map.Entry<UUID, Integer> entry : allHearts.entrySet()) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());

                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("player", player.getName() != null ? player.getName() : entry.getKey().toString());
                        placeholders.put("hearts", String.valueOf(entry.getValue()));

                        sender.sendMessage(plugin.getConfigManager().getMessage("heart_list_format", placeholders));
                    }

                    sender.sendMessage(plugin.getConfigManager().getMessage("heart_list_footer"));
                } else {
                    playerName = args[2];
                    target = Bukkit.getOfflinePlayer(playerName);

                    int hearts = plugin.getHeartManager().getPlayerHearts(target.getUniqueId());

                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("player", playerName);
                    placeholders.put("hearts", String.valueOf(hearts));

                    sender.sendMessage(plugin.getConfigManager().getMessage("heart_list_player", placeholders));
                }
                break;

            default:
                sender.sendMessage(plugin.getConfigManager().getMessage("invalid_usage", Map.of("usage", "/lifecoreadmin heart [add/remove/list]")));
                break;
        }
    }
    private void handleBanCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(plugin.getConfigManager().getCommandPermission("lifecoreadmin_ban"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid_usage", Map.of("usage", "/lifecoreadmin ban <player> <time>")));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found"));
            return;
        }

        String time = args[2];
        plugin.getBanManager().banPlayer(target.getUniqueId(), time);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName());
        placeholders.put("time", time);
        sender.sendMessage(plugin.getConfigManager().getMessage("ban_command_success", placeholders));
    }

    private void handleUnbanCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(plugin.getConfigManager().getCommandPermission("lifecoreadmin_unban"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid_usage", Map.of("usage", "/lifecoreadmin unban <player>")));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_not_found"));
            return;
        }

        plugin.getBanManager().unbanPlayer(target.getUniqueId());

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName());
        sender.sendMessage(plugin.getConfigManager().getMessage("unban_command_success", placeholders));
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§cThis server is running LifestealCore v1.0 - By blacked10469");
        sender.sendMessage("");
        sender.sendMessage(plugin.getConfigManager().getMessage("admin_help_header"));
        sender.sendMessage(plugin.getConfigManager().getMessage("admin_help_heart_add"));
        sender.sendMessage(plugin.getConfigManager().getMessage("admin_help_heart_remove"));
        sender.sendMessage(plugin.getConfigManager().getMessage("admin_help_heart_list"));
        sender.sendMessage(plugin.getConfigManager().getMessage("admin_help_ban"));
        sender.sendMessage(plugin.getConfigManager().getMessage("admin_help_unban"));
        sender.sendMessage(plugin.getConfigManager().getMessage("admin_help_footer"));
    }
}