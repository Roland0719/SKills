package org.roland0719.sKills;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.roland0719.sKills.command.SKillsCommand;
import org.roland0719.sKills.database.DatabaseManager;
import org.roland0719.sKills.database.PlayerStatsDAO;
import org.roland0719.sKills.listeners.DeathMessage;
import org.roland0719.sKills.listeners.DeathReward;
import org.roland0719.sKills.placeholders.SKillsPlaceholders;
import org.roland0719.sKills.utils.UpdateChecker;
import org.roland0719.sKills.utils.UpdateJoinListener;

public final class SKills extends JavaPlugin implements Listener {
   private FileConfiguration messages;
   private File messagesFile;
   private DatabaseManager database;
   private PlayerStatsDAO stats;

   public void onEnable() {
      this.saveDefaultMessages();
      this.saveDefaultConfig();
      FileConfiguration config = this.getConfig();
      this.database = new DatabaseManager(this);
      this.stats = new PlayerStatsDAO(this.database);
      this.getServer().getPluginManager().registerEvents(this, this);
      this.getServer().getPluginManager().registerEvents(new DeathMessage(this, this.messages), this);
      this.getServer().getPluginManager().registerEvents(new DeathReward(config), this);
      if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
         (new SKillsPlaceholders(this)).register();
      }

      UpdateChecker updateChecker = new UpdateChecker(this, "Roland0719/SKills");
      this.getServer().getPluginManager().registerEvents(new UpdateJoinListener(this, updateChecker), this);
      if (this.getConfig().getBoolean("update-checker", true)) {
         updateChecker.checkForUpdates();
      }

      SKillsCommand KillsCommand = new SKillsCommand(this);
      this.getCommand("skills").setExecutor(KillsCommand);
      this.getCommand("skills").setTabCompleter(KillsCommand);
      this.getLogger().info("SKills » The plugin successfully started.");
   }

   public void onDisable() {
      this.getLogger().info("SKills » Plugin disabled.");
   }

   private void saveDefaultMessages() {
      if (!this.getDataFolder().exists()) {
         this.getDataFolder().mkdirs();
      }

      this.messagesFile = new File(this.getDataFolder(), "messages.yml");
      if (!this.messagesFile.exists()) {
         this.saveResource("messages.yml", false);
      }

      this.messages = YamlConfiguration.loadConfiguration(this.messagesFile);
   }

   public void reloadMessages() {
      this.messages = YamlConfiguration.loadConfiguration(this.messagesFile);
   }

   public FileConfiguration getMessages() {
      return this.messages;
   }

   public int getKills(Player p) {
      return this.stats.getKills(p.getUniqueId());
   }

   public int getDeaths(Player p) {
      return this.stats.getDeaths(p.getUniqueId());
   }

   public PlayerStatsDAO getStats() {
      return this.stats;
   }
}
