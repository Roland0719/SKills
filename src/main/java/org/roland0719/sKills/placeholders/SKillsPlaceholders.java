package org.roland0719.sKills.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.roland0719.sKills.SKills;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SKillsPlaceholders extends PlaceholderExpansion {
    private final SKills plugin;
    public SKillsPlaceholders(SKills plugin) {
        this.plugin = plugin;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "skills";
    }
    @Override
    public @NotNull String getAuthor() {
        return "minecraft.roli.";
    }
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        String[] args = identifier.toLowerCase().split("_");
        if (args.length == 1) {
            switch (args[0]) {
                case "kill":
                case "kills":
                    return String.valueOf(plugin.getStats().getKills(player.getUniqueId()));
                case "death":
                case "deaths":
                    return String.valueOf(plugin.getStats().getDeaths(player.getUniqueId()));
                case "kd": {
                    int kills = plugin.getStats().getKills(player.getUniqueId());
                    int deaths = plugin.getStats().getDeaths(player.getUniqueId());
                    double kd = deaths == 0 ? kills : (double) kills / deaths;
                    return String.format(Locale.US, "%.2f", kd);
                }
            }
            return "";
        }
        if (args.length == 4 && args[0].equals("leaderboard")) {
            return handleLeaderboardPlaceholder(args);
        }
        return "";
    }

    private String handleLeaderboardPlaceholder(String[] args) {
        String type = args[1];
        String resultType = args[3];
        int position;
        try {
            position = Integer.parseInt(args[2]) - 1;
        } catch (NumberFormatException e) {
            return "";
        }
        List<UUID> leaderboard;
        if (type.equals("kd")) {
            leaderboard = plugin.getStats().getTopKD(10);
        } else if (type.equals("kills")) {
            leaderboard = plugin.getStats().getTopKills(10);
        } else if (type.equals("deaths")) {
            leaderboard = plugin.getStats().getTopDeaths(10);
        } else {
            return "";
        }
        if (position < 0 || position >= leaderboard.size()) {
            return getEmptyValue(resultType);
        }
        UUID uuid = leaderboard.get(position);
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(uuid);
        if (resultType.equals("name")) {
            return target.getName() != null
                    ? colorize(target.getName())
                    : getEmptyValue("name");
        }
        if (resultType.equals("value")) {
            if (type.equals("kills")) {
                return String.valueOf(plugin.getStats().getKills(uuid));
            }
            if (type.equals("deaths")) {
                return String.valueOf(plugin.getStats().getDeaths(uuid));
            }
            if (type.equals("kd")) {
                double kd = plugin.getStats().getKD(uuid);
                return String.format(Locale.US, "%.2f", kd);
            }
        }
        return "";
    }

    private String getEmptyValue(String resultType) {
        String value;
        if (resultType.equals("name")) {
            value = plugin.getConfig().getString(
                    "leaderboard.empty-name",
                    "&7---"
            );
        } else if (resultType.equals("value")) {
            value = plugin.getConfig().getString(
                    "leaderboard.empty-value",
                    "&70.00"
            );
        } else {
            return "";
        }
        return colorize(value);
    }
    private String colorize(String text) {
        if (text == null) return "";
        text = text.replaceAll(
                "(?i)#([0-9a-f]{6})",
                "§x§$1".replaceAll("(.)", "§$1")
        );
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
