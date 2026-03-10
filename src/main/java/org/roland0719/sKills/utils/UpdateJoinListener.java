package org.roland0719.sKills.utils;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.roland0719.sKills.SKills;

public class UpdateJoinListener implements Listener {
   private final SKills plugin;
   private final UpdateChecker updateChecker;
   private final Messages messages;

   public UpdateJoinListener(SKills plugin, UpdateChecker updateChecker) {
      this.plugin = plugin;
      this.updateChecker = updateChecker;
      this.messages = new Messages(plugin.getMessages());
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if (this.plugin.getConfig().getBoolean("update-checker", true)) {
         if (player.hasPermission("skills.update-checker")) {
            if (this.updateChecker.isUpdateAvailable()) {
               Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                  this.messages.send(player, "update-checker", Map.of("current", this.plugin.getDescription().getVersion(), "latest", this.updateChecker.getLatestVersion()));
               }, 40L);
            }
         }
      }
   }
}
