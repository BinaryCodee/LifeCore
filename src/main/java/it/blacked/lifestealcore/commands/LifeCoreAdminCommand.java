package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

        sendHelpMessage(sender);
        return true;
    }

    private void handleHeartCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/lifecoreadmin heart [add/remove/list]");
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
                    sender.sendMessage("§c/lifecoreadmin heart add [player] [amount]");
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
                    sender.sendMessage("§c/lifecoreadmin heart remove [player] [amount]");
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
                    sender.sendMessage("§c/lifecoreadmin heart list [player/all]");
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
                sender.sendMessage("§c/lifecoreadmin heart [add/remove/list]");
                break;
        }
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§c§lLIFESTEAL §7- §6§lCOMANDI ADMIN");
        sender.sendMessage("§c/lifecoreadmin help §8- §7Mostra tutti i comandi per gli admin");
        sender.sendMessage("§c/lifecoreadmin heart add [player] [amount] §8- §7Aggiungi i cuori ad un giocatore");
        sender.sendMessage("§c/lifecoreadmin heart remove [player] [amount] §8- §7Rimuovi i cuori di un giocatore");
        sender.sendMessage("§c/lifecoreadmin heart list [player/all] §8- §7Lista cuori di un giocatore o di tutti i giocatori");
        sender.sendMessage("");
    }
}