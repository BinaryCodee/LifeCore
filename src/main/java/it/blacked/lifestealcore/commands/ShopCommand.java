package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.managers.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    private final LifeCore plugin;
    private final ShopManager shopManager;

    public ShopCommand(LifeCore plugin) {
        this.plugin = plugin;
        this.shopManager = new ShopManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_only"));
            return true;
        }
        Player player = (Player) sender;
        shopManager.openMainShop(player);
        return true;
    }
}