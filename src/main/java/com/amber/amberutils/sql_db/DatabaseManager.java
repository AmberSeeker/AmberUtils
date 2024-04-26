package com.amber.amberutils.sql_db;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.helpers.NameHelper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.entity.living.player.Player;

public class DatabaseManager {

    private static final Logger logger = AmberUtils.logger;

    public static final Map<UUID, Boolean> noSpaceToggles = AmberUtils.noSpaceToggles;
    
    public static void loadPlayerData(CommandSource source) {
        //logger.info("Loading player data...");
        if (AmberUtilsConfig.STORAGE_METHOD.equalsIgnoreCase("sql")) {
            loadPlayerDataFromSQL(source);
        } else if (AmberUtilsConfig.STORAGE_METHOD.equalsIgnoreCase("json")) {
            //loadPlayerDataFromJson(source);
        } else if (AmberUtilsConfig.STORAGE_METHOD.equalsIgnoreCase("sqlite")) {
            loadPlayerDataFromSQLite(source);
        }
        else {
            logger.error("Invalid storage method specified in AmberUtilsConfig. No data will be loaded.");
        }
    }

    public static void loadPlayerDataFromSQL(CommandSource source) {
        logger.info("Loading player data from SQL database...");
        try (Connection conn = DriverManager.getConnection(SQLConfig.DB_URL, SQLConfig.DB_USER, SQLConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT player_uuid, no_space_toggle FROM player_settings")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
                boolean noSpaceToggle = rs.getBoolean("no_space_toggle");
                noSpaceToggles.put(playerUuid, noSpaceToggle);
            }
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of("Loading player data from SQL database..."));
                ((Player) source).sendMessage(Text.of(TextColors.GREEN, "Player data loaded successfully."));
                logger.info("§aPlayer data loaded successfully.");
            } else {
                logger.info("§aPlayer data loaded successfully.");
            }
        } catch (SQLException e) {
            logger.error("Error loading player data:", e);
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of(TextColors.RED, "An error occurred while loading player data. Please check the console for details."));
            } else {
                logger.error("An error occurred while loading player data. Please check the console for details.");
            }
        }
    }

    public static void loadPlayerDataFromSQLite(CommandSource source) {
        logger.info("Loading player data from SQLite database...");
        try (Connection conn = SQLiteConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT player_uuid, no_space_toggle FROM player_settings")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
                boolean noSpaceToggle = rs.getBoolean("no_space_toggle");
                noSpaceToggles.put(playerUuid, noSpaceToggle);
            }
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of("Loading player data from SQLite database..."));
                ((Player) source).sendMessage(Text.of(TextColors.GREEN, "Player data loaded successfully."));
                logger.info("§aPlayer data loaded successfully.");
            } else {
                logger.info("§aPlayer data loaded successfully.");
            }
        } catch (SQLException e) {
            logger.error("Error loading player data from SQLite database:", e);
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of(TextColors.RED, "An error occurred while loading player data from SQLite database. Please check the console for details."));
            } else {
                logger.error("An error occurred while loading player data from SQLite database. Please check the console for details.");
            }
        }
    }
    
    
    public static void savePlayerData(CommandSource source) {
        //logger.info("Saving player data...");
        if (AmberUtilsConfig.STORAGE_METHOD.equalsIgnoreCase("sql")) {
            savePlayerDataToSQL(source);
        } else if (AmberUtilsConfig.STORAGE_METHOD.equalsIgnoreCase("json")) {
            //savePlayerDataToJson(source);
        } else if (AmberUtilsConfig.STORAGE_METHOD.equalsIgnoreCase("sqlite")) {
            savePlayerDataToSQLite(source);
        } else {
            logger.error("Invalid storage method specified in AmberUtilsConfig. No data will be saved.");
        }
    }

    public static void savePlayerDataToSQL(CommandSource source) {
        logger.info("Saving player data to SQL database...");
        try (Connection conn = DriverManager.getConnection(SQLConfig.DB_URL, SQLConfig.DB_USER, SQLConfig.DB_PASSWORD)) {
            for (Map.Entry<UUID, Boolean> entry : noSpaceToggles.entrySet()) {
                UUID playerId = entry.getKey();
                boolean noSpaceToggle = entry.getValue();
                String playerName = NameHelper.getPlayerName(playerId);
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO player_settings (player_uuid, player_name, no_space_toggle) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE no_space_toggle = ?")) {
                    stmt.setString(1, playerId.toString());
                    stmt.setString(2, playerName);
                    stmt.setBoolean(3, noSpaceToggle);
                    stmt.setBoolean(4, noSpaceToggle);
                    stmt.executeUpdate();
                }
            }
            noSpaceToggles.clear();
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of("Saving player data to SQL database..."));
                ((Player) source).sendMessage(Text.of(TextColors.GREEN, "Player data saved successfully."));
                logger.info("§aPlayer data saved successfully.");
            } else {
                logger.info("§aPlayer data saved successfully.");
            }
        } catch (SQLException e) {
            logger.error("Error saving player data:", e);
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of(TextColors.RED, "An error occurred while saving player data. Please check the console for details."));
            } else {
                logger.error("An error occurred while saving player data. Please check the console for details.");
            }
        }
    }

    public static void savePlayerDataToSQLite(CommandSource source) {
        logger.info("Saving player data to SQLite database...");
        try (Connection conn = SQLiteConfig.getConnection()) {
            for (Map.Entry<UUID, Boolean> entry : noSpaceToggles.entrySet()) {
                UUID playerId = entry.getKey();
                boolean noSpaceToggle = entry.getValue();
                String playerName = NameHelper.getPlayerName(playerId);
                try (PreparedStatement stmt = conn.prepareStatement("INSERT OR REPLACE INTO player_settings (player_uuid, player_name, no_space_toggle) VALUES (?, ?, ?)")) {
                    stmt.setString(1, playerId.toString());
                    stmt.setString(2, playerName);
                    stmt.setBoolean(3, noSpaceToggle);
                    //stmt.setBoolean(4, noSpaceToggle);
                    stmt.executeUpdate();
                }
            }
            noSpaceToggles.clear();
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of("Saving player data to SQLite database..."));
                ((Player) source).sendMessage(Text.of(TextColors.GREEN, "Player data saved successfully."));
                logger.info("§aPlayer data saved successfully.");
            } else {
                logger.info("§aPlayer data saved successfully.");
            }
        } catch (SQLException e) {
            logger.error("Error saving player data:", e);
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of(TextColors.RED, "An error occurred while saving player data. Please check the console for details."));
            } else {
                logger.error("An error occurred while saving player data. Please check the console for details.");
            }
        }
    }
}
