package org.roland0719.sKills.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.roland0719.sKills.SKills;

public final class DeathMessage implements Listener {
   private final FileConfiguration messages;
   private final SKills plugin;

   public DeathMessage(SKills plugin, FileConfiguration messages) {
      this.plugin = plugin;
      this.messages = messages;
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent e) {
      Player player = e.getPlayer();
      this.plugin.getStats().addDeath(player.getUniqueId());
      EntityDamageEvent var4 = player.getLastDamageCause();
      if (var4 instanceof EntityDamageByEntityEvent) {
         label40: {
            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent)var4;
            Entity damager = ev.getDamager();
            String killerName;
            String killerWeapon;
            char var10000;
            if (damager instanceof LivingEntity) {
               LivingEntity mob = (LivingEntity)damager;
               if (!(damager instanceof Player)) {
                  killerName = mob.getType().toString().replace("_", " ").toLowerCase();
                  var10000 = Character.toUpperCase(killerName.charAt(0));
                  killerName = var10000 + killerName.substring(1);
                  String rawMobMsgChat = this.messages.getString("death.mob.chat", "&c%player%&f was killed by a &a%mob%.");
                  rawMobMsgChat = rawMobMsgChat.replace("%player%", player.getDisplayName()).replace("%mob%", killerName);
                  e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', rawMobMsgChat));
                  killerWeapon = this.messages.getString("death.mob.actionbar", "&c%player%&f was killed by a &a%mob%.");
                  killerWeapon = killerWeapon.replace("%player%", player.getDisplayName()).replace("%mob%", killerName);
                  player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', killerWeapon)));
                  break label40;
               }
            }

            if (damager instanceof Player) {
               Player killer = (Player)damager;
               this.plugin.getStats().addKill(killer.getUniqueId());
               killerName = killer.getDisplayName();
               ItemStack weapon = killer.getInventory().getItemInMainHand();
               killerWeapon = weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName() ? weapon.getItemMeta().getDisplayName() : weapon.getType().toString().replace("_", " ").toLowerCase();
               var10000 = Character.toUpperCase(killerWeapon.charAt(0));
               killerWeapon = var10000 + killerWeapon.substring(1);
               String rawKilledMsgChat = this.messages.getString("death.killed.chat", "&c%player%&f was slain by &a%killer%&f using %weapon%.");
               rawKilledMsgChat = rawKilledMsgChat.replace("%player%", player.getDisplayName()).replace("%killer%", killerName).replace("%weapon%", killerWeapon);
               e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', rawKilledMsgChat));
               String rawKilledMsgActionbar = this.messages.getString("death.killed.actionbar", "&c%player%&f was slain by &a%killer%&f using %weapon%.");
               rawKilledMsgActionbar = rawKilledMsgActionbar.replace("%player%", player.getDisplayName()).replace("%killer%", killerName).replace("%weapon%", killerWeapon);
               player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', rawKilledMsgActionbar)));
            }
         }
      }

      String rawVoidMsgChat;
      String rawVoidMsgActionbar;
      if (player.getLastDamageCause().getCause() == DamageCause.FALL) {
         rawVoidMsgChat = this.messages.getString("death.fall.chat", "&c%player%&f hit the ground too hard.");
         rawVoidMsgChat = rawVoidMsgChat.replace("%player%", player.getDisplayName());
         e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', rawVoidMsgChat));
         rawVoidMsgActionbar = this.messages.getString("death.fall.actionbar", "&c%player%&f hit the ground too hard.");
         rawVoidMsgActionbar = rawVoidMsgActionbar.replace("%player%", player.getDisplayName());
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', rawVoidMsgActionbar)));
      }

      if (player.getLastDamageCause().getCause() == DamageCause.DROWNING) {
         rawVoidMsgChat = this.messages.getString("death.drowning.chat", "&c%player%&f drowned.");
         rawVoidMsgChat = rawVoidMsgChat.replace("%player%", player.getDisplayName());
         e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', rawVoidMsgChat));
         rawVoidMsgActionbar = this.messages.getString("death.drowning.actionbar", "&c%player%&f drowned.");
         rawVoidMsgActionbar = rawVoidMsgActionbar.replace("%player%", player.getDisplayName());
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', rawVoidMsgActionbar)));
      }

      if (player.getLastDamageCause().getCause() == DamageCause.LAVA) {
         rawVoidMsgChat = this.messages.getString("death.lava.chat", "&c%player%&f tried to swim in lava.");
         rawVoidMsgChat = rawVoidMsgChat.replace("%player%", player.getDisplayName());
         e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', rawVoidMsgChat));
         rawVoidMsgActionbar = this.messages.getString("death.lava.actionbar", "&c%player%&f tried to swim in lava.");
         rawVoidMsgActionbar = rawVoidMsgActionbar.replace("%player%", player.getDisplayName());
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', rawVoidMsgActionbar)));
      }

      if (player.getLastDamageCause().getCause() == DamageCause.VOID) {
         rawVoidMsgChat = this.messages.getString("death.void.chat", "&c%player%&f fell into the void and vanished.");
         rawVoidMsgChat = rawVoidMsgChat.replace("%player%", player.getDisplayName());
         e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', rawVoidMsgChat));
         rawVoidMsgActionbar = this.messages.getString("death.void.actionbar", "&c%player%&f fell into the void and vanished.");
         rawVoidMsgActionbar = rawVoidMsgActionbar.replace("%player%", player.getDisplayName());
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', rawVoidMsgActionbar)));
      }

   }
}
