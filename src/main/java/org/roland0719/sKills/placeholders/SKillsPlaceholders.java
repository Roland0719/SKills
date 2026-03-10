package org.roland0719.sKills.placeholders;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.roland0719.sKills.SKills;

public class SKillsPlaceholders extends PlaceholderExpansion {
   private final SKills plugin;

   public SKillsPlaceholders(SKills plugin) {
      this.plugin = plugin;
   }

   @NotNull
   public String getIdentifier() {
      return "skills";
   }

   @NotNull
   public String getAuthor() {
      return "minecraft.roli.";
   }

   @NotNull
   public String getVersion() {
      return "1.0.0";
   }

   public boolean persist() {
      return true;
   }

   public String onPlaceholderRequest(Player player, @NotNull String identifier) {
      if (player == null) {
         return "";
      } else {
         String[] args = identifier.toLowerCase().split("_");
         if (args.length == 1) {
            String var4 = args[0];
            byte var5 = -1;
            switch(var4.hashCode()) {
            case -1335772033:
               if (var4.equals("deaths")) {
                  var5 = 3;
               }
               break;
            case 3417:
               if (var4.equals("kd")) {
                  var5 = 4;
               }
               break;
            case 3291998:
               if (var4.equals("kill")) {
                  var5 = 0;
               }
               break;
            case 95457908:
               if (var4.equals("death")) {
                  var5 = 2;
               }
               break;
            case 102052053:
               if (var4.equals("kills")) {
                  var5 = 1;
               }
            }

            switch(var5) {
            case 0:
            case 1:
               return String.valueOf(this.plugin.getStats().getKills(player.getUniqueId()));
            case 2:
            case 3:
               return String.valueOf(this.plugin.getStats().getDeaths(player.getUniqueId()));
            case 4:
               int kills = this.plugin.getStats().getKills(player.getUniqueId());
               int deaths = this.plugin.getStats().getDeaths(player.getUniqueId());
               double kd = deaths == 0 ? (double)kills : (double)kills / (double)deaths;
               return String.format(Locale.US, "%.2f", kd);
            default:
               return "";
            }
         } else {
            return args.length == 4 && args[0].equals("leaderboard") ? this.handleLeaderboardPlaceholder(args) : "";
         }
      }
   }

   private String handleLeaderboardPlaceholder(String[] args) {
      String type = args[1];
      String resultType = args[3];

      int position;
      try {
         position = Integer.parseInt(args[2]) - 1;
      } catch (NumberFormatException var10) {
         return "";
      }

      List leaderboard;
      if (type.equals("kd")) {
         leaderboard = this.plugin.getStats().getTopKD(10);
      } else if (type.equals("kills")) {
         leaderboard = this.plugin.getStats().getTopKills(10);
      } else {
         if (!type.equals("deaths")) {
            return "";
         }

         leaderboard = this.plugin.getStats().getTopDeaths(10);
      }

      if (position >= 0 && position < leaderboard.size()) {
         UUID uuid = (UUID)leaderboard.get(position);
         OfflinePlayer target = this.plugin.getServer().getOfflinePlayer(uuid);
         if (resultType.equals("name")) {
            return target.getName() != null ? this.colorize(target.getName()) : this.getEmptyValue("name");
         } else {
            if (resultType.equals("value")) {
               if (type.equals("kills")) {
                  return String.valueOf(this.plugin.getStats().getKills(uuid));
               }

               if (type.equals("deaths")) {
                  return String.valueOf(this.plugin.getStats().getDeaths(uuid));
               }

               if (type.equals("kd")) {
                  double kd = this.plugin.getStats().getKD(uuid);
                  return String.format(Locale.US, "%.2f", kd);
               }
            }

            return "";
         }
      } else {
         return this.getEmptyValue(resultType);
      }
   }

   private String getEmptyValue(String resultType) {
      String value;
      if (resultType.equals("name")) {
         value = this.plugin.getConfig().getString("leaderboard.empty-name", "&7---");
      } else {
         if (!resultType.equals("value")) {
            return "";
         }

         value = this.plugin.getConfig().getString("leaderboard.empty-value", "&70.00");
      }

      return this.colorize(value);
   }

   private String colorize(String text) {
      if (text == null) {
         return "";
      } else {
         text = text.replaceAll("(?i)#([0-9a-f]{6})", "§x§$1".replaceAll("(.)", "§$1"));
         return ChatColor.translateAlternateColorCodes('&', text);
      }
   }
}
