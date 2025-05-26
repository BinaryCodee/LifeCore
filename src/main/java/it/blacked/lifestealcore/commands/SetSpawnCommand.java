package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private final LifeCore plugin;

    public SetSpawnCommand(LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player_only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(plugin.getConfigManager().getCommandPermission("lifecoreadmin_setspawn"))) {
            player.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }

        plugin.getSpawnManager().setSpawnLocation(player.getLocation());
        player.sendMessage(plugin.getConfigManager().getMessage("spawn_set"));

        return true;
    }
}