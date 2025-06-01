package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.TeamsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TeamsAdminCommand implements CommandExecutor {
    private final LifeCore plugin;
    private final TeamsManager teamsManager;

    public TeamsAdminCommand(LifeCore plugin) {
        this.plugin = plugin;
        this.teamsManager = LifeCore.getTeamsManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lifecoreadmin.teams")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("no_permission")));
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;
            case "kick":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teamsadmin kick [player] [team]")));
                    return true;
                }
                teamsManager.adminKickPlayer(sender, args[1], args[2]);
                break;
            case "reset":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teamsadmin reset [team]")));
                    return true;
                }
                teamsManager.adminResetTeam(sender, args[1]);
                break;
            case "ally":
                if (args.length < 4) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teamsadmin ally [add/remove] [team1] [team2]")));
                    return true;
                }
                if (args[1].equalsIgnoreCase("add")) {
                    teamsManager.adminAddAlly(sender, args[2], args[3]);
                } else if (args[1].equalsIgnoreCase("remove")) {
                    teamsManager.adminRemoveAlly(sender, args[2], args[3]);
                }
                break;
            case "setstat":
                if (args.length < 4) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teamsadmin setstat [team] [stat] [value]")));
                    return true;
                }
                teamsManager.adminSetStat(sender, args[1], args[2], args[3]);
                break;
            case "create":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teamsadmin create [team]")));
                    return true;
                }
                teamsManager.adminCreateTeam(sender, args[1]);
                break;
            case "delete":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/teamsadmin delete [team]")));
                    return true;
                }
                teamsManager.adminDeleteTeam(sender, args[1]);
                break;
            default:
                sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        for (String line : plugin.getConfigManager().getConfig().getStringList("messages.teams_admin_help")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}