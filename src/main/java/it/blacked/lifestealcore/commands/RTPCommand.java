package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.gui.RTPMenu;
import it.blacked.lifestealcore.managers.ConfigManager;
import it.blacked.lifestealcore.tasks.RTPTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RTPCommand implements CommandExecutor {

    private final LifeCore plugin;
    private final ConfigManager configManager;

    public RTPCommand(LifeCore plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(configManager.getMessage("player_only"));
            return true;
        }
        Player player = (Player) sender;
        if (!configManager.isRtpEnabled()) {
            player.sendMessage(configManager.getMessage("rtp_failed"));
            return true;
        }
        if (!player.hasPermission(configManager.getCommandPermission("rtp"))) {
            player.sendMessage(configManager.getMessage("no_permission"));
            return true;
        }
        if (args.length == 0) {
            new RTPMenu(plugin, player, configManager).open();
            return true;
        }
        String worldName = args[0].toLowerCase();
        if (!isValidWorld(worldName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("world", worldName);
            player.sendMessage(configManager.getMessage("rtp_world_not_found", placeholders));
            return true;
        }
        if (configManager.isRtpEconomyEnabled() && LifeCore.getEconomy() != null) {
            double cost = configManager.getRtpCost();
            Economy economy = LifeCore.getEconomy();
            if (!economy.has(player, cost)) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("cost", String.valueOf(cost));
                player.sendMessage(configManager.getMessage("rtp_not_enough_money", placeholders));
                return true;
            }
            economy.withdrawPlayer(player, cost);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("cost", String.valueOf(cost));
            player.sendMessage(configManager.getMessage("rtp_money_taken", placeholders));
        }
        long cooldown = LifeCore.getRtpManager().getCooldown(player);
        if (cooldown > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", String.valueOf(cooldown));
            player.sendMessage(configManager.getMessage("rtp_cooldown", placeholders));
            return true;
        }
        new RTPTask(plugin, player, worldName, configManager).runTaskTimer(plugin, 0L, 20L);
        return true;
    }

    private boolean isValidWorld(String worldName) {
        return plugin.getServer().getWorld(worldName) != null &&
                configManager.getRtpMenuItems().containsKey(worldName) &&
                (boolean) configManager.getRtpMenuItems().get(worldName).getOrDefault("enabled", true);
    }
}