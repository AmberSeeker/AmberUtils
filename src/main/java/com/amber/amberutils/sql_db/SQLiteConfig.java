package com.amber.amberutils.sql_db;

import com.amber.amberutils.AmberUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConfig {

    private static final Logger logger = AmberUtils.logger;
    private static final Path dbDir = Sponge.getGame().getGameDirectory().resolve("config/AmberUtils/data");
    private static final Path dbFile = dbDir.resolve("player_data.db");
    private static final String DB_URL = "jdbc:sqlite:" + dbFile;

    public static void initializeDatabase() {

        File directory = dbDir.toFile();
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                logger.info("Created directory: {}", dbDir);
            } else {
                logger.error("Failed to create directory: {}", dbDir);
            }
        }

        // Check if the database file exists, if not, create it
        File databaseFile = dbFile.toFile();
        if (!databaseFile.exists()) {
            if (createDatabase()) {
                logger.info("Created SQLite database: {}", dbFile);
                createPlayerSettingsTable();
            } else {
                logger.error("Failed to create SQLite database: {}", dbFile);
            }
        }

        // Ensure JDBC driver is loaded
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found", e);
        }
    }

    // Create table if it doesn't exist
    private static void createPlayerSettingsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_settings ("
                   + "player_uuid TEXT PRIMARY KEY,"
                   + "player_name TEXT NOT NULL,"
                   + "no_space_toggle BOOLEAN NOT NULL)";
    
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("player_settings table created successfully.");
        } catch (SQLException e) {
            logger.error("Error creating player_settings table", e);
        }
    }
    

    private static boolean createDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            
            // This will create an empty SQLite database file
            return true;
        } catch (SQLException e) {
            logger.error("Error creating SQLite database", e);
            return false;
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
