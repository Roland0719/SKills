package org.roland0719.sKills.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerStatsDAO {
    private final DatabaseManager db;
    public PlayerStatsDAO(DatabaseManager db) {
        this.db = db;
    }
    public int getKills(UUID uuid) {
        return getValue(uuid, "kills");
    }
    public int getDeaths(UUID uuid) {
        return getValue(uuid, "deaths");
    }
    private int getValue(UUID uuid, String column) {
        String sql = "SELECT " + column + " FROM player_stats WHERE uuid=?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setKills(UUID uuid, int kills) {
        setValue(uuid, "kills", kills);
    }
    public void setDeaths(UUID uuid, int deaths) {
        setValue(uuid, "deaths", deaths);
    }
    private void setValue(UUID uuid, String column, int value) {
        String insert = "INSERT OR IGNORE INTO player_stats (uuid, kills, deaths) VALUES (?,0,0)";
        String update = "UPDATE player_stats SET " + column + " = ? WHERE uuid = ?";

        try (PreparedStatement ps1 = db.getConnection().prepareStatement(insert);
             PreparedStatement ps2 = db.getConnection().prepareStatement(update)) {

            ps1.setString(1, uuid.toString());
            ps1.executeUpdate();

            ps2.setInt(1, value);
            ps2.setString(2, uuid.toString());
            ps2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Map<UUID, int[]> getAllStats() {
        Map<UUID, int[]> stats = new HashMap<>();
        String sql = "SELECT uuid, kills, deaths FROM player_stats";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                int kills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");
                stats.put(uuid, new int[]{kills, deaths});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    public List<UUID> getTopKD(int limit) {
        Map<UUID, int[]> stats = getAllStats();

        return stats.entrySet().stream()
                .sorted((a, b) -> {
                    double kdA = calculateKD(a.getValue()[0], a.getValue()[1]);
                    double kdB = calculateKD(b.getValue()[0], b.getValue()[1]);
                    return Double.compare(kdB, kdA);
                })
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }
    public double getKD(UUID uuid) {
        int kills = getKills(uuid);
        int deaths = getDeaths(uuid);
        return calculateKD(kills, deaths);
    }
    private double calculateKD(int kills, int deaths) {
        return deaths == 0 ? kills : (double) kills / deaths;
    }
    public List<UUID> getTopKills(int limit) { return getTop("kills", limit); }
    public List<UUID> getTopDeaths(int limit) { return getTop("deaths", limit); }
    public void addKill(UUID uuid) { update(uuid, "kills"); }
    public void addDeath(UUID uuid) {
        update(uuid, "deaths");
    }
    public void resetStats(UUID uuid) {
        String sql = "UPDATE player_stats SET kills = 0, deaths = 0 WHERE uuid = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void resetAllStats() {
        String sql = "UPDATE player_stats SET kills = 0, deaths = 0";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void update(UUID uuid, String column) {
        String insert = "INSERT OR IGNORE INTO player_stats (uuid, kills, deaths) VALUES (?,0,0)";
        String update = "UPDATE player_stats SET " + column + " = " + column + " + 1 WHERE uuid = ?";
        try (PreparedStatement ps1 = db.getConnection().prepareStatement(insert);
             PreparedStatement ps2 = db.getConnection().prepareStatement(update)) {
            ps1.setString(1, uuid.toString());
            ps1.executeUpdate();
            ps2.setString(1, uuid.toString());
            ps2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private List<UUID> getTop(String column, int limit) {
        List<UUID> result = new ArrayList<>();
        String sql = "SELECT uuid FROM player_stats ORDER BY " + column + " DESC LIMIT ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(UUID.fromString(rs.getString("uuid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
