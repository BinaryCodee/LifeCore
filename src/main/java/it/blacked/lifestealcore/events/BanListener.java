package it.blacked.lifestealcore.events;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.commands.SpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BanListener implements Listener {

    private final LifeCore plugin;

    public BanListener(LifeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBanned(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        if (command.startsWith("/ban ") || command.startsWith("/tempban ") ||
                command.startsWith("/minecraft:ban ") || command.startsWith("/lifecoreadmin ban ")) {
            String[] args = command.split(" ");
            if (args.length < 2) return;

            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);

            if (target != null && target.isOnline()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (plugin.getBanManager().isPlayerBanned(target.getUniqueId())) {
                        SpawnCommand spawnCommand = new SpawnCommand(plugin);
                        spawnCommand.teleportToSpawn(target);
                    }
                }, 5L);
            }
        }
    }
}