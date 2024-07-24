package com.amber.amberutils.handlers;

import org.spongepowered.api.text.format.TextColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChatColorManager {
    private static ChatColorManager instance;

    private Map<UUID, TextColor> playerColors;

    private ChatColorManager() {
        playerColors = new HashMap<>();
    }

    public static ChatColorManager getInstance() {
        if (instance == null) {
            instance = new ChatColorManager();
        }
        return instance;
    }

    public void setPlayerColor(UUID playerUUID, TextColor color) {
        playerColors.put(playerUUID, color);
    }

    public Optional<TextColor> getPlayerColor(UUID playerUUID) {
        return Optional.ofNullable(playerColors.get(playerUUID));
    }

    public void removePlayerColor(UUID playerUUID) {
        playerColors.remove(playerUUID);
    }
}
