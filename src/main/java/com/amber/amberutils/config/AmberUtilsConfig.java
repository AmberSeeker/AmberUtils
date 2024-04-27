package com.amber.amberutils.config;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.sql_db.SQLConfig;
import com.amber.amberutils.sql_db.SQLiteConfig;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class AmberUtilsConfig {

    private static final Logger logger = AmberUtils.logger;
    public static final Path configDir = Sponge.getGame().getGameDirectory().resolve("config/AmberUtils");
    private static final Path generalConfigFile = configDir.resolve("AmberUtilsConfig.properties");

    public static String STORAGE_METHOD = "";
    public static String DB_URL = "";
    public static String DB_USER = "";
    public static String DB_PASSWORD = "";

    public static void readGeneralConfig() {
        logger.info("Checking AmberUtilsConfig status...");
        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                logger.info("Created AmberUtils directory.");
            }

            if (!Files.exists(generalConfigFile)) {
                Files.createFile(generalConfigFile);
                logger.info("Created AmberUtilsConfig.properties file.");
                logger.warn("Don't forget to set your storage.method in the config file and reload the plugin.");
                try (BufferedWriter writer = Files.newBufferedWriter(generalConfigFile, StandardOpenOption.WRITE)) {
                    writer.write("# General Configuration\n");
                    writer.newLine();
                    writer.write("# Set this to SQLite to save data locally.\n");
                    writer.write("# Else set it to SQL and enter your database details below.\n");
                    writer.write("storage.method=sqlite-or-sql\n");
                    writer.newLine();
                    writer.write("# SQL Configuration (Fill in if using SQL storage method)\n");
                    writer.write("db.url=mysql://your.database.url\n");
                    writer.write("db.name=your_database_name\n");
                    writer.write("db.port=database_port_usually_3306\n");
                    writer.write("db.user=your_database_user\n");
                    writer.write("db.password=your_database_password\n");
                }
            }
            BannedItemsList.readBanList();
            // Load properties from the config file
            Properties prop = new Properties();
            try (InputStream input = new FileInputStream(generalConfigFile.toFile())) {
                prop.load(input);
                STORAGE_METHOD = prop.getProperty("storage.method");
                DB_URL = "jdbc:" + prop.getProperty("db.url", "");
                DB_URL = DB_URL + ":" + prop.getProperty("db.port", "");
                DB_URL = DB_URL + "/" + prop.getProperty("db.name", "");
                DB_USER = prop.getProperty("db.user", "");
                DB_PASSWORD = prop.getProperty("db.password", "");
                logger.info("General configuration loaded successfully.");
                initializeConfig();
            }
        } catch (IOException e) {
            logger.error("An error occurred while reading or creating AmberUtils configuration file.");
            e.printStackTrace();
        }
    }

    public static void initializeConfig() {
        switch (STORAGE_METHOD) {
            case "sql":
                logger.info("Storage type set to SQL Database.");
                if (DB_URL.isEmpty() || DB_USER.isEmpty() || DB_PASSWORD.isEmpty()) {
                    logger.error("Incomplete SQL configuration. Please fill in database details.");
                    break;
                }
                SQLConfig.readSQLConfig(DB_URL, DB_USER, DB_PASSWORD);
                break;
            case "sqlite":
                logger.info("Storage type set to local SQLite Database.");
                SQLiteConfig.initializeDatabase();
                break;
            default:
                logger.error("Invalid storage method specified in AmberUtilsConfig. Data won't be saved until fixed and reloaded.");
        }
    }
}
