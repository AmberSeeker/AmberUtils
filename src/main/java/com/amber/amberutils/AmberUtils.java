package com.amber.amberutils;

import com.amber.amberutils.commands.Commands;
import com.amber.amberutils.listeners.EventListeners;
import com.amber.amberutils.sql_db.DatabaseManager;
import com.amber.amberutils.sql_db.SQLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import com.pixelmonmod.pixelmon.Pixelmon;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION, description = PluginInfo.DESCR, dependencies = {@Dependency(id = "pixelmon")})
public class AmberUtils {
    @Inject
    private Game game;

    @Inject
    public static final Logger logger = LoggerFactory.getLogger("AmberUtils");

    public static final Map<UUID, Boolean> noSpaceToggles = new HashMap<>();

    private static AmberUtils instance;
 
    public static AmberUtils getInstance() {
    return instance;
    }
    
    public Logger getLogger() {
    return this.logger;
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        this.logger.info("AmberUtils is starting...");
        try {
            SQLConfig.readSQLConfig();
            DatabaseManager.loadPlayerData(Sponge.getServer().getConsole());
            CommandSpec uCommandSpec = Commands.buildSpec();
            Sponge.getCommandManager().register(this, uCommandSpec, "amberutils", "amu");
        } catch (Exception e) {
            logger.error("An error occurred during initialization:", e);
        }
    }
    
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        this.logger.info("AmberUtils is now active!");
        Pixelmon.EVENT_BUS.register(new EventListeners());
    }

    @Listener
    public void reload(GameReloadEvent event) {
        CommandSource source = event.getCause().first(CommandSource.class).orElse(null);
        if (source instanceof Player) {
            logger.info("Reloading AmberUtils...");
            source.sendMessage(Text.of(TextColors.GREEN, "Reloading AmberUtils..."));
        } else {
            logger.info("Reloading AmberUtils...");
        }
        try {
            SQLConfig.readSQLConfig();
            DatabaseManager.savePlayerData(source);
            DatabaseManager.loadPlayerData(source);
        } catch (Exception e) {
            logger.error("An error occurred during reload:", e);
        }
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        this.logger.info("AmberUtils is saving Data and Shutting Down! Bye!");
        try {
            DatabaseManager.savePlayerData(Sponge.getServer().getConsole());
        } catch (Exception e) {
            logger.error("An error occurred during server stop:", e);
        }
    }
}
