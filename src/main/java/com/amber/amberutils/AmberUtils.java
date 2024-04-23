package com.amber.amberutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import net.minecraft.command.CommandBase;
import com.pixelmonmod.pixelmon.Pixelmon;


@Plugin(id = "amberutils", name = "AmberUtils", version = "0.0.1", description = "Utility stuff for pixelmon", dependencies = {@Dependency(id = "pixelmon")})
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
        logger.info("AmberUtils is starting!");
    }
    
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    instance = this;
    this.logger.info("AmberUtils is now active!");
    Pixelmon.EVENT_BUS.register(new EventListeners());
    }
}
