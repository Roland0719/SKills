package org.roland0719.sKills.listeners;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class DeathReward implements Listener {
   private final FileConfiguration messages;

   public DeathReward(FileConfiguration messages) {
      this.messages = messages;
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent e) {
      Player dead = e.getEntity();
      Player killer = dead.getKiller();
      Iterator var4 = this.messages.getStringList("death.reward.victim").iterator();

      String cmd;
      while(var4.hasNext()) {
         cmd = (String)var4.next();
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", dead.getName()));
      }

      if (killer != null) {
         var4 = this.messages.getStringList("death.reward.killer").iterator();

         while(var4.hasNext()) {
            cmd = (String)var4.next();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", killer.getName()).replace("%killed%", dead.getName()));
         }
      }

   }
}
