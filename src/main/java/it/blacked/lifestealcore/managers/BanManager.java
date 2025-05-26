package it.blacked.lifestealcore.managers;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanManager {

    private final LifeCore plugin;
    private final File banFile;
    private FileConfiguration banConfig;
    private final Map<UUID, Long> bannedPlayers = new HashMap<>();

    public BanManager(LifeCore plugin) {
        this.plugin = plugin;
        this.banFile = new File(plugin.getDataFolder(), "bans.yml");
        loadBans();
    }

    private void loadBans() {
        if (!banFile.exists()) {
            try {
                banFile.getParentFile().mkdirs();
                banFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossibile creare il file bans.yml!");
                e.printStackTrace();
            }
        }

        banConfig = YamlConfiguration.loadConfiguration(banFile);

        if (banConfig.contains("bans")) {
            for (String uuidString : banConfig.getConfigurationSection("bans").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    long banEndTime = banConfig.getLong("bans." + uuidString);
                    if (banEndTime > System.currentTimeMillis()) {
                        bannedPlayers.put(uuid, banEndTime);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("UUID non valido nel file bans.yml: " + uuidString);
                }
            }
        }
    }

    private void saveBans() {
        banConfig.set("bans", null);

        for (Map.Entry<UUID, Long> entry : bannedPlayers.entrySet()) {
            banConfig.set("bans." + entry.getKey().toString(), entry.getValue());
        }

        try {
            banConfig.save(banFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossibile salvare il file bans.yml!");
            e.printStackTrace();
        }
    }

    public void banPlayer(UUID uuid, String time) {
        long duration = parseDuration(time);
        long endTime = System.currentTimeMillis() + duration;

        bannedPlayers.put(uuid, endTime);
        saveBans();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", formatTime(duration));
            player.sendMessage(plugin.getConfigManager().getMessage("banned_message", placeholders));
            if (plugin.getConfigManager().isBanTitleEnabled()) {
                String title = plugin.getConfigManager().getMessage("banned_title", placeholders);
                String subtitle = plugin.getConfigManager().getMessage("banned_subtitle", placeholders);

                player.sendTitle(title, subtitle, 20, 100, 20);
            }
        }
    }

    public void unbanPlayer(UUID uuid) {
        bannedPlayers.remove(uuid);
        saveBans();
        int startingHearts = plugin.getConfigManager().getStartingHeartsAfterUnban();
        plugin.getHeartManager().setPlayerHearts(uuid, startingHearts);
    }

    public boolean isPlayerBanned(UUID uuid) {
        if (!bannedPlayers.containsKey(uuid)) {
            return false;
        }

        long endTime = bannedPlayers.get(uuid);

        if (System.currentTimeMillis() > endTime) {
            bannedPlayers.remove(uuid);
            saveBans();
            return false;
        }

        return true;
    }

    public long getRemainingBanTime(UUID uuid) {
        if (!isPlayerBanned(uuid)) {
            return 0;
        }

        return Math.max(0, bannedPlayers.get(uuid) - System.currentTimeMillis());
    }

    public String formatTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append("d ");
        }

        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }

        if (minutes > 0 || hours > 0 || days > 0) {
            sb.append(minutes).append("m ");
        }

        sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public long parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return 0;
        }

        long totalMillis = 0;

        Pattern pattern = Pattern.compile("(\\d+)([dhms])");
        Matcher matcher = pattern.matcher(duration);

        while (matcher.find()) {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "d":
                    totalMillis += TimeUnit.DAYS.toMillis(amount);
                    break;
                case "h":
                    totalMillis += TimeUnit.HOURS.toMillis(amount);
                    break;
                case "m":
                    totalMillis += TimeUnit.MINUTES.toMillis(amount);
                    break;
                case "s":
                    totalMillis += TimeUnit.SECONDS.toMillis(amount);
                    break;
            }
        }

        return totalMillis;
    }
}