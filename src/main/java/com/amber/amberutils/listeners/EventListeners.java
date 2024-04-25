package com.amber.amberutils.listeners;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.helpers.GeneralHelpers;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.api.events.PokeballImpactEvent;
import com.pixelmonmod.pixelmon.api.events.AggressionEvent;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.raids.JoinRaidEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent.SuccessfulRaidCapture;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventListeners {

    //Check for Empty Pokeball or Pokemon thrown on a wild Pokemon
    @SubscribeEvent
    public void onPokeballImpact(PokeballImpactEvent e) {
        if (!(e.getEntityHit() instanceof EntityPixelmon))
            return; 
        if (!(e.pokeball.getThrower() instanceof EntityPlayerMP))
            return; 
        EntityPixelmon pokemon = (EntityPixelmon)e.getEntityHit();
        if (pokemon.isBossPokemon() || pokemon.hasOwner())
            return;
        EntityPlayerMP player = (EntityPlayerMP)e.pokeball.getThrower();
        if (GeneralHelpers.playerCheck(player)) {
            e.setCanceled(true);
            CommandChatHandler.sendFormattedChat(player, TextFormatting.RED, "You have no space in your Party or PC! Can't engage!");
        }
    }

    //Check for aggressive pokemon
    @SubscribeEvent
    public void onWildAggression(AggressionEvent e) {
        if (!(e.aggressor instanceof EntityPixelmon))
            return;
        if (!(e.player instanceof EntityPlayerMP))
            return;
        EntityPixelmon pokemon = (EntityPixelmon)e.aggressor;
        if (pokemon.isBossPokemon() || pokemon.hasOwner())
            return;

        EntityPlayerMP player = (EntityPlayerMP)e.player;
        if (GeneralHelpers.playerCheck(player)) {
            e.setCanceled(true);
        }
    }

    //Check for battles with Wild Pokemon if all else fails...
    @SubscribeEvent
    public void onBattleStarted(BattleStartedEvent e) {
        BattleParticipant p1 = e.participant1[0];
        BattleParticipant p2 = e.participant2[0];
        if (p1 instanceof WildPixelmonParticipant && p2 instanceof WildPixelmonParticipant)
            return; 
        if (p1 instanceof WildPixelmonParticipant || p2 instanceof WildPixelmonParticipant) {
            PlayerParticipant participant = (p1 instanceof PlayerParticipant) ? (PlayerParticipant)p1 : (PlayerParticipant)p2;
            WildPixelmonParticipant pokeparticipant = (p1 instanceof PlayerParticipant) ? (WildPixelmonParticipant)p2 : (WildPixelmonParticipant)p1;
            EntityPlayerMP player = (EntityPlayerMP)participant.getEntity();
            EntityPixelmon pokemon = (EntityPixelmon)pokeparticipant.getEntity();
            if (pokemon.isBossPokemon())
            return;
            if (GeneralHelpers.playerCheck(player)) {
                e.setCanceled(true);
                CommandChatHandler.sendFormattedChat(player, TextFormatting.RED, "You have no space in your Party or PC! Can't engage!");
            } 
        } 
    }

    //Check for player trying to join a raid
    @SubscribeEvent
    public void onRaidJoin(JoinRaidEvent e) {
        EntityPlayerMP player = (EntityPlayerMP)e.getPlayer();
        if (GeneralHelpers.playerCheck(player)) {
            e.setCanceled(true);
            CommandChatHandler.sendFormattedChat(player, TextFormatting.RED, "You can't join the raid as you have no space in your Party or PC!");
        }
    }

    //Moronic Fusion Raids Fix
    @SubscribeEvent
    public void onRaidCaptureSuccess(SuccessfulRaidCapture e) {
        Pokemon pokemon = e.getRaidPokemon();
        String pname = pokemon.getSpecies().name();
        int pform = pokemon.getForm();
        AmberUtils.logger.info("Name: " + pname + " Form: " + pform + " ENUM: "+ pokemon.getFormEnum());
        PlayerPartyStorage storage = Pixelmon.storageManager.getParty(e.player);
        
        //Kyurem Stuff
        if (pname.equalsIgnoreCase("Kyurem") && pform != 0)
        {
            Pokemon kyuFuse = null;
            
            //Gib Zekrom to player
            if (pform == 1)
            kyuFuse = Pixelmon.pokemonFactory.create(EnumSpecies.Zekrom);
            
            //Gib Reshiram to player
            if (pform == 2)
            kyuFuse = Pixelmon.pokemonFactory.create(EnumSpecies.Reshiram);
            
            //Add to player's party and change Kyurem to Normal
            storage.add(kyuFuse);
            pokemon.setForm(0);
        }
        
        //Necrozma Stuff
        if (pname.equalsIgnoreCase("Necrozma") && pform != 0)
        {
            Pokemon necroFuse = null;
            
            //Gib Solgaleo to player
            if (pform == 1)
            necroFuse = Pixelmon.pokemonFactory.create(EnumSpecies.Solgaleo);

            //Gib Lunala to player
            if (pform == 2)
            necroFuse = Pixelmon.pokemonFactory.create(EnumSpecies.Lunala);
            
            //Add to player's party and change Necrozma to Normal
            storage.add(necroFuse);
            pokemon.setForm(0);
        }
    }
}
