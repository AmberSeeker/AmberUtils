package com.amber.amberutils.sql_db;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.PluginInfo;
import com.amber.amberutils.commands.Commands;
import com.amber.amberutils.helpers.PlayerNameHelper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import org.slf4j.Logger;
import com.google.gson.Gson;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {

    private static final Logger logger = AmberUtils.logger;

    public static final Map<UUID, Boolean> noSpaceToggles = AmberUtils.noSpaceToggles;

    private static final Path jsonFile = GeneralConfig.configDir.resolve("player_data.json");
//NOTE PLESE LLOOKSOWKK SOME UUID STRING JARGON NEEDS TO BE SOLVED AT 159
    
    public static void loadPlayerData(CommandSource source) {
        logger.info("Loading player data...");
        if (GeneralConfig.STORAGE_METHOD.equalsIgnoreCase("sql")) {
            loadPlayerDataFromSQL(source);
        } else if (GeneralConfig.STORAGE_METHOD.equalsIgnoreCase("json")) {
            loadPlayerDataFromJson(source);
        } else {
            logger.error("Invalid storage method specified in GeneralConfig. No data will be loaded.");
        }
    }

    public static void loadPlayerDataFromSQL(CommandSource source) {
        logger.info("Loading player data...");
        try (Connection conn = DriverManager.getConnection(SQLConfig.DB_URL, SQLConfig.DB_USER, SQLConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT player_uuid, no_space_toggle FROM player_settings")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
                boolean noSpaceToggle = rs.getBoolean("no_space_toggle");
                noSpaceToggles.put(playerUuid, noSpaceToggle);
            }
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of("Loading player data..."));
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
    
    public static void savePlayerData(CommandSource source) {
        logger.info("Saving player data...");
        if (GeneralConfig.STORAGE_METHOD.equalsIgnoreCase("sql")) {
            savePlayerDataToSQL(source);
        } else if (GeneralConfig.STORAGE_METHOD.equalsIgnoreCase("json")) {
            savePlayerDataToJson(source);
        } else {
            logger.error("Invalid storage method specified in GeneralConfig. No data will be saved.");
        }
    }

    public static void savePlayerDataToSQL(CommandSource source) {
        logger.info("Saving player data...");
        try (Connection conn = DriverManager.getConnection(SQLConfig.DB_URL, SQLConfig.DB_USER, SQLConfig.DB_PASSWORD)) {
            for (Map.Entry<UUID, Boolean> entry : noSpaceToggles.entrySet()) {
                UUID playerId = entry.getKey();
                boolean noSpaceToggle = entry.getValue();
                String playerName = PlayerNameHelper.getPlayerName(playerId);
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO player_settings (player_uuid, player_name, no_space_toggle) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE no_space_toggle = ?")) {
                    stmt.setString(1, playerId.toString());
                    stmt.setString(2, PlayerNameHelper.getPlayerName(playerId));
                    stmt.setBoolean(3, noSpaceToggle);
                    stmt.setBoolean(4, noSpaceToggle);
                    stmt.executeUpdate();
                }
            }
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of("Saving player data..."));
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

public static void loadPlayerDataFromJson(CommandSource source) {
    logger.info("Loading player data from player_data.json...");
    Path configFile = GeneralConfig.configDir.resolve("player_data.json");
    Gson gson = new Gson();
    if (!Files.exists(configFile)) {
        logger.warn("player_data.json not found. No data loaded.");
        return;
    }
    try (BufferedReader reader = Files.newBufferedReader(configFile)) {
        String json = reader.readLine(); // Assuming data is on a single line
        if (json != null) {
            Map<UUID, Boolean> playerData = gson.fromJson(json, new HashMap<UUID, Boolean>().getClass());
            noSpaceToggles.putAll(playerData);
            if (source instanceof Player) {
                ((Player) source).sendMessage(Text.of("Loading player data from player_data.json..."));
                ((Player) source).sendMessage(Text.of(TextColors.GREEN, "Player data loaded successfully."));
            } else {
                logger.info("Player data loaded successfully from player_data.json.");
            }
        } else {
            logger.warn("player_data.json is empty. No data loaded.");
        }
    } catch (IOException e) {
        logger.error("Error loading player data from player_data.json:", e);
        // ... (error handling)
    }
}

public static void savePlayerDataToJson(CommandSource source) {
    logger.info("Saving player data to player_data.json...");
    Path configFile = GeneralConfig.configDir.resolve("player_data.json");
    Gson gson = new Gson();
  
    // Create a new object to hold unique data
    Map<UUID, Boolean> uniqueData = new HashMap<>();
    for (Map.Entry<UUID, Boolean> entry : noSpaceToggles.entrySet()) {
      UUID uuid = entry.getKey();
      boolean noSpaceToggle = entry.getValue();
      // Add key-value pair to the unique data object
      uniqueData.put(uuid, noSpaceToggle);
    }
  
    String json = gson.toJson(uniqueData); // Convert unique data to JSON string
  
    try (BufferedWriter writer = Files.newBufferedWriter(configFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
      writer.write(json);
      if (source instanceof Player) {
        ((Player) source).sendMessage(Text.of("Saving player data to player_data.json..."));
        ((Player) source).sendMessage(Text.of(TextColors.GREEN, "Player data saved successfully."));
      } else {
        logger.info("Player data saved successfully to player_data.json.");
      }
    } catch (IOException e) {
      logger.error("Error saving player data to player_data.json:", e);
    }
  }


}
