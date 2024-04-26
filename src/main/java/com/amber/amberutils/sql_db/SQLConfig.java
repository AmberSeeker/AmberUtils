package com.amber.amberutils.sql_db;

import com.amber.amberutils.AmberUtils;
import org.slf4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConfig {

    private static final Logger logger = AmberUtils.logger;

    public static String DB_URL;
    public static String DB_USER;
    public static String DB_PASSWORD;

    public static void readSQLConfig(String dbUrl, String dbUser, String dbPassword) {
        DB_URL = dbUrl;
        DB_USER = dbUser;
        DB_PASSWORD = dbPassword;
        logger.info("Connecting to SQL Database...");
        createPlayerSettingsTable();
    }

    private static void createPlayerSettingsTable() {
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            logger.error("Incomplete SQL configuration. Please fill in database details.");
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS player_settings ("
                + "player_uuid VARCHAR(255) PRIMARY KEY,"
                + "player_name VARCHAR(255) NOT NULL,"
                + "no_space_toggle BOOLEAN NOT NULL)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Hooked into player_settings table.");
        } catch (SQLException e) {
            logger.error("Error creating player_settings table", e);
        }
    }
}
