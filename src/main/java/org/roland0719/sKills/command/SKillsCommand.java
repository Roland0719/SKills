package org.roland0719.sKills.command;

import java.util.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.roland0719.sKills.utils.Messages;
import org.roland0719.sKills.SKills;

public class SKillsCommand implements CommandExecutor, TabCompleter {
    private Messages messages;
    private final SKills plugin;

    public SKillsCommand(SKills plugin) {
        this.plugin = plugin;
        this.messages = new Messages(plugin.getMessages());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messages.send(sender, "help");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("skills.reload")) {
                    messages.send(sender, "error.no-permission");
                    return true;
                }
                plugin.reloadConfig();
                plugin.reloadMessages();
                reloadMessages();
                messages.send(sender, "success.reload");
                return true;

            case "help":
                if (!sender.hasPermission("skills.help")) {
                    messages.send(sender, "error.no-permission");
                    return true;
                }
                messages.send(sender, "help");
                return true;

            case "resetall":
                if (!sender.hasPermission("skills.resetall")) {
                    messages.send(sender, "error.no-permission");
                    return true;
                }
                plugin.getStats().resetAllStats();
                messages.send(sender, "success.resetall.sender");
                return true;

            case "reset": {
                if (!sender.hasPermission("skills.reset")) {
                    messages.send(sender, "error.no-permission");
                    return true;
                }
                if (args.length < 2) {
                    messages.send(sender, "error.usage-reset");
                    return true;
                }
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);
                if (!target.hasPlayedBefore() && !target.isOnline()) {
                    messages.send(sender, "error.player-not-found");
                    return true;
                }
                plugin.getStats().resetStats(target.getUniqueId());
                messages.send(sender,
                        "success.reset.sender",
                        Map.of("player", target.getName()));
                if (target.isOnline()) {
                    messages.send((Player) target, "success.reset.target");
                }
                return true;
            }
            case "set": {
                if (!sender.hasPermission("skills.set")) {
                    messages.send(sender, "error.no-permission");
                    return true;
                }
                if (args.length < 4) {
                    messages.send(sender, "error.usage-set");
                    return true;
                }
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);
                if (!target.hasPlayedBefore() && !target.isOnline()) {
                    messages.send(sender, "error.player-not-found");
                    return true;
                }
                String type = args[2].toLowerCase();
                int value;
                try {
                    value = Integer.parseInt(args[3]);
                    if (value < 0) {
                        messages.send(sender, "error.invalid-number");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    messages.send(sender, "error.invalid-number");
                    return true;
                }
                switch (type) {
                    case "kill":
                    case "kills":
                        plugin.getStats().setKills(target.getUniqueId(), value);

                        messages.send(sender,
                                "success.set.sender",
                                Map.of(
                                        "player", target.getName(),
                                        "type", "kills",
                                        "value", String.valueOf(value)
                                )
                        );
                        if (target.isOnline()) {
                            messages.send((Player) target,
                                    "success.set.target",
                                    Map.of(
                                            "type", "kills",
                                            "value", String.valueOf(value)
                                    )
                            );
                        }
                        break;
                    case "death":
                    case "deaths":
                        plugin.getStats().setDeaths(target.getUniqueId(), value);
                        messages.send(sender,
                                "success.set.sender",
                                Map.of(
                                        "player", target.getName(),
                                        "type", "deaths",
                                        "value", String.valueOf(value)
                                )
                        );

                        if (target.isOnline()) {
                            messages.send((Player) target,
                                    "success.set.target",
                                    Map.of(
                                            "type", "deaths",
                                            "value", String.valueOf(value)
                                    )
                            );
                        }
                        break;
                    default:
                        messages.send(sender, "error.usage-set");
                        return true;
                }
                return true;
            }

            case "stats": {
                if (!(sender instanceof Player)) {
                    messages.send(sender, "error.not-console");
                    return true;
                }
                if (!sender.hasPermission("skills.stats")) {
                    messages.send(sender, "error.no-permission");
                    return true;
                }
                Player viewer = (Player) sender;
                OfflinePlayer target;
                if (args.length >= 2) {
                    target = plugin.getServer().getOfflinePlayer(args[1]);
                    if (!target.hasPlayedBefore() && !target.isOnline()) {
                        messages.send(sender, "error.player-not-found");
                        return true;
                    }
                } else {
                    target = viewer;
                }
                int kills = plugin.getStats().getKills(target.getUniqueId());
                int deaths = plugin.getStats().getDeaths(target.getUniqueId());
                double kd = deaths == 0
                        ? kills
                        : (double) kills / deaths;
                messages.send(viewer,
                        "stats",
                        Map.of(
                                "player", target.getName(),
                                "kills", String.valueOf(kills),
                                "deaths", String.valueOf(deaths),
                                "kd", String.format(Locale.US, "%.2f", kd)
                        )
                );
                return true;
            }
            default:
                messages.send(sender, "help");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("skills")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("skills.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("skills.help")) {
                completions.add("help");
            }
            if (sender.hasPermission("skills.reset")) {
                completions.add("reset");
            }
            if (sender.hasPermission("skills.resetall")) {
                completions.add("resetall");
            }
            if (sender.hasPermission("skills.set")) {
                completions.add("set");
            }
            if (sender.hasPermission("skills.stats")) {
                completions.add("stats");
            }
            return completions;
        }
        if (args.length == 2 &&
                (args[0].equalsIgnoreCase("stats")
                        || args[0].equalsIgnoreCase("reset")
                        || args[0].equalsIgnoreCase("set"))) {
            List<String> players = new ArrayList<>();
            for (OfflinePlayer p : plugin.getServer().getOfflinePlayers()) {
                if (p.getName() != null) {
                    players.add(p.getName());
                }
            }
            return players;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return List.of("kill", "death");
        }
        return Collections.emptyList();
    }
    public void reloadMessages() {
        this.messages = new Messages(plugin.getMessages());
    }
}
