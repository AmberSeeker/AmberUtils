package com.amber.amberutils.helpers;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.user.UserStorageService;
import java.util.Optional;
import java.util.UUID;

public class NameHelper {

    public static String getPlayerName(UUID playerId) {
        Optional<Player> playerOptional = Sponge.getServer().getPlayer(playerId);
        if (playerOptional.isPresent()) {
            return playerOptional.get().getName();
        } else {
            // Player is not online, try to fetch their name from storage service
            Optional<UserStorageService> userStorageServiceOptional = Sponge.getServiceManager().provide(UserStorageService.class);
            if (userStorageServiceOptional.isPresent()) {
                Optional<String> playerNameOptional = userStorageServiceOptional.get().get(playerId).map(user -> user.getName());
                return playerNameOptional.orElse("Unknown"); // Return "Unknown" if player name is not found
            } else {
                // UserStorageService is not available
                return "Unknown";
            }
        }
    }
}    