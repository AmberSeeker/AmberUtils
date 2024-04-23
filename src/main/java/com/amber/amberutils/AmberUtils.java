package com.amber.amberutils;

import com.amber.amberutils.commands.Commands;
import com.amber.amberutils.listeners.EventListeners;
import com.amber.amberutils.sql_db.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import net.minecraft.command.CommandBase;
import com.pixelmonmod.pixelmon.Pixelmon;
import org.spongepowered.api.command.spec.CommandSpec;


@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION, description = PluginInfo.DESCR, dependencies = {@Dependency(id = "pixelmon")})
public class AmberUtils {
    @Inject
    private Game game;

    @Inject
    private static final Logger logger = LoggerFactory.getLogger("AmberUtils");

    private static AmberUtils instance;
 
    public static AmberUtils getInstance() {
    return instance;
    }
    
    public Logger getLogger() {
    return this.logger;
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        this.logger.info("AmberUtils is starting!");
        DatabaseManager.loadPlayerData();
        CommandSpec uCommandSpec = Commands.buildSpec();
        Sponge.getCommandManager().register(this, uCommandSpec, "amberutils", "amu");
    }
    
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    instance = this;
    this.logger.info("AmberUtils is now active!");
    Pixelmon.EVENT_BUS.register(new EventListeners());
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
    this.logger.info("AmberUtils is saving Data and shutting down! Bye!");
    DatabaseManager.savePlayerData();
}
}
