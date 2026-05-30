package org.roland0719.sKills.listeners;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeathLighting implements Listener {
    private final JavaPlugin plugin;
    private final FileConfiguration messages;
    public DeathLighting(JavaPlugin plugin, FileConfiguration messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (plugin.getConfig().getBoolean("death-lightning", true)) {
          Player dead = e.getEntity();
          World world = dead.getWorld();
          world.strikeLightning(dead.getLocation());
        }
    }
}
