package com.amber.amberutils.commands;

import java.util.List;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.Evolution;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.EvoCondition;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.types.LevelingEvolution;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.types.TickingEvolution;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.Pixelmon;

import net.minecraft.entity.player.EntityPlayerMP;

public class EvoFixCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Only players can use this command."));
            return CommandResult.success();
        }

        Player player = (Player) src;

        if (!args.hasAny("slot")) {
            player.sendMessage(Text.of(TextColors.RED, "You must specify a slot number between 1 and 6."));
            player.sendMessage(Text.of(TextColors.YELLOW, "Usage: /evofix <slot>"));
            return CommandResult.success();
        }

        int slot = args.<Integer>getOne("slot").orElse(-1);

        if (slot < 1 || slot > 6) {
            player.sendMessage(Text.of(TextColors.RED, "Invalid Slot. Slot number must be between 1 and 6."));
            return CommandResult.success();
        }
        
        PlayerPartyStorage partyStorage = Pixelmon.storageManager.getParty((EntityPlayerMP)player);
        Pokemon pokemon = partyStorage.get(slot - 1);

        if (pokemon == null ||  pokemon.isEgg()) {
            player.sendMessage(Text.of(TextColors.RED, "That pokemon doesn't exist or can't be evolved!"));
            return CommandResult.success();
        }
        
        if (pokemon.getHealth() < 1) {
            player.sendMessage(Text.of(TextColors.RED, "Please heal your pokemon before trying to evolve it!"));
            return CommandResult.success();
        }
        
        EntityPixelmon pixel = 	pokemon.getOrSpawnPixelmon(pokemon.getOwnerPlayer());

        if (pixel == null) {
            player.sendMessage(Text.of(TextColors.RED, "An error occurred while throwing out your Pokemon. Please try again!"));
            return CommandResult.success();
        }
        
        if (this.didEvolve(pixel)) {
            player.sendMessage(Text.of(TextColors.GREEN, "Successfully evolved " + pokemon.getSpecies().name + "!"));
        }
        else {
            player.sendMessage(Text.of(TextColors.RED, "No evolutions were found that "+ pokemon.getSpecies().name +" matches the requirements for!"));
            pixel.retrieve();
        }

        return CommandResult.success();
    }

    public static CommandSpec buildSpec() {
        return CommandSpec.builder()
            .description(Text.of("Fixes pokemon evolution issues for a specified slot"))
            .permission("amberutils.command.evofix")
            .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("slot"))))
            .executor(new EvoFixCommand())
            .build();
    }
    
    private boolean didEvolve(EntityPixelmon pixel) {
        for (Evolution evolution : pixel.getPokemonData().getEvolutions(LevelingEvolution.class)) {
            if (!this.doesPassConditions(pixel, evolution.conditions) || !pixel.testLevelEvolution(pixel.getPokemonData().getLevel())) {
                continue;
            }

            return true;
        }

        for (Evolution evolution : pixel.getPokemonData().getEvolutions(TickingEvolution.class)) {
            if (!this.doesPassConditions(pixel, evolution.conditions) || !pixel.testTickingEvolution()) {
                continue;
            }

            return true;
        }

        return false;
    }

    private boolean doesPassConditions(EntityPixelmon pixel, List<EvoCondition> conditions) {
        for (EvoCondition condition : conditions) {
            if (!condition.passes(pixel)) {
                return false;
            }
        }
        return true;
    }
}
