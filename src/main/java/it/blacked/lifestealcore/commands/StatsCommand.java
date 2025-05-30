package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StatsCommand implements CommandExecutor {
    private final LifeCore plugin;
    private final Economy economy;
    private final File playersFile;
    private final FileConfiguration playersConfig;

    public StatsCommand(LifeCore plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomy();
        this.playersFile = new File(plugin.getDataFolder(), "players.yml");
        if (!playersFile.exists()) {
            try {
                playersFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossibile creare il file players.yml: " + e.getMessage());
            }
        }
        this.playersConfig = YamlConfiguration.loadConfiguration(playersFile);
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
                    plugin.getConfigManager().getMessage("invalid_usage").replace("%usage%", "/stats [player]")));
            return true;
        }

        OfflinePlayer target;
        String messageKey;
        boolean isSelf;

        if (args.length == 0) {
            if (!sender.hasPermission("lifecore.stats")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("no_permission")));
                return true;
            }
            target = (Player) sender;
            messageKey = "stats_self";
            isSelf = true;
        } else {
            if (!sender.hasPermission("lifecore.stats.others")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("no_permission")));
                return true;
            }
            target = plugin.getServer().getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfigManager().getMessage("player_not_found")));
                return true;
            }
            messageKey = "stats_other";
            isSelf = false;
        }
        double hearts = target.isOnline() ? target.getPlayer().getMaxHealth() / 2.0
                : plugin.getConfig().getInt("settings.default_hearts", 10);
        String uuid = target.getUniqueId().toString();
        int kills = playersConfig.getInt("players." + uuid + ".kills", 0);
        int deaths = playersConfig.getInt("players." + uuid + ".deaths", 0);
        double balance = economy.hasAccount(target) ? economy.getBalance(target) : 0.0;
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName());
        placeholders.put("hearts", String.format("%.1f", hearts));
        placeholders.put("kills", String.valueOf(kills));
        placeholders.put("deaths", String.valueOf(deaths));
        placeholders.put("balance", String.format("%.2f", balance));

        String message = plugin.getConfigManager().getMessage(messageKey, placeholders);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }
}