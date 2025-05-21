package it.blacked.lifestealcore.placeholders;

import it.blacked.lifestealcore.LifeCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Locale;

public class LifeCoreExpansion extends PlaceholderExpansion {

    private final LifeCore plugin;

    public LifeCoreExpansion(LifeCore plugin) {
        this.plugin = plugin;
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
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("hearts")) {
            return String.valueOf(plugin.getHeartManager().getPlayerHearts(player.getUniqueId()));
        }

        if (params.equalsIgnoreCase("max_hearts")) {
            return String.valueOf(plugin.getConfigManager().getMaxHearts());
        }

        if (params.equalsIgnoreCase("is_banned")) {
            return String.valueOf(plugin.getBanManager().isPlayerBanned(player.getUniqueId()));
        }

        if (params.equalsIgnoreCase("ban_time")) {
            if (plugin.getBanManager().isPlayerBanned(player.getUniqueId())) {
                return plugin.getBanManager().getRemainingBanTime(player.getUniqueId());
            }
            return "0";
        }

        if (params.equalsIgnoreCase("suicide_count")) {
            return String.valueOf(plugin.getHeartManager().getSuicideCounter(player.getUniqueId()));
        }

        if (params.equalsIgnoreCase("remaining_suicides")) {
            int current = plugin.getHeartManager().getSuicideCounter(player.getUniqueId());
            int max = plugin.getConfigManager().getSuicideBanCount();
            return String.valueOf(max - current);
        }

        if (params.equalsIgnoreCase("heart_price")) {
            return formatNumber(plugin.getConfigManager().getHeartPrice());
        }

        if (params.equalsIgnoreCase("unban_price")) {
            return formatNumber(plugin.getConfigManager().getUnbanPrice());
        }

        if (params.equalsIgnoreCase("balance")) {
            if (plugin.getEconomy() != null) {
                return formatNumber(plugin.getEconomy().getBalance(player));
            }
            return "0";
        }

        if (params.equalsIgnoreCase("enough_heart")) {
            if (plugin.getEconomy() != null) {
                double balance = plugin.getEconomy().getBalance(player);
                double price = plugin.getConfigManager().getHeartPrice();
                return balance >= price ? "§a✓ §7Clicca per acquistare un cuore!" : "§c✘ §7Non hai abbastanza soldi!";
            }
            return "§c✘ §7Economy non trovato!";
        }

        if (params.equalsIgnoreCase("enough_unban")) {
            if (plugin.getEconomy() != null) {
                double balance = plugin.getEconomy().getBalance(player);
                double price = plugin.getConfigManager().getUnbanPrice();
                return balance >= price ? "§a✓ §7Clicca per acquistare l'unban!" : "§c✘ §7Non hai abbastanza soldi!";
            }
            return "§c✘ §7Economy non trovato!";
        }

        return null;
    }

    private String formatNumber(double number) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.ITALY);
        return format.format(number);
    }
}