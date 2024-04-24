package com.amber.amberutils.sql_db;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.PluginInfo;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class SQLConfig {

    private static final Logger logger = AmberUtils.logger;
    private static final Path configDir = Sponge.getGame().getGameDirectory().resolve("config/AmberUtils");
    private static final Path sqlConfigFile = configDir.resolve("SQLconfig.properties");
    public static String DB_URL;
    public static String DB_USER;
    public static String DB_PASSWORD;

    public static void readSQLConfig() {
        logger.info("Checking SQLConfig status...");
        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                logger.info("Created AmberUtils directory.");
            }

            if (!Files.exists(sqlConfigFile)) {
                Files.createFile(sqlConfigFile);
                logger.info("Created SQLconfig.properties file.");
                logger.warn("Don't forget to put your SQL DB details in the config file and reload the plugin.");
                try (BufferedWriter writer = Files.newBufferedWriter(sqlConfigFile, StandardOpenOption.WRITE)) {
                    writer.write("# SQL Configuration");
                    writer.newLine();
                    writer.write("db.url=mysql://your.database.url:3306/your_database_name");
                    writer.newLine();
                    writer.write("db.user=your_database_user");
                    writer.newLine();
                    writer.write("db.password=your_database_password");
                }
            }

            Properties prop = new Properties();
            try (InputStream input = new FileInputStream(sqlConfigFile.toFile())) {
                prop.load(input);
                DB_URL = "jdbc:"+prop.getProperty("db.url");
                DB_USER = prop.getProperty("db.user");
                DB_PASSWORD = prop.getProperty("db.password");
                logger.info("Config file check complete.");
            } catch (IOException ex) {
                ex.printStackTrace();
                logger.error("Config file not setup. Plugin won't be able to save changes!");
            }
        } catch (IOException e) {
            logger.info("An error occurred while reading or creating SQL configuration file.");
            e.printStackTrace();
        }
    }
}
