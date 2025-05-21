package it.blacked.lifestealcore.managers;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final LifeCore plugin;
    private FileConfiguration config;
    private final Map<String, String> messages = new HashMap<>();
    private int maxHearts;
    private int defaultHearts;
    private int suicideBanCount;
    private String suicideBanTime;
    private String deathBanTime;
    private int startingHeartsAfterUnban;
    private boolean economyEnabled;
    private int heartPrice;
    private int unbanPrice;
    private Map<String, Object> buyMenuConfig;
    private Map<String, Object> unbanMenuConfig;
    private Map<String, String> commandPermissions;
    private Map<String, List<String>> commandAliases;

    public ConfigManager(LifeCore plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }

        plugin.reloadConfig();
        this.config = plugin.getConfig();

        loadMessages();
        loadSettings();
        loadEconomySettings();
        loadGuiSettings();
        loadCommandSettings();
    }

    private void loadMessages() {
        if (config.getConfigurationSection("messages") != null) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&',
                        config.getString("messages." + key)));
            }
        }
    }

    private void loadSettings() {
        this.maxHearts = config.getInt("settings.max_hearts", 20);
        this.defaultHearts = config.getInt("settings.default_hearts", 10);
        this.suicideBanCount = config.getInt("settings.suicide_ban_count", 3);
        this.suicideBanTime = config.getString("settings.suicide_ban_time", "7d");
        this.deathBanTime = config.getString("settings.death_ban_time", "4d");
        this.startingHeartsAfterUnban = config.getInt("settings.starting_hearts_after_unban", 6);
        this.economyEnabled = config.getBoolean("settings.enable_economy", true);
    }

    private void loadEconomySettings() {
        this.heartPrice = config.getInt("economy.heart_price", 50000);
        this.unbanPrice = config.getInt("economy.unban_price", 150000);
    }

    private void loadGuiSettings() {
        this.buyMenuConfig = new HashMap<>();
        this.unbanMenuConfig = new HashMap<>();

        if (config.getConfigurationSection("gui.buy_menu") != null) {
            for (String key : config.getConfigurationSection("gui.buy_menu").getKeys(false)) {
                if (key.equals("unban_item") || key.equals("heart_item")) {
                    Map<String, Object> itemInfo = new HashMap<>();
                    for (String itemKey : config.getConfigurationSection("gui.buy_menu." + key).getKeys(false)) {
                        if (itemKey.equals("lore")) {
                            List<String> lore = new ArrayList<>();
                            for (String line : config.getStringList("gui.buy_menu." + key + ".lore")) {
                                lore.add(ChatColor.translateAlternateColorCodes('&', line));
                            }
                            itemInfo.put(itemKey, lore);
                        } else {
                            itemInfo.put(itemKey, config.getString("gui.buy_menu." + key + "." + itemKey));
                        }
                    }
                    buyMenuConfig.put(key, itemInfo);
                } else {
                    buyMenuConfig.put(key, config.get("gui.buy_menu." + key));
                }
            }
        }

        if (config.getConfigurationSection("gui.unban_menu") != null) {
            for (String key : config.getConfigurationSection("gui.unban_menu").getKeys(false)) {
                if (key.equals("crystal_item")) {
                    Map<String, Object> itemInfo = new HashMap<>();
                    for (String itemKey : config.getConfigurationSection("gui.unban_menu." + key).getKeys(false)) {
                        if (itemKey.equals("lore")) {
                            List<String> lore = new ArrayList<>();
                            for (String line : config.getStringList("gui.unban_menu." + key + ".lore")) {
                                lore.add(ChatColor.translateAlternateColorCodes('&', line));
                            }
                            itemInfo.put(itemKey, lore);
                        } else {
                            itemInfo.put(itemKey, config.getString("gui.unban_menu." + key + "." + itemKey));
                        }
                    }
                    unbanMenuConfig.put(key, itemInfo);
                } else {
                    unbanMenuConfig.put(key, config.get("gui.unban_menu." + key));
                }
            }
        }
    }

    private void loadCommandSettings() {
        this.commandPermissions = new HashMap<>();
        this.commandAliases = new HashMap<>();

        if (config.getConfigurationSection("commands") != null) {
            for (String command : config.getConfigurationSection("commands").getKeys(false)) {
                if (config.contains("commands." + command + ".permission")) {
                    commandPermissions.put(command, config.getString("commands." + command + ".permission"));
                }

                if (config.contains("commands." + command + ".aliases")) {
                    commandAliases.put(command, config.getStringList("commands." + command + ".aliases"));
                }
            }
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "&cMessage not found: " + key);
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return message;
    }

    public int getMaxHearts() {
        return maxHearts;
    }

    public int getDefaultHearts() {
        return defaultHearts;
    }

    public int getSuicideBanCount() {
        return suicideBanCount;
    }

    public String getSuicideBanTime() {
        return suicideBanTime;
    }

    public String getDeathBanTime() {
        return deathBanTime;
    }

    public int getStartingHeartsAfterUnban() {
        return startingHeartsAfterUnban;
    }

    public boolean isEconomyEnabled() {
        return economyEnabled;
    }

    public Map<String, Object> getBuyMenuConfig() {
        return buyMenuConfig;
    }

    public Map<String, Object> getUnbanMenuConfig() {
        return unbanMenuConfig;
    }

    public String getCommandPermission(String command) {
        return commandPermissions.getOrDefault(command, "");
    }

    public List<String> getCommandAliases(String command) {
        return commandAliases.getOrDefault(command, new ArrayList<>());
    }

    public String getBuyMenuTitle() {
        return ChatColor.translateAlternateColorCodes('&', (String) buyMenuConfig.get("title"));
    }

    public Map<String, Object> getHeartItemConfig() {
        return (Map<String, Object>) buyMenuConfig.get("heart_item");
    }

    public double getHeartPrice() {
        Object priceObj = config.get("economy.heart_price");
        if (priceObj instanceof Number) {
            return ((Number) priceObj).doubleValue();
        } else if (priceObj instanceof String) {
            try {
                return Double.parseDouble((String) priceObj);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Impossibile convertire il prezzo del cuore in un numero: " + priceObj);
                return 50000.0;
            }
        }
        return 50000.0;
    }

    public String getUnbanMenuTitle() {
        String title = (String) getConfigValue("gui.unban_menu.title", "&8Lifesteal Menu");
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    public double getUnbanPrice() {
        Object priceObj = config.get("economy.unban_price");
        if (priceObj instanceof Number) {
            return ((Number) priceObj).doubleValue();
        } else if (priceObj instanceof String) {
            try {
                return Double.parseDouble((String) priceObj);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Impossibile convertire il prezzo dell'unban in un numero: " + priceObj);
                return 150000.0;
            }
        }
        return 150000.0;
    }

    private Object getConfigValue(String path, Object defaultValue) {
        Object value = config;
        for (String part : path.split("\\.")) {
            if (!(value instanceof Map)) {
                return defaultValue;
            }
            value = ((Map<?, ?>) value).get(part);
            if (value == null) {
                return defaultValue;
            }
        }
        return value;
    }
}