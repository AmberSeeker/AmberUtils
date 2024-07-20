package com.amber.amberutils.listeners;

import com.amber.amberdiscord.DiscordUtils;
import com.amber.amberutils.config.AmberUtilsConfig;
import com.amber.amberutils.helpers.GeneralHelpers;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.listener.EntityPlayerExtension;
import com.pixelmonmod.pixelmon.client.gui.GuiResources;

import javax.annotation.Resource;

import org.spongepowered.api.Sponge;

import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DiscordBroadcasts {
    
    String server = "";

    @SubscribeEvent
    public void onSpawn(SpawnEvent e) {

      if (!AmberUtilsConfig.discordbc)
      return;

      Entity entity = e.action.getOrCreateEntity();
      if (!(entity instanceof EntityPixelmon))
        return;
      
      EntityPixelmon pokemon = (EntityPixelmon)entity;
      if (pokemon.isBossPokemon() || pokemon.hasOwner())
        return;
      
      //Checks for Legendary/Ultra Beast
      Boolean isLegend = Boolean.valueOf(pokemon.getPokemonData().getSpecies().isLegendary());
      Boolean isBeast = Boolean.valueOf(pokemon.getPokemonData().getSpecies().isUltraBeast());
      Boolean isShiny = Boolean.valueOf(pokemon.getPokemonData().isShiny());
      
      if (isLegend.booleanValue() || isBeast.booleanValue()) 
      {
        EnumSpecies species = pokemon.getPokemonData().getSpecies();
        String formName = pokemon.getPokemonData().getFormEnum().toString();
        BlockPos location = e.action.spawnLocation.location.pos;
        World world = pokemon.getEntityWorld();
        EntityPlayerMP player = (EntityPlayerMP)e.action.spawnLocation.cause;
        Sponge.getPluginManager().getPlugin("discordutils").ifPresent(plugin -> {
          // DiscordUtils.legendSpawnNotif("Pikadex", "shiny", "legend", "eternatus", "Swampland M", "AmberSeeker", "WorldName", "FormName");
          server = DiscordUtils.server;
          DiscordUtils.legendSpawnNotif(server, GeneralHelpers.checkShiny(isShiny), GeneralHelpers.checkType(isLegend, isBeast), GeneralHelpers.getPokeNameWithForm(species.name(), formName), GeneralHelpers.getBiome(world, location), player.getName(), GeneralHelpers.dimName(world), GeneralHelpers.getForm(formName));
          });   
    }
  }

  @SubscribeEvent
  public void onCaught(CaptureEvent.SuccessfulCapture e) {

    if (!AmberUtilsConfig.discordbc)
      return;

    Pokemon pokemon = e.getPokemon().getPokemonData();
    //Checks for Legendary/Ultra Beast
    Boolean isLegend = Boolean.valueOf(pokemon.getSpecies().isLegendary());
    Boolean isBeast = Boolean.valueOf(pokemon.getSpecies().isUltraBeast());
    Boolean isShiny = Boolean.valueOf(pokemon.isShiny());

    if (isLegend.booleanValue() || isBeast.booleanValue()) 
    {
      EntityPlayerMP player = (EntityPlayerMP)e.player;
      ResourceLocation rl = GuiResources.getPokemonSprite(pokemon);
      Sponge.getPluginManager().getPlugin("discordutils").ifPresent(plugin -> {
        server = DiscordUtils.server;
        DiscordUtils.legendCaughtNotif(server, GeneralHelpers.checkShiny(isShiny), GeneralHelpers.checkType(isLegend, isBeast), GeneralHelpers.getPokeNameWithForm(pokemon.getSpecies().name(), pokemon.getFormEnum().toString()), GeneralHelpers.getForm(pokemon.getFormEnum().toString()), player.getName(), rl.toString());
      });
    }
  }

  @SubscribeEvent
  public void onDefeat(BeatWildPixelmonEvent e) {

    if (!AmberUtilsConfig.discordbc)
      return;
    
    EntityPixelmon pokemon = (EntityPixelmon)e.wpp.getEntity();
    Boolean isLegend = Boolean.valueOf(pokemon.getSpecies().isLegendary());
    Boolean isBeast = Boolean.valueOf(pokemon.getSpecies().isUltraBeast());
    Boolean isShiny = Boolean.valueOf(pokemon.getPokemonData().isShiny());
    if (isLegend.booleanValue() || isBeast.booleanValue()) {

    EntityPlayerMP player = (EntityPlayerMP)e.player;
    Sponge.getPluginManager().getPlugin("discordutils").ifPresent(plugin -> {
      server = DiscordUtils.server;
      DiscordUtils.legendDefeatNotif(server, GeneralHelpers.checkShiny(isShiny), GeneralHelpers.checkType(isLegend, isBeast), GeneralHelpers.getPokeNameWithForm(pokemon.getSpecies().name(), pokemon.getFormEnum().toString()), GeneralHelpers.getForm(pokemon.getFormEnum().toString()), player.getName());
    });
    }
  }
}
