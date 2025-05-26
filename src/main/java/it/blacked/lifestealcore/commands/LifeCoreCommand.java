package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.gui.BuyMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LifeCoreCommand implements CommandExecutor {

    private final LifeCore plugin;
    private final SpawnCommand spawnCommand;

    public LifeCoreCommand(LifeCore plugin) {
        this.plugin = plugin;
        this.spawnCommand = new SpawnCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("buy")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player_only"));
                return true;
            }

            Player player = (Player) sender;
            new BuyMenu(plugin, player).open();
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            return spawnCommand.onCommand(sender, command, label, new String[0]);
        }

        sendHelpMessage(sender);
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§cThis server is running LifestealCore v1.2 - By blacked10469");
        sender.sendMessage("");
        sender.sendMessage(plugin.getConfigManager().getMessage("help_header"));
        sender.sendMessage(plugin.getConfigManager().getMessage("help_command"));
        sender.sendMessage(plugin.getConfigManager().getMessage("buy_command"));
        sender.sendMessage(plugin.getConfigManager().getMessage("spawn_command"));

        if (sender.hasPermission(plugin.getConfigManager().getCommandPermission("lifecoreadmin"))) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin_help_command"));
        }

        sender.sendMessage("");
    }
}