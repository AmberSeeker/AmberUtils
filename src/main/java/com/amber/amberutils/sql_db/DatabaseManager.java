package com.amber.amberutils.sql_db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static final String DB_URL = "jdbc:mysql://sql6.freesqldatabase.com:3306/sql6701200";
    private static final String DB_USER = "sql6701200";
    private static final String DB_PASSWORD = "Zanblu9dLG";
    public static final Map<UUID, Boolean> noSpaceToggles = new HashMap<>();

    public static void loadPlayerData() {
        logger.log(Level.INFO, "Loading player data...");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT player_uuid, no_space_toggle FROM player_settings")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
                boolean noSpaceToggle = rs.getBoolean("no_space_toggle");
                noSpaceToggles.put(playerUuid, noSpaceToggle);
            }
            logger.log(Level.INFO, "Player data loaded successfully.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading player data:", e);
        }
    }

    public static void savePlayerData() {
        logger.log(Level.INFO, "Saving player data...");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            for (Map.Entry<UUID, Boolean> entry : noSpaceToggles.entrySet()) {
                UUID playerId = entry.getKey();
                boolean noSpaceToggle = entry.getValue();
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO player_settings (player_uuid, no_space_toggle) VALUES (?, ?) ON DUPLICATE KEY UPDATE no_space_toggle = ?")) {
                    stmt.setString(1, playerId.toString());
                    stmt.setBoolean(2, noSpaceToggle);
                    stmt.setBoolean(3, noSpaceToggle);
                    stmt.executeUpdate();
                }
            }
            logger.log(Level.INFO, "Player data saved successfully.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving player data:", e);
        }
    }
}
