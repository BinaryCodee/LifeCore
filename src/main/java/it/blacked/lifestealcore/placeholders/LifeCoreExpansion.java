package it.blacked.lifestealcore.placeholders;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.LifeCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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

        return null;
    }
}