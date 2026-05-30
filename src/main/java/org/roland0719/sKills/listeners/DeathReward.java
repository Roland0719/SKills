package org.roland0719.sKills.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.roland0719.sKills.SKills;

public final class DeathReward implements Listener {
    private final SKills plugin;
    public DeathReward(SKills plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("death-reward", true)) {
            return;
        }
        Player dead = e.getEntity();
        Player killer = dead.getKiller();
        ConfigurationSection rewardSection =
                config.getConfigurationSection("reward");
        if (rewardSection == null) {
            return;
        }
        int victimKills =
                plugin.getStats().getKills(dead.getUniqueId());
        ConfigurationSection victimSection =
                rewardSection.getConfigurationSection("victim");
        if (victimSection != null) {
            executeRewards(
                    victimSection,
                    victimKills,
                    dead,
                    killer
            );
        }
        if (killer != null) {
            int killerKills =
                    plugin.getStats().getKills(killer.getUniqueId());
            ConfigurationSection killerSection =
                    rewardSection.getConfigurationSection("killer");
            if (killerSection != null) {
                executeRewards(
                        killerSection,
                        killerKills,
                        killer,
                        dead
                );
            }
        }
    }

    private void executeRewards(
            ConfigurationSection section,
            int kills,
            Player player,
            Player victim
    ) {
        for (String key : section.getKeys(false)) {
            String range = section.getString(key + ".range");
            if (range == null || !range.contains("-")) {
                continue;
            }
            String[] split = range.split("-");
            int min;
            int max;
            try {
                min = Integer.parseInt(split[0]);
                max = Integer.parseInt(split[1]);
            } catch (NumberFormatException ex) {
                continue;
            }
            if (kills >= min && kills <= max) {
                for (String cmd : section.getStringList(key + ".commands")) {
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            cmd.replace("%player%", player.getName())
                                    .replace(
                                            "%victim%",
                                            victim != null
                                                    ? victim.getName()
                                                    : player.getName()
                                    )
                    );
                }
                break;
            }
        }
    }
}
