package org.roland0719.sKills.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.roland0719.sKills.SKills;

public class DatabaseManager {
    private final SKills plugin;
    private Connection connection;
    public DatabaseManager(SKills plugin) {
        this.plugin = plugin;
        connect();
        createTable();
    }
    public Connection getConnection() {
        return connection;
    }
    private void connect() {
        try {
            String type = plugin.getConfig().getString("database.type");
            if (type.equalsIgnoreCase("SQLITE")) {
                File file = new File(plugin.getDataFolder(),
                        plugin.getConfig().getString("database.sqlite.file"));
                String url = "jdbc:sqlite:" + file;
                connection = DriverManager.getConnection(url);
            } else {
                String host = plugin.getConfig().getString("database.mysql.host");
                int port = plugin.getConfig().getInt("database.mysql.port");
                String db = plugin.getConfig().getString("database.mysql.database");
                String user = plugin.getConfig().getString("database.mysql.username");
                String pass = plugin.getConfig().getString("database.mysql.password");
                String url = "jdbc:mysql://" + host + ":" + port + "/" + db +
                        "?useSSL=false&autoReconnect=true";
                connection = DriverManager.getConnection(url, user, pass);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Database connection failed!");
            e.printStackTrace();
        }
    }
    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS player_stats (
                uuid VARCHAR(36) PRIMARY KEY,
                kills INT NOT NULL DEFAULT 0,
                deaths INT NOT NULL DEFAULT 0
            );
        """;
        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
