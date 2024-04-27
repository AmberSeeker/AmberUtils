package com.amber.amberutils.config;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.PluginInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

public class BannedItemsList {
    
    public static final Logger logger = AmberUtils.logger;
    public static List<String> banlist = new ArrayList<>();
    public static void readBanList() {

    try {
        
        Path configDir = Sponge.getGame().getGameDirectory().resolve("config/AmberUtils");
        Path bannedItemsFile = configDir.resolve("banned_items.txt");

        if (!Files.exists(configDir)) {
            Files.createDirectories(configDir);
            logger.info("Created AmberUtils directory.");
        }

        if (!Files.exists(bannedItemsFile)) {
            Files.createFile(bannedItemsFile);
            logger.info("Created banned_items file.");
            try (BufferedWriter writer = Files.newBufferedWriter(bannedItemsFile, StandardOpenOption.WRITE)) {
              writer.write("#AmberUtils" + "-" + PluginInfo.MC_VERSION + "-" + PluginInfo.VERSION+" Banned Items\n");
              writer.write("#Put the ID's of items to be banned here, without the '#'. For Example:\n");
              writer.write("#minecraft:piston");
          }
        }

        logger.info("Checking for banned items...");
        banlist.clear();
        try (Scanner myReader = new Scanner(bannedItemsFile.toFile())) {
        while (myReader.hasNextLine())
        {
          String line = myReader.nextLine().trim();
          if (!line.startsWith("#") && !line.isEmpty()){
          banlist.add(line);}
        }
      }

        logger.info("§aFound " + banlist.size() + " banned items.");
        List<String> lines = Files.readAllLines(bannedItemsFile);
        lines.removeIf(line -> line.trim().isEmpty());
        String content = lines.stream()
                     .map(String::trim) // Trim leading and trailing whitespace from each line
                     .collect(Collectors.joining(System.lineSeparator())); // Join the lines
        Files.write(bannedItemsFile, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
        logger.info("An error occurred while reading or creating files.");
        e.printStackTrace();
    }
  }
    
}
