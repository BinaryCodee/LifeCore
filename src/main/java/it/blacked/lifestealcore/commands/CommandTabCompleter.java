package it.blacked.lifestealcore.commands;

import it.blacked.lifestealcore.LifeCore;
import it.blacked.lifestealcore.gui.BuyMenu;
import it.blacked.lifestealcore.gui.UnbanMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTabCompleter implements TabCompleter {

    private final LifeCore plugin;

    public CommandTabCompleter(LifeCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("lifecore")) {
            if (args.length == 1) {
                completions.add("help");
                completions.add("buy");
                return filterCompletions(completions, args[0]);
            }
        } else if (command.getName().equalsIgnoreCase("lifecoreadmin")) {
            if (args.length == 1) {
                completions.add("help");
                completions.add("heart");
                return filterCompletions(completions, args[0]);
            } else if (args.length == 2 && args[0].equalsIgnoreCase("heart")) {
                completions.add("add");
                completions.add("remove");
                completions.add("list");
                return filterCompletions(completions, args[1]);
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("heart") &&
                        (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                    return getOnlinePlayerNames(args[2]);
                } else if (args[0].equalsIgnoreCase("heart") && args[1].equalsIgnoreCase("list")) {
                    completions.add("all");
                    completions.addAll(getOnlinePlayerNames(""));
                    return filterCompletions(completions, args[2]);
                }
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("heart") &&
                        (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                    completions.add("1");
                    completions.add("2");
                    completions.add("5");
                    completions.add("10");
                    return filterCompletions(completions, args[3]);
                }
            }
        }

        return completions;
    }

    private List<String> filterCompletions(List<String> completions, String arg) {
        if (arg == null || arg.isEmpty()) {
            return completions;
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> getOnlinePlayerNames(String arg) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> arg.isEmpty() || name.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
}