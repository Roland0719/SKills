package org.roland0719.sKills.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public class PlayerStatsDAO {
   private final DatabaseManager db;

   public PlayerStatsDAO(DatabaseManager db) {
      this.db = db;
   }

   public int getKills(UUID uuid) {
      return this.getValue(uuid, "kills");
   }

   public int getDeaths(UUID uuid) {
      return this.getValue(uuid, "deaths");
   }

   private int getValue(UUID uuid, String column) {
      String sql = "SELECT " + column + " FROM player_stats WHERE uuid=?";

      try {
         PreparedStatement ps = this.db.getConnection().prepareStatement(sql);

         int var6;
         label54: {
            try {
               ps.setString(1, uuid.toString());
               ResultSet rs = ps.executeQuery();
               if (rs.next()) {
                  var6 = rs.getInt(column);
                  break label54;
               }
            } catch (Throwable var8) {
               if (ps != null) {
                  try {
                     ps.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (ps != null) {
               ps.close();
            }

            return 0;
         }

         if (ps != null) {
            ps.close();
         }

         return var6;
      } catch (SQLException var9) {
         var9.printStackTrace();
         return 0;
      }
   }

   public void setKills(UUID uuid, int kills) {
      this.setValue(uuid, "kills", kills);
   }

   public void setDeaths(UUID uuid, int deaths) {
      this.setValue(uuid, "deaths", deaths);
   }

   private void setValue(UUID uuid, String column, int value) {
      String insert = "INSERT OR IGNORE INTO player_stats (uuid, kills, deaths) VALUES (?,0,0)";
      String update = "UPDATE player_stats SET " + column + " = ? WHERE uuid = ?";

      try {
         PreparedStatement ps1 = this.db.getConnection().prepareStatement(insert);

         try {
            PreparedStatement ps2 = this.db.getConnection().prepareStatement(update);

            try {
               ps1.setString(1, uuid.toString());
               ps1.executeUpdate();
               ps2.setInt(1, value);
               ps2.setString(2, uuid.toString());
               ps2.executeUpdate();
            } catch (Throwable var12) {
               if (ps2 != null) {
                  try {
                     ps2.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (ps2 != null) {
               ps2.close();
            }
         } catch (Throwable var13) {
            if (ps1 != null) {
               try {
                  ps1.close();
               } catch (Throwable var10) {
                  var13.addSuppressed(var10);
               }
            }

            throw var13;
         }

         if (ps1 != null) {
            ps1.close();
         }
      } catch (SQLException var14) {
         var14.printStackTrace();
      }

   }

   public Map<UUID, int[]> getAllStats() {
      Map<UUID, int[]> stats = new HashMap();
      String sql = "SELECT uuid, kills, deaths FROM player_stats";

      try {
         PreparedStatement ps = this.db.getConnection().prepareStatement(sql);

         try {
            ResultSet rs = ps.executeQuery();

            try {
               while(rs.next()) {
                  UUID uuid = UUID.fromString(rs.getString("uuid"));
                  int kills = rs.getInt("kills");
                  int deaths = rs.getInt("deaths");
                  stats.put(uuid, new int[]{kills, deaths});
               }
            } catch (Throwable var10) {
               if (rs != null) {
                  try {
                     rs.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (rs != null) {
               rs.close();
            }
         } catch (Throwable var11) {
            if (ps != null) {
               try {
                  ps.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (ps != null) {
            ps.close();
         }
      } catch (SQLException var12) {
         var12.printStackTrace();
      }

      return stats;
   }

   public List<UUID> getTopKD(int limit) {
      Map<UUID, int[]> stats = this.getAllStats();
      return stats.entrySet().stream().sorted((a, b) -> {
         double kdA = this.calculateKD(((int[])a.getValue())[0], ((int[])a.getValue())[1]);
         double kdB = this.calculateKD(((int[])b.getValue())[0], ((int[])b.getValue())[1]);
         return Double.compare(kdB, kdA);
      }).limit((long)limit).map(Entry::getKey).toList();
   }

   public double getKD(UUID uuid) {
      int kills = this.getKills(uuid);
      int deaths = this.getDeaths(uuid);
      return this.calculateKD(kills, deaths);
   }

   private double calculateKD(int kills, int deaths) {
      return deaths == 0 ? (double)kills : (double)kills / (double)deaths;
   }

   public List<UUID> getTopKills(int limit) {
      return this.getTop("kills", limit);
   }

   public List<UUID> getTopDeaths(int limit) {
      return this.getTop("deaths", limit);
   }

   public void addKill(UUID uuid) {
      this.update(uuid, "kills");
   }

   public void addDeath(UUID uuid) {
      this.update(uuid, "deaths");
   }

   public void resetStats(UUID uuid) {
      String sql = "UPDATE player_stats SET kills = 0, deaths = 0 WHERE uuid = ?";

      try {
         PreparedStatement ps = this.db.getConnection().prepareStatement(sql);

         try {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
         } catch (Throwable var7) {
            if (ps != null) {
               try {
                  ps.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (ps != null) {
            ps.close();
         }
      } catch (SQLException var8) {
         var8.printStackTrace();
      }

   }

   public void resetAllStats() {
      String sql = "UPDATE player_stats SET kills = 0, deaths = 0";

      try {
         PreparedStatement ps = this.db.getConnection().prepareStatement(sql);

         try {
            ps.executeUpdate();
         } catch (Throwable var6) {
            if (ps != null) {
               try {
                  ps.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (ps != null) {
            ps.close();
         }
      } catch (SQLException var7) {
         var7.printStackTrace();
      }

   }

   private void update(UUID uuid, String column) {
      String insert = "INSERT OR IGNORE INTO player_stats (uuid, kills, deaths) VALUES (?,0,0)";
      String update = "UPDATE player_stats SET " + column + " = " + column + " + 1 WHERE uuid = ?";

      try {
         PreparedStatement ps1 = this.db.getConnection().prepareStatement(insert);

         try {
            PreparedStatement ps2 = this.db.getConnection().prepareStatement(update);

            try {
               ps1.setString(1, uuid.toString());
               ps1.executeUpdate();
               ps2.setString(1, uuid.toString());
               ps2.executeUpdate();
            } catch (Throwable var11) {
               if (ps2 != null) {
                  try {
                     ps2.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (ps2 != null) {
               ps2.close();
            }
         } catch (Throwable var12) {
            if (ps1 != null) {
               try {
                  ps1.close();
               } catch (Throwable var9) {
                  var12.addSuppressed(var9);
               }
            }

            throw var12;
         }

         if (ps1 != null) {
            ps1.close();
         }
      } catch (SQLException var13) {
         var13.printStackTrace();
      }

   }

   private List<UUID> getTop(String column, int limit) {
      List<UUID> result = new ArrayList();
      String sql = "SELECT uuid FROM player_stats ORDER BY " + column + " DESC LIMIT ?";

      try {
         PreparedStatement ps = this.db.getConnection().prepareStatement(sql);

         try {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
               result.add(UUID.fromString(rs.getString("uuid")));
            }
         } catch (Throwable var9) {
            if (ps != null) {
               try {
                  ps.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (ps != null) {
            ps.close();
         }
      } catch (SQLException var10) {
         var10.printStackTrace();
      }

      return result;
   }
}
