package org.roland0719.sKills.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.roland0719.sKills.SKills;

public class DatabaseManager {
   private final SKills plugin;
   private Connection connection;

   public DatabaseManager(SKills plugin) {
      this.plugin = plugin;
      this.connect();
      this.createTable();
   }

   public Connection getConnection() {
      return this.connection;
   }

   private void connect() {
      try {
         String type = this.plugin.getConfig().getString("database.type");
         if (type.equalsIgnoreCase("SQLITE")) {
            File file = new File(this.plugin.getDataFolder(), this.plugin.getConfig().getString("database.sqlite.file"));
            String url = "jdbc:sqlite:" + String.valueOf(file);
            this.connection = DriverManager.getConnection(url);
         } else {
            String host = this.plugin.getConfig().getString("database.mysql.host");
            int port = this.plugin.getConfig().getInt("database.mysql.port");
            String db = this.plugin.getConfig().getString("database.mysql.database");
            String user = this.plugin.getConfig().getString("database.mysql.username");
            String pass = this.plugin.getConfig().getString("database.mysql.password");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&autoReconnect=true";
            this.connection = DriverManager.getConnection(url, user, pass);
         }
      } catch (SQLException var8) {
         this.plugin.getLogger().severe("Database connection failed!");
         var8.printStackTrace();
      }

   }

   private void createTable() {
      String sql = "    CREATE TABLE IF NOT EXISTS player_stats (\n        uuid VARCHAR(36) PRIMARY KEY,\n        kills INT NOT NULL DEFAULT 0,\n        deaths INT NOT NULL DEFAULT 0\n    );\n";

      try {
         Statement stmt = this.connection.createStatement();

         try {
            stmt.execute(sql);
         } catch (Throwable var6) {
            if (stmt != null) {
               try {
                  stmt.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (stmt != null) {
            stmt.close();
         }
      } catch (SQLException var7) {
         var7.printStackTrace();
      }

   }
}
