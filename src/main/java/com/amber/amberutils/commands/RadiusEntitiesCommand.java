package com.amber.amberutils.commands;

import com.amber.amberutils.config.AmberUtilsConfig;
import com.amber.amberutils.helpers.GeneralHelpers;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.minecraft.entity.player.EntityPlayerMP;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

import java.util.Collection;

public class RadiusEntitiesCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        
        // Disables the module
        if (!AmberUtilsConfig.pokenear) {
            src.sendMessage(Text.of(TextColors.RED, "This module is disabled!"));
            return CommandResult.success();
        }

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Only players can use this command."));
            return CommandResult.success();
        }

        Player player = (Player) src;
        Location<World> playerLocation = player.getLocation();
        double radius = 100.0;

        Collection<Entity> nearbyEntities = player.getWorld().getEntities(entity -> 
            entity.getLocation().getPosition().distance(playerLocation.getPosition()) <= radius
        );

        player.sendMessage(Text.of(TextColors.GREEN, "Entities within a 100-block radius:"));
        for (Entity entity : nearbyEntities) {
            if (entity instanceof EntityPixelmon){
                EntityPixelmon pokemon = (EntityPixelmon) entity;
                if (pokemon.hasOwner()) continue;
                player.sendMessage(Text.of(TextColors.YELLOW, pokemon.getSpecies().name, pokemon.isBossPokemon() ? " Boss" : "", pokemon.isLegendary() ? " Legend" : "", pokemon.getPokemonData().isShiny() ? " Shiny": "", pokemon.getBossMode().isMega()));
                EntityPlayerMP mcplayer = (EntityPlayerMP)player;
                mcplayer.dropItem(GeneralHelpers.getPhoto(pokemon), false,false);
            }
        }

        return CommandResult.success();
    }

    public static CommandSpec buildSpec() {
        return CommandSpec.builder()
            .description(Text.of("Lists all entities within a 100-block radius"))
            .permission("amberutils.command.radiusentities")
            .executor(new RadiusEntitiesCommand())
            .build();
    }
}
