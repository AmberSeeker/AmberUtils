package com.amber.amberutils.sql_db;

import com.amber.amberutils.AmberUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class JsonConfig { // Class name changed

  private static final Logger logger = AmberUtils.logger;
  private static final Path configDir = Sponge.getGame().getGameDirectory().resolve("config/AmberUtils");
  private static final Path jsonFile = configDir.resolve("player_data.json"); // Use .json extension

  public static void readJsonConfig() { // Method name changed
    logger.info("Checking player_data.json status...");
    try {
      if (!Files.exists(configDir)) {
        Files.createDirectories(configDir);
        logger.info("Created AmberUtils directory.");
      }

      if (!Files.exists(jsonFile)) {
        Files.createFile(jsonFile);
        logger.info("Created player_data.json file.");
      }
    } catch (IOException e) {
      logger.error("An error occurred while creating player_data.json.", e);
    }
  }
}
