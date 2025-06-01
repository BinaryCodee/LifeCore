package it.blacked.lifestealcore;

import it.blacked.lifestealcore.commands.*;
import it.blacked.lifestealcore.events.*;
import it.blacked.lifestealcore.managers.*;
import it.blacked.lifestealcore.placeholders.LifeCoreExpansion;
import it.blacked.lifestealcore.placeholders.TeamsExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class LifeCore extends JavaPlugin {

    private static LifeCore instance;
    private static ConfigManager configManager;
    private static HeartManager heartManager;
    private static BanManager banManager;
    private static Economy economy;
    private SpawnManager spawnManager;
    private static RTPManager rtpManager;
    private static TeamsManager teamsManager;

    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        if (configManager.isEconomyEnabled() && !setupEconomy()) {
            getLogger().severe("Vault non trovato.");
        }
        this.spawnManager = new SpawnManager(this);
        this.heartManager = new HeartManager(this);
        this.banManager = new BanManager(this);
        this.rtpManager = new RTPManager(this);
        this.teamsManager = new TeamsManager(this);
        getServer().getPluginManager().registerEvents(new RTPInventoryClickListener(this), this);
        registerCommands();
        registerEvents();
        registerPlaceholders();
        getLogger().info("LifeCore v" + getDescription().getVersion() + " Enabled!");
    }

    @Override
    public void onDisable() {
        if (rtpManager != null) {
            rtpManager.cleanup();
        }
        getLogger().info("LifeCore v" + getDescription().getVersion() + " Disabled!");
    }

    private void registerCommands() {
        getCommand("lifecore").setExecutor(new LifeCoreCommand(this));
        getCommand("lifecoreadmin").setExecutor(new LifeCoreAdminCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("rtp").setExecutor(new RTPCommand(this, configManager));
        getCommand("randomtp").setExecutor(new RTPCommand(this, configManager));
        getCommand("wild").setExecutor(new RTPCommand(this, configManager));
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("ping").setExecutor(new PingCommand(this));
        getCommand("sellgui").setExecutor(new SellGuiCommand(this));
        getCommand("teams").setExecutor(new TeamsCommand(this));
        getCommand("teamsadmin").setExecutor(new TeamsAdminCommand(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerStatsDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerBannedListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRTPListener(this, configManager), this);
        getServer().getPluginManager().registerEvents(new PlayerSpawnListener(this, configManager), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopInventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new CreativeDestroySlotListener(), this);
        getServer().getPluginManager().registerEvents(new SellGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new BanListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamsPlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamsChatListener(this), this);
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LifeCoreExpansion(this).register();
            new TeamsExpansion(this, teamsManager).register();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static LifeCore getInstance() {
        return instance;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static HeartManager getHeartManager() {
        return heartManager;
    }

    public static BanManager getBanManager() {
        return banManager;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public static RTPManager getRtpManager() {
        return rtpManager;
    }

    public static TeamsManager getTeamsManager() {
        return teamsManager;
    }
}