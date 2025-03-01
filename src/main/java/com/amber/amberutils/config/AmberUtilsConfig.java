package com.amber.amberutils.config;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.handlers.SQLConfig;
import com.amber.amberutils.handlers.SQLiteConfig;

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
    public static int[] natures = new int[10];
    public static int base_price = 10;
    public static int shiny_price = 100;
    public static int ha_price = 50;
    public static int hundo_price = 20;
    public static int perf_price = 10;
    public static int nature_price = 10;
    public static boolean sell_ha = true;
    public static boolean sell_shiny = true;
    public static int boxclear_price = 100;

    // Experimental
    public static boolean discordbc = false;
    public static boolean pokenear = false;
    public static boolean sellgui = false;
    public static boolean eggsell = false;
    public static boolean evofix = false;
    public static boolean chatcolor = false;
    public static boolean boxclear = false;

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
                logger.warn("Don't forget to set your storage.method and discord details(for DiscordUtils) in the config file and reload the plugin.");
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
                    writer.newLine();
                    writer.write("# Enable Experimental Modules (Things will break)\n");
                    writer.write("ex.discordbc=false\n");
                    writer.write("ex.pokenear=false\n");
                    writer.write("ex.sellgui=false\n");
                    writer.write("ex.eggsell=false\n");
                    writer.write("ex.evofix=false\n");
                    writer.write("ex.chatcolor=false\n");
                    writer.write("ex.boxclear=false\n");
                    writer.newLine();
                    writer.write("# Discord Details for DiscordUtils\n");
                    writer.write("discord.token=YOUR_DISCORD_BOT_TOKEN_HERE\n");
                    writer.write("discord.channelid=CHANNEL_ID_OF_THE_BROADCAST_CHANNEL\n");
                    writer.write("server.name=YOUR_SERVER_NAME\n");
                    writer.newLine();
                    writer.write("# EggSell Stuff: Below values will be used for the /eggsell command.\n");
                    writer.write("eggsell.base_price=10\n");
                    writer.write("eggsell.shiny_price=100\n");
                    writer.write("eggsell.ha_price=50\n");
                    writer.write("eggsell.hundo_price=20\n");
                    writer.write("eggsell.perf_price=10\n");
                    writer.write("eggsell.nature_price=10\n");
                    writer.write("eggsell.sell_ha=true\n");
                    writer.write("eggsell.sell_shiny=true\n");
                    writer.newLine();
                    writer.write("# BoxClear Stuff\n");
                    writer.write("boxclear.price=100\n");
                }
            }
            
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
                
                try {
                    base_price = Integer.parseInt(prop.getProperty("eggsell.base_price", "10"));
                    shiny_price = Integer.parseInt(prop.getProperty("eggsell.shiny_price", "100"));
                    ha_price = Integer.parseInt(prop.getProperty("eggsell.ha_price", "50"));
                    hundo_price = Integer.parseInt(prop.getProperty("eggsell.hundo_price", "20"));
                    perf_price = Integer.parseInt(prop.getProperty("eggsell.perf_price", "10"));
                    nature_price = Integer.parseInt(prop.getProperty("eggsell.nature_price", "10"));
                    boxclear_price = Integer.parseInt(prop.getProperty("boxclear.price", "100"));
                }
                catch (NumberFormatException e) {
                    logger.error("Invalid number format in configuration file. Please check your config file.");
                    e.printStackTrace();
                }

                sell_ha = Boolean.parseBoolean(prop.getProperty("eggsell.sell_ha", "true"));
                sell_shiny = Boolean.parseBoolean(prop.getProperty("eggsell.sell_shiny", "true"));
                discordbc = Boolean.parseBoolean(prop.getProperty("ex.discordbc", "false")); 
                pokenear = Boolean.parseBoolean(prop.getProperty("ex.pokenear", "false"));
                sellgui = Boolean.parseBoolean(prop.getProperty("ex.sellgui", "false"));
                eggsell = Boolean.parseBoolean(prop.getProperty("ex.eggsell", "false"));
                evofix = Boolean.parseBoolean(prop.getProperty("ex.evofix", "false"));
                chatcolor = Boolean.parseBoolean(prop.getProperty("ex.chatcolor", "false"));
                boxclear = Boolean.parseBoolean(prop.getProperty("ex.boxclear", "false"));


                logger.info("General configuration loaded successfully.");
                BannedItemsList.readBanList();
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
