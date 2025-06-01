package it.blacked.lifestealcore.placeholders;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.TeamsManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamsExpansion extends PlaceholderExpansion {
    private final LifeCore plugin;
    private final TeamsManager teamsManager;

    public TeamsExpansion(LifeCore plugin, TeamsManager teamsManager) {
        this.plugin = plugin;
        this.teamsManager = teamsManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lifecore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "blacked104";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        String teamName = teamsManager.getTeamName(player.getUniqueId());
        if (teamName.isEmpty()) {
            switch (identifier) {
                case "teams_name":
                case "teams_ally_name":
                    return "";
                case "teams_members":
                case "teams_staff":
                case "teams_online":
                case "teams_offline":
                case "teams_position":
                case "teams_ally_members":
                case "teams_ally_staff":
                case "teams_ally_online":
                case "teams_ally_offline":
                case "teams_ally_position":
                    return "0";
                default:
                    return null;
            }
        }
        switch (identifier) {
            case "teams_name":
                return teamName;
            case "teams_members":
                return String.valueOf(teamsManager.getTeamMembersCount(teamName));
            case "teams_staff":
                return String.valueOf(teamsManager.getTeamStaffCount(teamName));
            case "teams_online":
                return String.valueOf(teamsManager.getTeamOnlineCount(teamName));
            case "teams_offline":
                return String.valueOf(teamsManager.getTeamOfflineCount(teamName));
            case "teams_position":
                return String.valueOf(teamsManager.getTeamPosition(teamName));
            case "teams_ally_name":
                return teamsManager.getAllyName(teamName);
            case "teams_ally_members":
                return String.valueOf(teamsManager.getAllyMembersCount(teamName));
            case "teams_ally_staff":
                return String.valueOf(teamsManager.getAllyStaffCount(teamName));
            case "teams_ally_online":
                return String.valueOf(teamsManager.getAllyOnlineCount(teamName));
            case "teams_ally_offline":
                return String.valueOf(teamsManager.getAllyOfflineCount(teamName));
            case "teams_ally_position":
                return String.valueOf(teamsManager.getAllyPosition(teamName));
            default:
                return null;
        }
    }
}