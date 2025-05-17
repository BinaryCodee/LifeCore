package it.blacked.lifestealcore;

import it.blacked.lifestealcore.commands.LifeCoreAdminCommand;
import it.blacked.lifestealcore.commands.LifeCoreCommand;
import it.blacked.lifestealcore.events.InventoryClickListener;
import it.blacked.lifestealcore.events.PlayerDeathListener;
import it.blacked.lifestealcore.events.PlayerJoinQuitListener;
import it.blacked.lifestealcore.events.PlayerMoveListener;
import it.blacked.lifestealcore.managers.BanManager;
import it.blacked.lifestealcore.managers.ConfigManager;
import it.blacked.lifestealcore.managers.HeartManager;
import it.blacked.lifestealcore.placeholders.LifeCoreExpansion;
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

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.configManager.loadConfig();

        if (configManager.isEconomyEnabled() && !setupEconomy()) {
            getLogger().severe("Vault non trovato.");
        }

        this.heartManager = new HeartManager(this);
        this.banManager = new BanManager(this);

        registerCommands();
        registerEvents();
        registerPlaceholders();

        getLogger().info("LifeCore v" + getDescription().getVersion() + " Enabled!");
    }

    @Override
    public void onDisable() {
        heartManager.saveAllHearts();
        banManager.saveAllBans();
        getLogger().info("LifeCore v" + getDescription().getVersion() + " Disabled!");
    }

    private void registerCommands() {
        getCommand("lifecore").setExecutor(new LifeCoreCommand(this));
        getCommand("lifecoreadmin").setExecutor(new LifeCoreAdminCommand(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LifeCoreExpansion(this).register();
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
}