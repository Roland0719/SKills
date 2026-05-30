package org.roland0719.sKills.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messages {
    private final FileConfiguration messages;
    public Messages(FileConfiguration messages) {
        this.messages = messages;
    }
    public void send(CommandSender sender, String path) {
        send(sender, path, Map.of());
    }
    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        if (messages.isList(path)) {
            List<String> list = messages.getStringList(path);
            for (String line : list) {
                sender.sendMessage(format(line, placeholders));
            }
            return;
        }
        String msg = messages.getString(path);
        if (msg != null) {
            sender.sendMessage(format(msg, placeholders));
        }
    }
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    private String format(String text, Map<String, String> placeholders) {
        String prefix = messages.getString("prefix", "");
        text = text.replace("%prefix%", prefix);
        for (var entry : placeholders.entrySet()) {
            text = text.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String color = matcher.group();
            text = text.replace(color,
                    net.md_5.bungee.api.ChatColor.of(color).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
