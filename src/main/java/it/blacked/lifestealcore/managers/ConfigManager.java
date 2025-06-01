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

    private static RTPManager rtpManager;
    private static ConfigManager configManager;
    private final LifeCore plugin;
    private FileConfiguration config;
    private final Map<String, String> messages = new HashMap<>();
    private int maxHearts;
    private int defaultHearts;
    private int heartTransferCount;
    private int suicideHeartLoss;
    private int suicideBanCount;
    private String suicideBanTime;
    private String deathBanTime;
    private int startingHeartsAfterUnban;
    private boolean economyEnabled;
    private double heartPrice;
    private double unbanPrice;
    private Map<String, Object> buyMenuConfig;
    private Map<String, Object> unbanMenuConfig;
    private Map<String, String> commandPermissions;
    private Map<String, List<String>> commandAliases;
    private int spawnDelay;
    private boolean spawnTeleportTitleEnabled;
    private boolean spawnTeleportSoundEnabled;
    private String spawnTeleportSound;
    private double spawnTeleportSoundVolume;
    private double spawnTeleportSoundPitch;
    private boolean spawnCountdownTitleEnabled;
    private boolean spawnCountdownSoundEnabled;
    private String spawnCountdownSound;
    private double spawnCountdownSoundVolume;
    private double spawnCountdownSoundPitch;
    private boolean banTitleEnabled;
    private boolean banFreezeMessageEnabled;
    private boolean rtpEnabled;
    private int rtpCooldown;
    private int rtpMaxRadius;
    private int rtpMinRadius;
    private int rtpMaxAttempts;
    private int rtpDelay;
    private boolean rtpEconomyEnabled;
    private double rtpCost;
    private boolean rtpSoundEnabled;
    private String rtpSound;
    private double rtpSoundVolume;
    private double rtpSoundPitch;
    private String rtpMenuTitle;
    private Map<String, Map<String, Object>> rtpMenuItems;
    private Map<String, Object> rtpFillItem;
    private int rtpMenuRows;
    private Map<String, Map<String, Object>> rtpWorldsConfig;
    private Map<String, Map<String, Object>> shopCategories = new HashMap<>();
    private Map<String, Object> shopMainConfig = new HashMap<>();
    private Map<Integer, String> mainShopSlots = new HashMap<>();
    private int maxTeamHomes;
    private int maxTeamMembers;
    private boolean teamsEnabled;
    private boolean teamFriendlyFire;
    private boolean teamChatEnabled;
    private boolean allyChatEnabled;

    public ConfigManager(LifeCore plugin) {
        this.plugin = plugin;
        setConfigManager(this);
        loadConfig();
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public Map<String, Object> getShopMainConfig() {
        return shopMainConfig;
    }

    public static RTPManager getRtpManager() {
        return rtpManager;
    }

    public static void setConfigManager(ConfigManager configManager) {
        ConfigManager.configManager = configManager;
    }

    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        this.rtpMenuRows = config.getInt("rtp.menu.rows", 3);
        loadMessages();
        loadSettings();
        loadEconomySettings();
        loadGuiSettings();
        loadCommandSettings();
        loadSpawnSettings();
        loadBanSettings();
        loadRtpSettings();
        loadShopSettings();
        loadTeamSettings();
    }

    private void loadMessages() {
        if (config.getConfigurationSection("messages") != null) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                String message = config.getString("messages." + key);
                if (message != null) {
                    messages.put(key, ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        }
    }

    private void loadShopSettings() {
        String mainPath = "shop.main";
        shopMainConfig.put("title", config.getString(mainPath + ".title", "&2&lSHOP"));
        shopMainConfig.put("size", config.getInt(mainPath + ".size", 36));
        if (config.getConfigurationSection(mainPath + ".items") != null) {
            for (String key : config.getConfigurationSection(mainPath + ".items").getKeys(false)) {
                Map<String, Object> itemConfig = new HashMap<>();
                String itemPath = mainPath + ".items." + key;
                itemConfig.put("slot", config.getInt(itemPath + ".slot"));
                itemConfig.put("material", config.getString(itemPath + ".material"));
                itemConfig.put("name", config.getString(itemPath + ".name"));
                shopMainConfig.put(key, itemConfig);
                mainShopSlots.put((int) itemConfig.get("slot"), key);
            }
        }
        if (config.getConfigurationSection("shop.categories") != null) {
            for (String category : config.getConfigurationSection("shop.categories").getKeys(false)) {
                Map<String, Object> categoryConfig = new HashMap<>();
                String catPath = "shop.categories." + category;
                categoryConfig.put("size", config.getInt(catPath + ".size", 54));
                if (config.getConfigurationSection(catPath + ".pages") != null) {
                    Map<Integer, Map<String, Object>> pages = new HashMap<>();
                    for (String pageStr : config.getConfigurationSection(catPath + ".pages").getKeys(false)) {
                        int page = Integer.parseInt(pageStr);
                        Map<String, Object> pageConfig = new HashMap<>();
                        String pagePath = catPath + ".pages." + page + ".items";
                        if (config.getConfigurationSection(pagePath) != null) {
                            for (String itemKey : config.getConfigurationSection(pagePath).getKeys(false)) {
                                Map<String, Object> itemConfig = new HashMap<>();
                                String itemPath = pagePath + "." + itemKey;
                                itemConfig.put("slot", config.getInt(itemPath + ".slot"));
                                itemConfig.put("material", config.getString(itemPath + ".material"));
                                itemConfig.put("buy", config.getDouble(itemPath + ".buy", -1));
                                itemConfig.put("sell", config.getDouble(itemPath + ".sell", -1));
                                pageConfig.put(itemKey, itemConfig);
                            }
                        }
                        pages.put(page, pageConfig);
                    }
                    categoryConfig.put("pages", pages);
                } else {
                    Map<String, Object> items = new HashMap<>();
                    String itemsPath = catPath + ".items";
                    if (config.getConfigurationSection(itemsPath) != null) {
                        for (String itemKey : config.getConfigurationSection(itemsPath).getKeys(false)) {
                            Map<String, Object> itemConfig = new HashMap<>();
                            String itemPath = itemsPath + "." + itemKey;
                            itemConfig.put("slot", config.getInt(itemPath + ".slot"));
                            itemConfig.put("material", config.getString(itemPath + ".material"));
                            itemConfig.put("buy", config.getDouble(itemPath + ".buy", -1));
                            itemConfig.put("sell", config.getDouble(itemPath + ".sell", -1));
                            items.put(itemKey, itemConfig);
                        }
                    }
                    categoryConfig.put("items", items);
                }
                shopCategories.put(category, categoryConfig);
            }
        }
    }

    private void loadTeamSettings() {
        this.maxTeamHomes = config.getInt("teams.max_homes", 5);
        this.maxTeamMembers = config.getInt("teams.max_members", 20);
        this.teamsEnabled = config.getBoolean("teams.enabled", true);
        this.teamFriendlyFire = config.getBoolean("teams.friendly_fire", false);
        this.teamChatEnabled = config.getBoolean("teams.chat_enabled", true);
        this.allyChatEnabled = config.getBoolean("teams.ally_chat_enabled", true);
    }

    private void loadSettings() {
        this.maxHearts = config.getInt("settings.max_hearts", 30);
        this.defaultHearts = config.getInt("settings.default_hearts", 10);
        this.heartTransferCount = config.getInt("settings.heart_transfer_count", 1);
        this.suicideHeartLoss = config.getInt("settings.suicide_heart_loss", 1);
        this.suicideBanCount = config.getInt("settings.suicide_ban_count", 3);
        this.suicideBanTime = config.getString("settings.suicide_ban_time", "7d");
        this.deathBanTime = config.getString("settings.death_ban_time", "4d");
        this.startingHeartsAfterUnban = config.getInt("settings.starting_hearts_after_unban", 6);
        this.economyEnabled = config.getBoolean("settings.enable_economy", true);
    }

    private void loadEconomySettings() {
        this.heartPrice = config.getDouble("economy.heart_price", 75000.0);
        this.unbanPrice = config.getDouble("economy.unban_price", 300000.0);
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

    private void loadSpawnSettings() {
        this.spawnDelay = config.getInt("settings.spawn_teleport_delay", 5);
        this.spawnTeleportTitleEnabled = config.getBoolean("settings.spawn_teleport_title_enabled", true);
        this.spawnTeleportSoundEnabled = config.getBoolean("settings.spawn_teleport_sound_enabled", true);
        this.spawnTeleportSound = config.getString("settings.spawn_teleport_sound", "ENTITY_ENDERMAN_TELEPORT");
        this.spawnTeleportSoundVolume = config.getDouble("settings.spawn_teleport_sound_volume", 1.0);
        this.spawnTeleportSoundPitch = config.getDouble("settings.spawn_teleport_sound_pitch", 1.0);
        this.spawnCountdownTitleEnabled = config.getBoolean("settings.spawn_countdown_title_enabled", true);
        this.spawnCountdownSoundEnabled = config.getBoolean("settings.spawn_countdown_sound_enabled", true);
        this.spawnCountdownSound = config.getString("settings.spawn_countdown_sound", "BLOCK_NOTE_BLOCK_PLING");
        this.spawnCountdownSoundVolume = config.getDouble("settings.spawn_countdown_sound_volume", 1.0);
        this.spawnCountdownSoundPitch = config.getDouble("settings.spawn_countdown_sound_pitch", 1.0);
    }

    private void loadBanSettings() {
        this.banTitleEnabled = config.getBoolean("settings.ban_title_enabled", true);
        this.banFreezeMessageEnabled = config.getBoolean("settings.ban_freeze_message_enabled", true);
    }

    private void loadRtpSettings() {
        this.rtpEnabled = config.getBoolean("rtp.enabled", true);
        this.rtpCooldown = config.getInt("rtp.cooldown", 5);
        this.rtpMaxRadius = config.getInt("rtp.max_radius", 15000);
        this.rtpMinRadius = config.getInt("rtp.min_radius", 1000);
        this.rtpMaxAttempts = config.getInt("rtp.max_attempts", 20);
        this.rtpDelay = config.getInt("rtp.delay", 5);
        this.rtpEconomyEnabled = config.getBoolean("rtp.economy.enabled", false);
        this.rtpCost = config.getDouble("rtp.economy.cost", 0.0);
        this.rtpSoundEnabled = config.getBoolean("rtp.sound.enabled", true);
        this.rtpSound = config.getString("rtp.sound.sound", "ENTITY_ENDERMAN_TELEPORT");
        this.rtpSoundVolume = config.getDouble("rtp.sound.volume", 1.0);
        this.rtpSoundPitch = config.getDouble("rtp.sound.pitch", 1.2);
        this.rtpMenuTitle = ChatColor.translateAlternateColorCodes('&',
                config.getString("rtp.menu.title", "&0&l⟐ &b&lRANDOM TELEPORT &0&l⟐"));
        this.rtpMenuItems = new HashMap<>();
        if (config.getConfigurationSection("rtp.menu.items") != null) {
            for (String key : config.getConfigurationSection("rtp.menu.items").getKeys(false)) {
                Map<String, Object> itemConfig = new HashMap<>();
                String path = "rtp.menu.items." + key;
                itemConfig.put("world", config.getString(path + ".world"));
                itemConfig.put("slot", config.getInt(path + ".slot"));
                itemConfig.put("type", config.getString(path + ".type"));
                itemConfig.put("name", config.getString(path + ".name"));
                List<String> lore = new ArrayList<>();
                for (String line : config.getStringList(path + ".lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                itemConfig.put("lore", lore);
                rtpMenuItems.put(key, itemConfig);
            }
        }
        this.rtpFillItem = new HashMap<>();
        String fillPath = "rtp.menu.fill_item";
        rtpFillItem.put("material", config.getString(fillPath + ".material", "BLACK_STAINED_GLASS_PANE"));
        rtpFillItem.put("name", config.getString(fillPath + ".name", " "));
        this.rtpWorldsConfig = new HashMap<>();
        if (config.getConfigurationSection("rtp.worlds") != null) {
            for (String worldKey : config.getConfigurationSection("rtp.worlds").getKeys(false)) {
                Map<String, Object> worldConfig = new HashMap<>();
                String path = "rtp.worlds." + worldKey;
                worldConfig.put("enabled", config.getBoolean(path + ".enabled", true));
                worldConfig.put("max_radius", config.getInt(path + ".max_radius", 15000));
                worldConfig.put("min_radius", config.getInt(path + ".min_radius", 1000));
                worldConfig.put("max_y", config.getInt(path + ".max_y", 256));
                worldConfig.put("min_y", config.getInt(path + ".min_y", 64));
                worldConfig.put("safe_blocks", config.getStringList(path + ".safe_blocks"));
                worldConfig.put("unsafe_blocks", config.getStringList(path + ".unsafe_blocks"));
                rtpWorldsConfig.put(worldKey, worldConfig);
            }
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "&cMessaggio nel config.yml non trovato: " + key);
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }

    public boolean isBanCommandBlockedEnabled() {
        return config.getBoolean("settings.ban-block-commands", true);
    }

    public boolean isBanCustomChatFormatEnabled() {
        return config.getBoolean("settings.ban-chat-format", true);
    }

    public int getMaxHearts() {
        return maxHearts;
    }

    public int getDefaultHearts() {
        return defaultHearts;
    }

    public int getHeartTransferCount() {
        return heartTransferCount;
    }

    public int getSuicideHeartLoss() {
        return suicideHeartLoss;
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

    public boolean isBanTitleEnabled() {
        return banTitleEnabled;
    }

    public boolean isBanFreezeMessageEnabled() {
        return banFreezeMessageEnabled;
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
        return heartPrice;
    }

    public String getUnbanMenuTitle() {
        String title = (String) getConfigValue("gui.unban_menu.title", "&8Lifesteal Menu");
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    public double getUnbanPrice() {
        return unbanPrice;
    }

    public int getSpawnDelay() {
        return spawnDelay;
    }

    public boolean isSpawnTeleportTitleEnabled() {
        return spawnTeleportTitleEnabled;
    }

    public boolean isSpawnTeleportSoundEnabled() {
        return spawnTeleportSoundEnabled;
    }

    public String getSpawnTeleportSound() {
        return spawnTeleportSound;
    }

    public double getSpawnTeleportSoundVolume() {
        return spawnTeleportSoundVolume;
    }

    public double getSpawnTeleportSoundPitch() {
        return spawnTeleportSoundPitch;
    }

    public boolean isSpawnCountdownTitleEnabled() {
        return spawnCountdownTitleEnabled;
    }

    public boolean isSpawnCountdownSoundEnabled() {
        return spawnCountdownSoundEnabled;
    }

    public String getSpawnCountdownSound() {
        return spawnCountdownSound;
    }

    public double getSpawnCountdownSoundVolume() {
        return spawnCountdownSoundVolume;
    }

    public double getSpawnCountdownSoundPitch() {
        return spawnCountdownSoundPitch;
    }

    public boolean isRtpEnabled() {
        return rtpEnabled;
    }

    public int getRtpCooldown() {
        return rtpCooldown;
    }

    public int getRtpMaxRadius() {
        return rtpMaxRadius;
    }

    public int getRtpMinRadius() {
        return rtpMinRadius;
    }

    public int getRtpMaxAttempts() {
        return rtpMaxAttempts;
    }

    public int getRtpDelay() {
        return rtpDelay;
    }

    public boolean isRtpEconomyEnabled() {
        return rtpEconomyEnabled;
    }

    public double getRtpCost() {
        return rtpCost;
    }

    public boolean isRtpSoundEnabled() {
        return rtpSoundEnabled;
    }

    public String getRtpSound() {
        return rtpSound;
    }

    public double getRtpSoundVolume() {
        return rtpSoundVolume;
    }

    public double getRtpSoundPitch() {
        return rtpSoundPitch;
    }

    public String getRtpMenuTitle() {
        return rtpMenuTitle;
    }

    public void setRtpMenuTitle(String title) {
        this.rtpMenuTitle = title;
    }

    public int getRtpMenuRows() {
        return rtpMenuRows;
    }

    public Map<String, Map<String, Object>> getRtpMenuItems() {
        return rtpMenuItems;
    }

    public Map<String, Object> getRtpFillItem() {
        return rtpFillItem;
    }

    public Map<String, Map<String, Object>> getRtpWorldsConfig() {
        return rtpWorldsConfig;
    }

    public Object getConfigValue(String path, Object defaultValue) {
        Object value = config.get(path, defaultValue);
        return value != null ? value : defaultValue;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public int getMaxTeamHomes() {
        return maxTeamHomes;
    }

    public int getMaxTeamMembers() {
        return maxTeamMembers;
    }

    public boolean isTeamsEnabled() {
        return teamsEnabled;
    }

    public boolean isTeamFriendlyFire() {
        return teamFriendlyFire;
    }

    public boolean isTeamChatEnabled() {
        return teamChatEnabled;
    }

    public boolean isAllyChatEnabled() {
        return allyChatEnabled;
    }

    public Map<String, Map<String, Object>> getShopCategories() {
        return shopCategories;
    }

    public String getCategoryFromSlot(int slot) {
        return mainShopSlots.get(slot);
    }
}