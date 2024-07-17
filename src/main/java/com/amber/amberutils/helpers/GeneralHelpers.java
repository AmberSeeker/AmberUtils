package com.amber.amberutils.helpers;

import com.amber.amberutils.AmberUtils;
import java.util.UUID;

import org.spongepowered.api.Sponge;

import java.util.Map;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GeneralHelpers {

    public static final Map<UUID, Boolean> noSpaceToggles = AmberUtils.noSpaceToggles;

    public static boolean getNoSpaceToggle(UUID playerId) {
        return noSpaceToggles.getOrDefault(playerId, false);
    }
    
    public static void setNoSpaceToggle(UUID playerId, boolean value) {
        noSpaceToggles.put(playerId, value);
    }
    
    public static boolean playerCheck(EntityPlayerMP player) {
        if (!getNoSpaceToggle(player.getUniqueID())) {
            PlayerPartyStorage ptStorage = Pixelmon.storageManager.getParty(player);
            PCStorage pcStorage = Pixelmon.storageManager.getPCForPlayer(player);
            if (ptStorage != null && pcStorage != null && ptStorage.countAll() == 6 && pcStorage.countAll() == PixelmonConfig.computerBoxes * 30)
                return true;
        }
        return false;
    }

    // Get the formatted Dimension Name
    public static String dimName(World world) {
        org.spongepowered.api.world.World spongeWorld = Sponge.getServer().getWorld(world.getWorldInfo().getWorldName()).orElse(null);
        String dimensionName = spongeWorld.getDimension().getType().getName();
        
        String[] words = dimensionName.split("_");
        StringBuilder formattedName = new StringBuilder();
    
        for (String word : words) {
            if (!word.isEmpty()) {
                String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                formattedName.append(capitalizedWord).append(" ");
            }
        }
    
        String dimension = formattedName.toString().trim();    
    
        return dimension;
    }

    // Get the fromatted Biome Name
    public static String getBiome(World world, BlockPos location)
    {
        String biome = world.getBiomeForCoordsBody(location).biomeName;
        int capitalCount = 0, iterator = 0;
        while (iterator < biome.length())
        {
            if (Character.isUpperCase(biome.charAt(iterator)))
            {
                capitalCount++;
                if (capitalCount > 1)
                {
                    if (biome.charAt(iterator - 1) != ' ')
                    {
                        biome = biome.substring(0, iterator) + ' ' + biome.substring(iterator);
                        iterator++;
                    }
                }
            }
            iterator++;
        }
        return biome;
    }

    // Get the formatted Pokemon Name with Form
    public static String getPokeNameWithForm(String pokeName, String formName) {
        String[] forms = {"alolan", "galarian", "hisuian", "therian", "resolute", "primal", "crowned", "white", "black", "ultra", "eternamax", "unbound", "mega"};
        String[] splpokes = {"tapu_lele", "tapu_koko", "tapu_bulu", "tapu_fini"};
        for (String splpoke : splpokes)
        if (pokeName.equalsIgnoreCase(splpoke))
        {
            pokeName = pokeName.replace("_", "-");
        }
        if (pokeName.equalsIgnoreCase("typenull")) {
            pokeName = "Type-Null";
        }
        if (pokeName.equalsIgnoreCase("silvally") && !formName.equalsIgnoreCase("normal")) {
            pokeName = pokeName + "-" + formName.toLowerCase();
            return pokeName;
        }

        if (formName.equals("Normal")) {
            return pokeName;
        }
        else if (formName.equalsIgnoreCase("megax")) {
            pokeName = pokeName + "-mega-x";
        }
        else if (formName.equalsIgnoreCase("megay")) {
            pokeName = pokeName + "-mega-y";
        }
        else if (formName.equalsIgnoreCase("dawn")) {
            pokeName = pokeName + "-dawn-wings";
        }
        else if (formName.equalsIgnoreCase("dusk")) {
            pokeName = pokeName + "-dusk-mane";
        }
        else if (formName.equalsIgnoreCase("icerider")) {
            pokeName = pokeName + "-ice-rider";
        }
        else if (formName.equalsIgnoreCase("shadowrider")) {
            pokeName = pokeName + "-shadow-rider";
        }
        else {
            for (String form : forms) {
                if (formName.equalsIgnoreCase(form)) {
                    pokeName = pokeName + "-" + form;
                }
            }    
        }
        return pokeName;
    }

    // Check if the pokemon is shiny
    public static String checkShiny(Boolean isShiny) {
        if (isShiny) {
            return "shiny";
        }
        else {
            return "normal";
        }
    }
    // Get the type of pokemon
    public static String checkType(Boolean isLegend, Boolean isBeast) {
        if (isLegend) {
            return "legend";
        }
        else if (isBeast) {
            return "beast";
        }
        else {
            return "normal";
        }
    }

    // Get formatted form name
    public static String getForm(String form) {
        if (form.equalsIgnoreCase("NoForm"))
        return "Normal";
        else {
            form = form.toLowerCase();
            form = form.substring(0, 1).toUpperCase() + form.substring(1, form.length());
            return form;
        }
    }
}
