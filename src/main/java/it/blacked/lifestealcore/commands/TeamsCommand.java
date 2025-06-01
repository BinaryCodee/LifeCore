package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.TeamsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamsCommand implements CommandExecutor {
    private final LifeCore plugin;
    private final TeamsManager teamsManager;

    public TeamsCommand(LifeCore plugin) {
        this.plugin = plugin;
        this.teamsManager = LifeCore.getTeamsManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("player_only")));
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "help":
                sendHelp(player);
                break;
            case "create":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams create [name]")));
                    return true;
                }
                teamsManager.createTeam(player, args[1]);
                break;
            case "delete":
                teamsManager.deleteTeam(player);
                break;
            case "invite":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams invite [player]")));
                    return true;
                }
                teamsManager.invitePlayer(player, args[1]);
                break;
            case "accept":
                teamsManager.acceptInvite(player);
                break;
            case "deny":
                teamsManager.denyInvite(player);
                break;
            case "promote":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams promote [player] [rank]")));
                    return true;
                }
                teamsManager.promotePlayer(player, args[1], args[2]);
                break;
            case "demote":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams demote [player] [rank]")));
                    return true;
                }
                teamsManager.demotePlayer(player, args[1], args[2]);
                break;
            case "kick":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams kick [player]")));
                    return true;
                }
                teamsManager.kickPlayer(player, args[1]);
                break;
            case "sethome":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams sethome [name]")));
                    return true;
                }
                teamsManager.setTeamHome(player, args[1], player.getLocation());
                break;
            case "delhome":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams delhome [name]")));
                    return true;
                }
                teamsManager.deleteTeamHome(player, args[1]);
                break;
            case "home":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams home [name]")));
                    return true;
                }
                teamsManager.teleportToHome(player, args[1]);
                break;
            case "homes":
                teamsManager.listTeamHomes(player);
                break;
            case "info":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams info [team/player]")));
                    return true;
                }
                teamsManager.showTeamInfo(player, args[1]);
                break;
            case "stats":
                teamsManager.showTeamStats(player);
                break;
            case "top":
                teamsManager.showTopTeams(player);
                break;
            case "position":
                teamsManager.showTeamPosition(player);
                break;
            case "ally":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams ally [request/accept/deny/remove/chat] [team/message]")));
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "request":
                        if (args.length < 3) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams ally request [team]")));
                            return true;
                        }
                        teamsManager.requestAlly(player, args[2]);
                        break;
                    case "accept":
                        teamsManager.acceptAlly(player);
                        break;
                    case "deny":
                        teamsManager.denyAlly(player);
                        break;
                    case "remove":
                        if (args.length < 3) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams ally remove [team]")));
                            return true;
                        }
                        teamsManager.removeAlly(player, args[2]);
                        break;
                    case "chat":
                        if (args.length < 3) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams ally chat [message]")));
                            return true;
                        }
                        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                        teamsManager.sendAllyChat(player, message);
                        break;
                }
                break;
            case "allystats":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams allystats [team]")));
                    return true;
                }
                teamsManager.showAllyStats(player, args[1]);
                break;
            case "allyposition":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams allyposition [team]")));
                    return true;
                }
                teamsManager.showAllyPosition(player, args[1]);
                break;
            case "chat":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teams chat [message]")));
                    return true;
                }
                String chatMessage = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                teamsManager.sendTeamChat(player, chatMessage);
                break;
            default:
                sendHelp(player);
        }
        return true;
    }

    private void sendHelp(Player player) {
        for (String line : plugin.getConfigManager().getConfig().getStringList("messages.teams_help")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}