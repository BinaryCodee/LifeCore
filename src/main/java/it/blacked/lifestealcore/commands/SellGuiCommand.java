package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.holder.SellGuiHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SellGuiCommand implements CommandExecutor {
    private final LifeCore plugin;

    public SellGuiCommand(LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_only"));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("lifecore.sellgui")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfigManager().getMessage("no_permission")));
            return true;
        }

        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfigManager().getConfig().getString("messages.shop_sell_gui_title", "&c&lVendi Oggetti"));
        Inventory inv = Bukkit.createInventory(new SellGuiHolder(), 54, title);
        player.openInventory(inv);
        return true;
    }
}