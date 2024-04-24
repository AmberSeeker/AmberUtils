package com.amber.amberutils.helpers;

import com.amber.amberutils.AmberUtils;
import java.util.UUID;
import java.util.Map;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import net.minecraft.entity.player.EntityPlayerMP;

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

}
