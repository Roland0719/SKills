package org.roland0719.sKills.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.roland0719.sKills.SKills;
import org.roland0719.sKills.utils.Messages;

public class SKillsCommand implements CommandExecutor, TabCompleter {
   private final Messages messages;
   private final SKills plugin;

   public SKillsCommand(SKills plugin) {
      this.plugin = plugin;
      this.messages = new Messages(plugin.getMessages());
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         this.messages.send(sender, "help");
         return true;
      } else {
         String var5 = args[0].toLowerCase();
         byte var6 = -1;
         switch(var5.hashCode()) {
         case -934641255:
            if (var5.equals("reload")) {
               var6 = 0;
            }
            break;
         case -350345742:
            if (var5.equals("resetall")) {
               var6 = 2;
            }
            break;
         case 113762:
            if (var5.equals("set")) {
               var6 = 4;
            }
            break;
         case 3198785:
            if (var5.equals("help")) {
               var6 = 1;
            }
            break;
         case 108404047:
            if (var5.equals("reset")) {
               var6 = 3;
            }
            break;
         case 109757599:
            if (var5.equals("stats")) {
               var6 = 5;
            }
         }

         int value;
         OfflinePlayer target;
         switch(var6) {
         case 0:
            if (!sender.hasPermission("skills.reload")) {
               this.messages.send(sender, "error.no-permission");
               return true;
            }

            this.plugin.reloadConfig();
            this.plugin.reloadMessages();
            this.messages.send(sender, "success.reload");
            return true;
         case 1:
            if (!sender.hasPermission("skills.help")) {
               this.messages.send(sender, "error.no-permission");
               return true;
            }

            this.messages.send(sender, "help");
            return true;
         case 2:
            if (!sender.hasPermission("skills.resetall")) {
               this.messages.send(sender, "error.no-permission");
               return true;
            }

            this.plugin.getStats().resetAllStats();
            this.messages.send(sender, "success.resetall.sender");
            return true;
         case 3:
            if (!sender.hasPermission("skills.reset")) {
               this.messages.send(sender, "error.no-permission");
               return true;
            } else if (args.length < 2) {
               this.messages.send(sender, "error.usage-reset");
               return true;
            } else {
               target = this.plugin.getServer().getOfflinePlayer(args[1]);
               if (!target.hasPlayedBefore() && !target.isOnline()) {
                  this.messages.send(sender, "error.player-not-found");
                  return true;
               }

               this.plugin.getStats().resetStats(target.getUniqueId());
               this.messages.send(sender, "success.reset.sender", Map.of("player", target.getName()));
               if (target.isOnline()) {
                  this.messages.send((Player)target, "success.reset.target");
               }

               return true;
            }
         case 4:
            if (!sender.hasPermission("skills.set")) {
               this.messages.send(sender, "error.no-permission");
               return true;
            } else if (args.length < 4) {
               this.messages.send(sender, "error.usage-set");
               return true;
            } else {
               target = this.plugin.getServer().getOfflinePlayer(args[1]);
               if (!target.hasPlayedBefore() && !target.isOnline()) {
                  this.messages.send(sender, "error.player-not-found");
                  return true;
               } else {
                  String type = args[2].toLowerCase();

                  try {
                     value = Integer.parseInt(args[3]);
                     if (value < 0) {
                        this.messages.send(sender, "error.invalid-number");
                        return true;
                     }
                  } catch (NumberFormatException var13) {
                     this.messages.send(sender, "error.invalid-number");
                     return true;
                  }

                  byte var12 = -1;
                  switch(type.hashCode()) {
                  case -1335772033:
                     if (type.equals("deaths")) {
                        var12 = 3;
                     }
                     break;
                  case 3291998:
                     if (type.equals("kill")) {
                        var12 = 0;
                     }
                     break;
                  case 95457908:
                     if (type.equals("death")) {
                        var12 = 2;
                     }
                     break;
                  case 102052053:
                     if (type.equals("kills")) {
                        var12 = 1;
                     }
                  }

                  String typeName;
                  switch(var12) {
                  case 0:
                  case 1:
                     this.plugin.getStats().setKills(target.getUniqueId(), value);
                     typeName = "kills";
                     break;
                  case 2:
                  case 3:
                     this.plugin.getStats().setDeaths(target.getUniqueId(), value);
                     typeName = "deaths";
                     break;
                  default:
                     this.messages.send(sender, "error.usage-set");
                     return true;
                  }

                  this.messages.send(sender, "success.set.sender", Map.of("player", target.getName(), "type", typeName, "value", String.valueOf(value)));
                  if (target.isOnline()) {
                     this.messages.send((Player)target, "success.set.target", Map.of("type", type, "value", String.valueOf(value)));
                  }

                  return true;
               }
            }
         case 5:
            if (!(sender instanceof Player)) {
               this.messages.send(sender, "error.not-console");
               return true;
            } else if (!sender.hasPermission("skills.stats")) {
               this.messages.send(sender, "error.no-permission");
               return true;
            } else {
               Player viewer = (Player)sender;
               Object target;
               if (args.length >= 2) {
                  target = this.plugin.getServer().getOfflinePlayer(args[1]);
                  if (!((OfflinePlayer)target).hasPlayedBefore() && !((OfflinePlayer)target).isOnline()) {
                     this.messages.send(sender, "error.player-not-found");
                     return true;
                  }
               } else {
                  target = viewer;
               }

               int kills = this.plugin.getStats().getKills(((OfflinePlayer)target).getUniqueId());
               value = this.plugin.getStats().getDeaths(((OfflinePlayer)target).getUniqueId());
               double kd;
               if (value == 0) {
                  kd = (double)kills;
               } else {
                  kd = (double)kills / (double)value;
               }

               this.messages.send(viewer, "stats", Map.of("player", ((OfflinePlayer)target).getName(), "kills", String.valueOf(kills), "deaths", String.valueOf(value), "kd", String.format(Locale.US, "%.2f", kd)));
               return true;
            }
         default:
            this.messages.send(sender, "help");
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (!command.getName().equalsIgnoreCase("skills")) {
         return Collections.emptyList();
      } else {
         ArrayList players;
         if (args.length == 1) {
            players = new ArrayList();
            if (sender.hasPermission("skills.reload")) {
               players.add("reload");
            }

            if (sender.hasPermission("skills.help")) {
               players.add("help");
            }

            if (sender.hasPermission("skills.reset")) {
               players.add("reset");
            }

            if (sender.hasPermission("skills.resetall")) {
               players.add("resetall");
            }

            if (sender.hasPermission("skills.set")) {
               players.add("set");
            }

            if (sender.hasPermission("skills.stats")) {
               players.add("stats");
            }

            return players;
         } else {
            OfflinePlayer[] var6;
            int var7;
            int var8;
            OfflinePlayer p;
            if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
               if (!sender.hasPermission("skills.stats")) {
                  return Collections.emptyList();
               } else {
                  players = new ArrayList();
                  var6 = this.plugin.getServer().getOfflinePlayers();
                  var7 = var6.length;

                  for(var8 = 0; var8 < var7; ++var8) {
                     p = var6[var8];
                     if (p.getName() != null) {
                        players.add(p.getName());
                     }
                  }

                  return players;
               }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
               if (!sender.hasPermission("skills.reset")) {
                  return Collections.emptyList();
               } else {
                  players = new ArrayList();
                  var6 = this.plugin.getServer().getOfflinePlayers();
                  var7 = var6.length;

                  for(var8 = 0; var8 < var7; ++var8) {
                     p = var6[var8];
                     if (p.getName() != null) {
                        players.add(p.getName());
                     }
                  }

                  return players;
               }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
               if (!sender.hasPermission("skills.set")) {
                  return Collections.emptyList();
               } else {
                  players = new ArrayList();
                  var6 = this.plugin.getServer().getOfflinePlayers();
                  var7 = var6.length;

                  for(var8 = 0; var8 < var7; ++var8) {
                     p = var6[var8];
                     if (p.getName() != null) {
                        players.add(p.getName());
                     }
                  }

                  return players;
               }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
               return !sender.hasPermission("skills.set") ? Collections.emptyList() : List.of("kill", "death");
            } else {
               return Collections.emptyList();
            }
         }
      }
   }
}
