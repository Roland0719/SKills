package org.roland0719.sKills.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    private final JavaPlugin plugin;
    private final String repo;
    private boolean updateAvailable = false;
    private String latestVersion = "";
    public UpdateChecker(JavaPlugin plugin, String repo) {
        this.plugin = plugin;
        this.repo = repo;
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/" + repo + "/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", plugin.getName());
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                while (scanner.hasNext()) {
                    json.append(scanner.nextLine());
                }
                scanner.close();
                latestVersion = extractTagName(json.toString());
                if (latestVersion == null) return;
                String currentVersion = plugin.getDescription().getVersion();
                updateAvailable = !normalize(currentVersion).equals(normalize(latestVersion));
                if (updateAvailable) {
                    plugin.getLogger().warning("SKills » New update available! Current: "
                            + currentVersion + " Latest: " + latestVersion);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for updates!");
            }
        });
    }
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
    public String getLatestVersion() {
        return latestVersion;
    }
    private String extractTagName(String json) {
        int index = json.indexOf("\"tag_name\":\"");
        if (index == -1) return null;
        int start = index + 12;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
    private String normalize(String version) {
        return version.toLowerCase().replace("v", "");
    }
}
