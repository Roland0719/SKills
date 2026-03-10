package org.roland0719.sKills.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {
   private final FileConfiguration messages;
   private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

   public Messages(FileConfiguration messages) {
      this.messages = messages;
   }

   public void send(CommandSender sender, String path) {
      this.send(sender, path, Map.of());
   }

   public void send(CommandSender sender, String path, Map<String, String> placeholders) {
      if (!this.messages.isList(path)) {
         String msg = this.messages.getString(path);
         if (msg != null) {
            sender.sendMessage(this.format(msg, placeholders));
         }

      } else {
         List<String> list = this.messages.getStringList(path);
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            String line = (String)var5.next();
            sender.sendMessage(this.format(line, placeholders));
         }

      }
   }

   private String format(String text, Map<String, String> placeholders) {
      String prefix = this.messages.getString("prefix", "");
      text = text.replace("%prefix%", prefix);

      Entry entry;
      for(Iterator var4 = placeholders.entrySet().iterator(); var4.hasNext(); text = text.replace("%" + (String)entry.getKey() + "%", (CharSequence)entry.getValue())) {
         entry = (Entry)var4.next();
      }

      String color;
      for(Matcher matcher = HEX_PATTERN.matcher(text); matcher.find(); text = text.replace(color, ChatColor.of(color).toString())) {
         color = matcher.group();
      }

      return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
   }
}
