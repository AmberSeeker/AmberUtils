package com.amber.amberutils.sql_db;

import com.amber.amberutils.AmberUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class GeneralConfig {

    private static final Logger logger = AmberUtils.logger;
    public static final Path configDir = Sponge.getGame().getGameDirectory().resolve("config/AmberUtils");
    private static final Path generalConfigFile = configDir.resolve("GeneralConfig.properties");

    public static String STORAGE_METHOD = "";

    public static void readGeneralConfig() {
        logger.info("Checking GeneralConfig status...");
        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                logger.info("Created AmberUtils directory.");
            }

            if (!Files.exists(generalConfigFile)) {
                Files.createFile(generalConfigFile);
                logger.info("Created GeneralConfig.properties file.");
                logger.warn("Don't forget to set your storage.method in the config file and reload the plugin.");
                try (BufferedWriter writer = Files.newBufferedWriter(generalConfigFile, StandardOpenOption.WRITE)) {
                    writer.write("# General Configuration\n\n");
                    writer.newLine();
                    writer.write("# Set this to json to save data locally.\n");
                    writer.write("#Else set it to sql and enter your database details in SQLConfig.properties.\n");
                    writer.newLine();
                    writer.write("storage.method=json-or-sql");
                }
            }

            // Load properties from the config file
            Properties prop = new Properties();
            try (InputStream input = new FileInputStream(generalConfigFile.toFile())) {
                prop.load(input);
                STORAGE_METHOD = prop.getProperty("storage.method");
                logger.info("General configuration loaded successfully.");
                initializeConfig();
            }
        } catch (IOException e) {
            logger.error("An error occurred while reading or creating General configuration file.");
            e.printStackTrace();
        }
    }
    public static void initializeConfig() {
        switch (STORAGE_METHOD) {
            case "sql":
                logger.info("Storage type set to SQL Database.");
                SQLConfig.readSQLConfig();
                break;
            case "json":
                logger.info("Storage type set to Json.");
                JsonConfig.readJsonConfig();
                break;
            default:
                // Handle invalid storage method
                logger.error("Invalid storage method specified in GeneralConfig. Data won't be saved until fixed and reloaded.");
            }
        }
}
