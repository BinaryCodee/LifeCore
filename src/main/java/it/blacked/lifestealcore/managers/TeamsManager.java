package it.blacked.lifestealcore.managers;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.utils.TeamsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeamsManager {
    private final LifeCore plugin;
    private final File teamsFile;
    private final FileConfiguration teamsConfig;
    private final Map<UUID, String> playerTeams;
    private final Map<UUID, TeamsUtils> playerRoles;
    private final Map<String, Map<String, Location>> teamHomes;
    private final Map<String, Set<String>> teamAllies;
    private final Map<String, Map<String, Integer>> teamStats;
    private final Map<UUID, String> teamInvites;
    private final Map<String, String> allyInvites;
    private final Map<UUID, Boolean> teamChatEnabled;
    private final Map<UUID, Boolean> allyChatEnabled;

    public TeamsManager(LifeCore plugin) {
        this.plugin = plugin;
        this.teamsFile = new File(plugin.getDataFolder(), "teams.yml");
        if (!teamsFile.exists()) {
            plugin.saveResource("teams.yml", false);
        }
        this.teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
        this.playerTeams = new HashMap<>();
        this.playerRoles = new HashMap<>();
        this.teamHomes = new HashMap<>();
        this.teamAllies = new HashMap<>();
        this.teamStats = new HashMap<>();
        this.teamInvites = new HashMap<>();
        this.allyInvites = new HashMap<>();
        this.teamChatEnabled = new HashMap<>();
        this.allyChatEnabled = new HashMap<>();
        loadTeams();
    }

    public void createTeam(Player player, String teamName) {
        if (playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_already_in_team")));
            return;
        }
        if (teamsConfig.contains("teams." + teamName.toLowerCase())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_name_taken")));
            return;
        }
        String normalizedTeamName = teamName.toLowerCase();
        playerTeams.put(player.getUniqueId(), teamName);
        playerRoles.put(player.getUniqueId(), TeamsUtils.LEADER);
        teamHomes.put(normalizedTeamName, new HashMap<>());
        teamAllies.put(normalizedTeamName, new HashSet<>());
        teamStats.put(normalizedTeamName, new HashMap<>());
        teamStats.get(normalizedTeamName).put("kills", 0);
        teamStats.get(normalizedTeamName).put("deaths", 0);
        teamStats.get(normalizedTeamName).put("streak", 0);
        teamsConfig.set("teams." + normalizedTeamName + ".leader", player.getUniqueId().toString());
        teamsConfig.set("teams." + normalizedTeamName + ".members." + player.getUniqueId().toString(), "LEADER");
        teamsConfig.set("teams." + normalizedTeamName + ".name", teamName);
        saveTeams();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_created").replace("%team%", teamName)));
    }

    public void deleteTeam(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        if (!playerRoles.get(player.getUniqueId()).equals(TeamsUtils.LEADER)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        removeTeam(teamName.toLowerCase());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_disbanded").replace("%team%", teamName)));
    }

    public void invitePlayer(Player player, String targetName) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER) && !role.equals(TeamsUtils.ADMIN)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("player_not_found")));
            return;
        }
        if (playerTeams.containsKey(target.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_already_in_team")));
            return;
        }
        teamInvites.put(target.getUniqueId(), teamName);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", teamName);
        placeholders.put("player", player.getName());
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_invite_received", placeholders)));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_invite_sent").replace("%player%", targetName)));
    }

    public void acceptInvite(Player player) {
        if (!teamInvites.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_invite")));
            return;
        }
        String teamName = teamInvites.get(player.getUniqueId());
        if (getTeamMembers(teamName).size() >= plugin.getConfigManager().getMaxTeamMembers()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_max_members")));
            return;
        }
        playerTeams.put(player.getUniqueId(), teamName);
        playerRoles.put(player.getUniqueId(), TeamsUtils.MEMBER);
        teamsConfig.set("teams." + teamName.toLowerCase() + ".members." + player.getUniqueId().toString(), "MEMBER");
        saveTeams();
        teamInvites.remove(player.getUniqueId());
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        placeholders.put("team", teamName);
        broadcastToTeam(teamName, plugin.getConfigManager().getMessage("teams_joined", placeholders));
    }

    public void denyInvite(Player player) {
        if (!teamInvites.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_invite")));
            return;
        }
        teamInvites.remove(player.getUniqueId());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_invite_denied")));
    }

    public void promotePlayer(Player player, String targetName, String rank) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !playerTeams.getOrDefault(target.getUniqueId(), "").equalsIgnoreCase(teamName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("player_not_found")));
            return;
        }
        TeamsUtils newRole;
        try {
            newRole = TeamsUtils.valueOf(rank.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_invalid_rank")));
            return;
        }
        if (newRole.equals(TeamsUtils.LEADER)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_leader_promote")));
            return;
        }
        playerRoles.put(target.getUniqueId(), newRole);
        teamsConfig.set("teams." + teamName.toLowerCase() + ".members." + target.getUniqueId().toString(), newRole.name());
        saveTeams();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", targetName);
        placeholders.put("rank", newRole.name());
        broadcastToTeam(teamName, plugin.getConfigManager().getMessage("teams_promoted", placeholders));
    }

    public void demotePlayer(Player player, String targetName, String rank) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !playerTeams.getOrDefault(target.getUniqueId(), "").equalsIgnoreCase(teamName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("player_not_found")));
            return;
        }
        TeamsUtils newRole;
        try {
            newRole = TeamsUtils.valueOf(rank.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_invalid_rank")));
            return;
        }
        if (newRole.equals(TeamsUtils.LEADER)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_leader_demote")));
            return;
        }
        playerRoles.put(target.getUniqueId(), newRole);
        teamsConfig.set("teams." + teamName.toLowerCase() + ".members." + target.getUniqueId().toString(), newRole.name());
        saveTeams();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", targetName);
        placeholders.put("rank", newRole.name());
        broadcastToTeam(teamName, plugin.getConfigManager().getMessage("teams_demoted", placeholders));
    }

    public void kickPlayer(Player player, String targetName) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER) && !role.equals(TeamsUtils.ADMIN)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !playerTeams.getOrDefault(target.getUniqueId(), "").equalsIgnoreCase(teamName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("player_not_found")));
            return;
        }
        if (playerRoles.get(target.getUniqueId()).equals(TeamsUtils.LEADER)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_kick_leader")));
            return;
        }
        playerTeams.remove(target.getUniqueId());
        playerRoles.remove(target.getUniqueId());
        teamsConfig.set("teams." + teamName.toLowerCase() + ".members." + target.getUniqueId().toString(), null);
        saveTeams();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", targetName);
        broadcastToTeam(teamName, plugin.getConfigManager().getMessage("teams_kicked", placeholders));
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_kicked_player").replace("%team%", teamName)));
    }

    public void setTeamHome(Player player, String homeName, Location location) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER) && !role.equals(TeamsUtils.ADMIN)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        if (teamHomes.get(teamName.toLowerCase()).size() >= plugin.getConfigManager().getMaxTeamHomes() && !teamHomes.get(teamName.toLowerCase()).containsKey(homeName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("max", String.valueOf(plugin.getConfigManager().getMaxTeamHomes()));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_max_homes", placeholders)));
            return;
        }
        teamHomes.get(teamName.toLowerCase()).put(homeName, location);
        teamsConfig.set("teams." + teamName.toLowerCase() + ".homes." + homeName + ".world", location.getWorld().getName());
        teamsConfig.set("teams." + teamName.toLowerCase() + ".homes." + homeName + ".x", location.getX());
        teamsConfig.set("teams." + teamName.toLowerCase() + ".homes." + homeName + ".y", location.getY());
        teamsConfig.set("teams." + teamName.toLowerCase() + ".homes." + homeName + ".z", location.getZ());
        teamsConfig.set("teams." + teamName.toLowerCase() + ".homes." + homeName + ".yaw", location.getYaw());
        teamsConfig.set("teams." + teamName.toLowerCase() + ".homes." + homeName + ".pitch", location.getPitch());
        saveTeams();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_home_set").replace("%home%", homeName)));
    }

    public void deleteTeamHome(Player player, String homeName) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER) && !role.equals(TeamsUtils.ADMIN)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        if (!teamHomes.get(teamName.toLowerCase()).containsKey(homeName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_home_not_found")));
            return;
        }
        teamHomes.get(teamName.toLowerCase()).remove(homeName);
        teamsConfig.set("teams." + teamName.toLowerCase() + ".homes." + homeName, null);
        saveTeams();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_home_deleted").replace("%home%", homeName)));
    }

    public void teleportToHome(Player player, String homeName) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        if (!teamHomes.get(teamName.toLowerCase()).containsKey(homeName)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_home_not_found")));
            return;
        }
        player.teleport(teamHomes.get(teamName.toLowerCase()).get(homeName));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_home_teleported").replace("%home%", homeName)));
    }

    public void listTeamHomes(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        if (teamHomes.get(teamName.toLowerCase()).isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_homes")));
            return;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_homes_list")));
        for (String homeName : teamHomes.get(teamName.toLowerCase()).keySet()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- " + homeName));
        }
    }

    public void showTeamInfo(Player player, String target) {
        Player targetPlayer = Bukkit.getPlayer(target);
        String teamName;
        if (targetPlayer != null && playerTeams.containsKey(targetPlayer.getUniqueId())) {
            teamName = playerTeams.get(targetPlayer.getUniqueId());
        } else if (teamsConfig.contains("teams." + target.toLowerCase())) {
            teamName = teamsConfig.getString("teams." + target.toLowerCase() + ".name", target);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_found")));
            return;
        }
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", teamName);
        placeholders.put("members", String.valueOf(getTeamMembersCount(teamName)));
        placeholders.put("staff", String.valueOf(getTeamStaffCount(teamName)));
        placeholders.put("online", String.valueOf(getTeamOnlineCount(teamName)));
        placeholders.put("offline", String.valueOf(getTeamOfflineCount(teamName)));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_info", placeholders)));
    }

    public void showTeamStats(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", teamName);
        placeholders.put("kills", String.valueOf(teamStats.get(teamName.toLowerCase()).getOrDefault("kills", 0)));
        placeholders.put("deaths", String.valueOf(teamStats.get(teamName.toLowerCase()).getOrDefault("deaths", 0)));
        placeholders.put("streak", String.valueOf(teamStats.get(teamName.toLowerCase()).getOrDefault("streak", 0)));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_stats", placeholders)));
    }

    public void showTopTeams(Player player) {
        List<String> teams = new ArrayList<>(teamsConfig.getConfigurationSection("teams").getKeys(false));
        teams.sort((a, b) -> teamStats.get(b.toLowerCase()).getOrDefault("kills", 0) - teamStats.get(a.toLowerCase()).getOrDefault("kills", 0));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_top_header")));
        for (int i = 0; i < Math.min(5, teams.size()); i++) {
            String teamKey = teams.get(i);
            String teamName = teamsConfig.getString("teams." + teamKey + ".name", teamKey);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("position", String.valueOf(i + 1));
            placeholders.put("team", teamName);
            placeholders.put("kills", String.valueOf(teamStats.get(teamKey).getOrDefault("kills", 0)));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_top_format", placeholders)));
        }
    }

    public void showTeamPosition(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", teamName);
        placeholders.put("position", String.valueOf(getTeamPosition(teamName)));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_position", placeholders)));
    }

    public void requestAlly(Player player, String allyTeam) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER) && !role.equals(TeamsUtils.ADMIN)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        if (!teamsConfig.contains("teams." + allyTeam.toLowerCase())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_found")));
            return;
        }
        String allyName = teamsConfig.getString("teams." + allyTeam.toLowerCase() + ".name", allyTeam);
        if (teamAllies.get(teamName.toLowerCase()).contains(allyTeam.toLowerCase())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_already_allied")));
            return;
        }
        allyInvites.put(allyTeam.toLowerCase(), teamName.toLowerCase());
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", teamName);
        broadcastToTeam(allyName, plugin.getConfigManager().getMessage("teams_ally_request", placeholders));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_ally_request_sent").replace("%team%", allyName)));
    }

    public void acceptAlly(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER) && !role.equals(TeamsUtils.ADMIN)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        if (!allyInvites.containsKey(teamName.toLowerCase())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_ally_invite")));
            return;
        }
        String allyTeamLower = allyInvites.get(teamName.toLowerCase());
        String allyTeamName = teamsConfig.getString("teams." + allyTeamLower + ".name", allyTeamLower);
        teamAllies.get(teamName.toLowerCase()).add(allyTeamLower);
        teamAllies.get(allyTeamLower).add(teamName.toLowerCase());
        teamsConfig.set("teams." + teamName.toLowerCase() + ".allies", new ArrayList<>(teamAllies.get(teamName.toLowerCase())));
        teamsConfig.set("teams." + allyTeamLower + ".allies", new ArrayList<>(teamAllies.get(allyTeamLower)));
        saveTeams();
        allyInvites.remove(teamName.toLowerCase());
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", allyTeamName);
        broadcastToTeam(teamName, plugin.getConfigManager().getMessage("teams_ally_accepted", placeholders));
        placeholders.put("team", teamName);
        broadcastToTeam(allyTeamName, plugin.getConfigManager().getMessage("teams_ally_accepted", placeholders));
    }

    public void denyAlly(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER) && !role.equals(TeamsUtils.ADMIN)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        if (!allyInvites.containsKey(teamName.toLowerCase())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_ally_invite")));
            return;
        }
        String allyTeamLower = allyInvites.get(teamName.toLowerCase());
        String allyTeamName = teamsConfig.getString("teams." + allyTeamLower + ".name", allyTeamLower);
        allyInvites.remove(teamName.toLowerCase());
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", allyTeamName);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_ally_denied", placeholders)));
    }

    public void removeAlly(Player player, String allyTeam) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        TeamsUtils role = playerRoles.get(player.getUniqueId());
        if (!role.equals(TeamsUtils.LEADER) && !role.equals(TeamsUtils.ADMIN)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_permission")));
            return;
        }
        String allyTeamLower = allyTeam.toLowerCase();
        if (!teamAllies.get(teamName.toLowerCase()).contains(allyTeamLower)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_allied")));
            return;
        }
        String allyTeamName = teamsConfig.getString("teams." + allyTeamLower + ".name", allyTeam);
        teamAllies.get(teamName.toLowerCase()).remove(allyTeamLower);
        teamAllies.get(allyTeamLower).remove(teamName.toLowerCase());
        teamsConfig.set("teams." + teamName.toLowerCase() + ".allies", new ArrayList<>(teamAllies.get(teamName.toLowerCase())));
        teamsConfig.set("teams." + allyTeamLower + ".allies", new ArrayList<>(teamAllies.get(allyTeamLower)));
        saveTeams();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", allyTeamName);
        broadcastToTeam(teamName, plugin.getConfigManager().getMessage("teams_ally_removed", placeholders));
        placeholders.put("team", teamName);
        broadcastToTeam(allyTeamName, plugin.getConfigManager().getMessage("teams_ally_removed", placeholders));
    }

    public void showAllyStats(Player player, String allyTeam) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        String allyTeamLower = allyTeam.toLowerCase();
        if (!teamAllies.get(teamName.toLowerCase()).contains(allyTeamLower)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_allied")));
            return;
        }
        String allyTeamName = teamsConfig.getString("teams." + allyTeamLower + ".name", allyTeam);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", allyTeamName);
        placeholders.put("kills", String.valueOf(teamStats.get(allyTeamLower).getOrDefault("kills", 0)));
        placeholders.put("deaths", String.valueOf(teamStats.get(allyTeamLower).getOrDefault("deaths", 0)));
        placeholders.put("streak", String.valueOf(teamStats.get(allyTeamLower).getOrDefault("streak", 0)));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_ally_stats", placeholders)));
    }

    public void showAllyPosition(Player player, String allyTeam) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        String allyTeamLower = allyTeam.toLowerCase();
        if (!teamAllies.get(teamName.toLowerCase()).contains(allyTeamLower)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_allied")));
            return;
        }
        String allyTeamName = teamsConfig.getString("teams." + allyTeamLower + ".name", allyTeam);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", allyTeamName);
        placeholders.put("position", String.valueOf(getTeamPosition(allyTeamName)));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_ally_position", placeholders)));
    }

    public void toggleTeamChat(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        boolean enabled = teamChatEnabled.getOrDefault(player.getUniqueId(), false);
        teamChatEnabled.put(player.getUniqueId(), !enabled);
        if (!enabled) {
            allyChatEnabled.put(player.getUniqueId(), false);
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage(!enabled ? "teams_chat_enabled" : "teams_chat_disabled")));
    }

    public void toggleAllyChat(Player player) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        if (teamAllies.get(teamName.toLowerCase()).isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_allies")));
            return;
        }
        boolean enabled = allyChatEnabled.getOrDefault(player.getUniqueId(), false);
        allyChatEnabled.put(player.getUniqueId(), !enabled);
        if (!enabled) {
            teamChatEnabled.put(player.getUniqueId(), false);
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage(!enabled ? "teams_ally_chat_enabled" : "teams_ally_chat_disabled")));
    }

    public void sendTeamChat(Player player, String message) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        placeholders.put("message", message);
        String formatted = plugin.getConfigManager().getMessage("teams_chat_format", placeholders);
        for (UUID member : getTeamMembers(teamName)) {
            Player online = Bukkit.getPlayer(member);
            if (online != null) {
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted));
            }
        }
    }

    public void sendAllyChat(Player player, String message) {
        if (!playerTeams.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_in_team")));
            return;
        }
        String teamName = playerTeams.get(player.getUniqueId());
        if (teamAllies.get(teamName.toLowerCase()).isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_no_allies")));
            return;
        }
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        placeholders.put("team", teamName);
        placeholders.put("message", message);
        String formatted = plugin.getConfigManager().getMessage("teams_ally_chat_format", placeholders);
        for (UUID member : getTeamMembers(teamName)) {
            Player online = Bukkit.getPlayer(member);
            if (online != null) {
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted));
            }
        }
        for (String allyTeamLower : teamAllies.get(teamName.toLowerCase())) {
            String allyTeamName = teamsConfig.getString("teams." + allyTeamLower + ".name", allyTeamLower);
            for (UUID member : getTeamMembers(allyTeamName)) {
                Player online = Bukkit.getPlayer(member);
                if (online != null) {
                    online.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted));
                }
            }
        }
    }

    public void adminKickPlayer(CommandSender sender, String playerName, String teamName) {
        String teamNameLower = teamName.toLowerCase();
        if (!teamsConfig.contains("teams." + teamNameLower)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_found")));
            return;
        }
        Player target = Bukkit.getPlayer(playerName);
        if (target == null || !playerTeams.getOrDefault(target.getUniqueId(), "").equalsIgnoreCase(teamName)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("player_not_found")));
            return;
        }
        playerTeams.remove(target.getUniqueId());
        playerRoles.remove(target.getUniqueId());
        teamsConfig.set("teams." + teamNameLower + ".members." + target.getUniqueId().toString(), null);
        saveTeams();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", playerName);
        broadcastToTeam(teamName, plugin.getConfigManager().getMessage("teams_kicked", placeholders));
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_kicked_player").replace("%team%", teamName)));
    }

    public void adminResetTeam(CommandSender sender, String teamName) {
        String teamNameLower = teamName.toLowerCase();
        if (!teamsConfig.contains("teams." + teamNameLower)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_found")));
            return;
        }
        removeTeam(teamNameLower);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_admin_reset").replace("%team%", teamName)));
    }

    public void adminAddAlly(CommandSender sender, String team1, String team2) {
        String team1Lower = team1.toLowerCase();
        String team2Lower = team2.toLowerCase();
        if (!teamsConfig.contains("teams." + team1Lower) || !teamsConfig.contains("teams." + team2Lower)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_found")));
            return;
        }
        if (teamAllies.get(team1Lower).contains(team2Lower)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_already_allied")));
            return;
        }
        teamAllies.get(team1Lower).add(team2Lower);
        teamAllies.get(team2Lower).add(team1Lower);
        teamsConfig.set("teams." + team1Lower + ".allies", new ArrayList<>(teamAllies.get(team1Lower)));
        teamsConfig.set("teams." + team2Lower + ".allies", new ArrayList<>(teamAllies.get(team2Lower)));
        saveTeams();
        String team1Name = teamsConfig.getString("teams." + team1Lower + ".name", team1);
        String team2Name = teamsConfig.getString("teams." + team2Lower + ".name", team2);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", team2Name);
        broadcastToTeam(team1Name, plugin.getConfigManager().getMessage("teams_ally_accepted", placeholders));
        placeholders.put("team", team1Name);
        broadcastToTeam(team2Name, plugin.getConfigManager().getMessage("teams_ally_accepted", placeholders));
    }

    public void adminRemoveAlly(CommandSender sender, String team1, String team2) {
        String team1Lower = team1.toLowerCase();
        String team2Lower = team2.toLowerCase();
        if (!teamsConfig.contains("teams." + team1Lower) || !teamsConfig.contains("teams." + team2Lower)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_found")));
            return;
        }
        if (!teamAllies.get(team1Lower).contains(team2Lower) || !teamAllies.get(team2Lower).contains(team1Lower)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_allied")));
            return;
        }
        teamAllies.get(team1Lower).remove(team2Lower);
        teamAllies.get(team2Lower).remove(team1Lower);
        teamsConfig.set("teams." + team1Lower + ".allies", new ArrayList<>(teamAllies.get(team1Lower)));
        teamsConfig.set("teams." + team2Lower + ".allies", new ArrayList<>(teamAllies.get(team2Lower)));
        saveTeams();
        String team1Name = teamsConfig.getString("teams." + team1Lower + ".name", team1);
        String team2Name = teamsConfig.getString("teams." + team2Lower + ".name", team2);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("team", team2Name);
        broadcastToTeam(team1Name, plugin.getConfigManager().getMessage("teams_ally_removed", placeholders));
        placeholders.put("team", team1Name);
        broadcastToTeam(team2Name, plugin.getConfigManager().getMessage("teams_ally_removed", placeholders));
    }

    public void adminSetStat(CommandSender sender, String teamName, String stat, String value) {
        String teamNameLower = teamName.toLowerCase();
        if (!teamsConfig.contains("teams." + teamNameLower)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_found")));
            return;
        }
        try {
            int val = Integer.parseInt(value);
            if (!teamStats.get(teamNameLower).containsKey(stat)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_invalid_stat")));
                return;
            }
            teamStats.get(teamNameLower).put(stat, val);
            teamsConfig.set("teams." + teamNameLower + ".stats." + stat, val);
            saveTeams();
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("team", teamName);
            placeholders.put("stat", stat);
            placeholders.put("value", value);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_admin_stat_set", placeholders)));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("invalid_amount")));
        }
    }

    public void adminCreateTeam(CommandSender sender, String teamName) {
        String teamNameLower = teamName.toLowerCase();
        if (teamsConfig.contains("teams." + teamNameLower)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_name_taken")));
            return;
        }
        teamHomes.put(teamNameLower, new HashMap<>());
        teamAllies.put(teamNameLower, new HashSet<>());
        teamStats.put(teamNameLower, new HashMap<>());
        teamStats.get(teamNameLower).put("kills", 0);
        teamStats.get(teamNameLower).put("deaths", 0);
        teamStats.get(teamNameLower).put("streak", 0);
        teamsConfig.set("teams." + teamNameLower + ".members", new HashMap<>());
        teamsConfig.set("teams." + teamNameLower + ".name", teamName);
        saveTeams();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_admin_created").replace("%team%", teamName)));
    }

    public void adminDeleteTeam(CommandSender sender, String teamName) {
        String teamNameLower = teamName.toLowerCase();
        if (!teamsConfig.contains("teams." + teamNameLower)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_not_found")));
            return;
        }
        removeTeam(teamNameLower);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_admin_deleted").replace("%team%", teamName)));
    }

    private void removeTeam(String teamNameLower) {
        String teamName = teamsConfig.getString("teams." + teamNameLower + ".name", teamNameLower);
        for (UUID member : getTeamMembers(teamName)) {
            playerTeams.remove(member);
            playerRoles.remove(member);
            teamInvites.remove(member);
            teamChatEnabled.remove(member);
            allyChatEnabled.remove(member);
            Player online = Bukkit.getPlayer(member);
            if (online != null) {
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getMessage("teams_disbanded").replace("%team%", teamName)));
            }
        }
        for (String allyLower : new HashSet<>(teamAllies.get(teamNameLower))) {
            teamAllies.get(allyLower).remove(teamNameLower);
            teamsConfig.set("teams." + allyLower + ".allies", new ArrayList<>(teamAllies.get(allyLower)));
        }
        teamHomes.remove(teamNameLower);
        teamAllies.remove(teamNameLower);
        teamStats.remove(teamNameLower);
        allyInvites.remove(teamNameLower);
        teamsConfig.set("teams." + teamNameLower, null);
        saveTeams();
    }

    public boolean arePlayersInSameTeam(Player player1, Player player2) {
        return playerTeams.containsKey(player1.getUniqueId()) && playerTeams.containsKey(player2.getUniqueId()) &&
                playerTeams.get(player1.getUniqueId()).equalsIgnoreCase(playerTeams.get(player2.getUniqueId()));
    }

    public boolean arePlayersAllied(Player player1, Player player2) {
        if (!playerTeams.containsKey(player1.getUniqueId()) || !playerTeams.containsKey(player2.getUniqueId())) return false;
        String team1 = playerTeams.get(player1.getUniqueId());
        String team2 = playerTeams.get(player2.getUniqueId());
        return teamAllies.get(team1.toLowerCase()).contains(team2.toLowerCase());
    }

    public boolean isTeamChatEnabled(Player player) {
        return teamChatEnabled.getOrDefault(player.getUniqueId(), false);
    }

    public boolean isAllyChatEnabled(Player player) {
        return allyChatEnabled.getOrDefault(player.getUniqueId(), false);
    }

    public void disableTeamChat(Player player) {
        teamChatEnabled.put(player.getUniqueId(), false);
    }

    public void disableAllyChat(Player player) {
        allyChatEnabled.put(player.getUniqueId(), false);
    }

    private void loadTeams() {
        if (teamsConfig.getConfigurationSection("teams") != null) {
            for (String teamKey : teamsConfig.getConfigurationSection("teams").getKeys(false)) {
                String teamName = teamsConfig.getString("teams." + teamKey + ".name", teamKey);
                teamHomes.put(teamKey, new HashMap<>());
                teamAllies.put(teamKey, new HashSet<>(teamsConfig.getStringList("teams." + teamKey + ".allies")));
                teamStats.put(teamKey, new HashMap<>());
                teamStats.get(teamKey).put("kills", teamsConfig.getInt("teams." + teamKey + ".stats.kills", 0));
                teamStats.get(teamKey).put("deaths", teamsConfig.getInt("teams." + teamKey + ".stats.deaths", 0));
                teamStats.get(teamKey).put("streak", teamsConfig.getInt("teams." + teamKey + ".stats.streak", 0));
                if (teamsConfig.getConfigurationSection("teams." + teamKey + ".homes") != null) {
                    for (String homeName : teamsConfig.getConfigurationSection("teams." + teamKey + ".homes").getKeys(false)) {
                        String path = "teams." + teamKey + ".homes." + homeName;
                        Location loc = new Location(
                                Bukkit.getWorld(teamsConfig.getString(path + ".world")),
                                teamsConfig.getDouble(path + ".x"),
                                teamsConfig.getDouble(path + ".y"),
                                teamsConfig.getDouble(path + ".z"),
                                (float) teamsConfig.getDouble(path + ".yaw"),
                                (float) teamsConfig.getDouble(path + ".pitch")
                        );
                        teamHomes.get(teamKey).put(homeName, loc);
                    }
                }
                if (teamsConfig.getConfigurationSection("teams." + teamKey + ".members") != null) {
                    for (String uuid : teamsConfig.getConfigurationSection("teams." + teamKey + ".members").getKeys(false)) {
                        UUID playerUUID = UUID.fromString(uuid);
                        playerTeams.put(playerUUID, teamName);
                        playerRoles.put(playerUUID, TeamsUtils.valueOf(teamsConfig.getString("teams." + teamKey + ".members." + uuid)));
                    }
                }
            }
        }
    }

    private void saveTeams() {
        try {
            teamsConfig.save(teamsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save teams.yml");
        }
    }

    private Set<UUID> getTeamMembers(String teamName) {
        Set<UUID> members = new HashSet<>();
        String teamKey = teamName.toLowerCase();
        if (teamsConfig.getConfigurationSection("teams." + teamKey + ".members") != null) {
            for (String uuid : teamsConfig.getConfigurationSection("teams." + teamKey + ".members").getKeys(false)) {
                members.add(UUID.fromString(uuid));
            }
        }
        return members;
    }

    private Set<UUID> getTeamStaff(String teamName) {
        Set<UUID> staff = new HashSet<>();
        for (UUID member : getTeamMembers(teamName)) {
            TeamsUtils role = playerRoles.get(member);
            if (role.equals(TeamsUtils.LEADER) || role.equals(TeamsUtils.ADMIN) || role.equals(TeamsUtils.MOD)) {
                staff.add(member);
            }
        }
        return staff;
    }

    private Set<UUID> getTeamOnline(String teamName) {
        Set<UUID> online = new HashSet<>();
        for (UUID member : getTeamMembers(teamName)) {
            if (Bukkit.getPlayer(member) != null) {
                online.add(member);
            }
        }
        return online;
    }

    private void broadcastToTeam(String teamName, String message) {
        for (UUID member : getTeamMembers(teamName)) {
            Player online = Bukkit.getPlayer(member);
            if (online != null) {
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    public String getTeamName(UUID playerUUID) {
        return playerTeams.getOrDefault(playerUUID, "");
    }

    public String getAllyName(String teamName) {
        return teamAllies.getOrDefault(teamName.toLowerCase(), new HashSet<>()).stream()
                .map(ally -> teamsConfig.getString("teams." + ally + ".name", ally))
                .findFirst().orElse("");
    }

    public int getTeamMembersCount(String teamName) {
        return getTeamMembers(teamName).size();
    }

    public int getAllyMembersCount(String teamName) {
        return teamAllies.getOrDefault(teamName.toLowerCase(), new HashSet<>()).stream()
                .map(ally -> teamsConfig.getString("teams." + ally + ".name", ally))
                .mapToInt(this::getTeamMembersCount).sum();
    }

    public int getTeamStaffCount(String teamName) {
        return getTeamStaff(teamName).size();
    }

    public int getAllyStaffCount(String teamName) {
        return teamAllies.getOrDefault(teamName.toLowerCase(), new HashSet<>()).stream()
                .map(ally -> teamsConfig.getString("teams." + ally + ".name", ally))
                .mapToInt(this::getTeamStaffCount).sum();
    }

    public int getTeamOnlineCount(String teamName) {
        return getTeamOnline(teamName).size();
    }

    public int getAllyOnlineCount(String teamName) {
        return teamAllies.getOrDefault(teamName.toLowerCase(), new HashSet<>()).stream()
                .map(ally -> teamsConfig.getString("teams." + ally + ".name", ally))
                .mapToInt(this::getTeamOnlineCount).sum();
    }

    public int getTeamOfflineCount(String teamName) {
        return getTeamMembersCount(teamName) - getTeamOnlineCount(teamName);
    }

    public int getAllyOfflineCount(String teamName) {
        return getAllyMembersCount(teamName) - getAllyOnlineCount(teamName);
    }

    public int getTeamPosition(String teamName) {
        List<String> teams = new ArrayList<>(teamsConfig.getConfigurationSection("teams").getKeys(false));
        teams.sort((a, b) -> teamStats.get(b.toLowerCase()).getOrDefault("kills", 0) - teamStats.get(a.toLowerCase()).getOrDefault("kills", 0));
        return teams.indexOf(teamName.toLowerCase()) + 1;
    }

    public int getAllyPosition(String teamName) {
        String ally = getAllyName(teamName);
        return ally.isEmpty() ? 0 : getTeamPosition(ally);
    }
}