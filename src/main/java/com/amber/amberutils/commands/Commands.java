package com.amber.amberutils.commands;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.listeners.EventListeners;
import com.amber.amberutils.sql_db.DatabaseManager;
import com.amber.amberutils.helpers.ToggleHelper;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;

public class Commands implements CommandExecutor {

    private static final Logger logger = AmberUtils.logger;

    public static final Map<UUID, Boolean> noSpaceToggles = AmberUtils.noSpaceToggles;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        src.sendMessage(Text.of(TextColors.GREEN, "AmberUtils-1.12.2-0.0.1-alpha"));
        src.sendMessage(Text.of(TextColors.GREEN, "Usage: /amberutils <subcommand|help|?>"));
        // Add more information as needed
        return CommandResult.success();
    }

    public static CommandSpec buildSpec() {
        return CommandSpec.builder()
            .description(Text.of("Displays information about AmberUtils plugin"))
            .permission("amberutils.command.info") // Optional: Add permission node
            .executor(new Commands())
            .child(buildNoSpaceSpec(), "nospace")
            .child(buildHelpSpec(), "help", "?")
            .child(buildReloadSpec(), "reload")
            .build();
    }

    private static CommandSpec buildNoSpaceSpec() {
        return CommandSpec.builder()
            .description(Text.of("Toggle the 'no space' behavior"))
            .permission("amberutils.command.nospace") // Optional: Add permission node
            .executor((src, args) -> {
                if (!(src instanceof Player)) {
                    src.sendMessage(Text.of(TextColors.RED, "Only players can use this command."));
                    return CommandResult.success();
                }
                Player player = (Player) src;
                UUID playerId = player.getUniqueId();
                boolean newValue = !getNoSpaceToggle(playerId);
                setNoSpaceToggle(playerId, newValue);
                src.sendMessage(Text.of(TextColors.GOLD, "Battle regardless of Space has been set to: " + newValue));
                return CommandResult.success();
            })
            .build();
    }

    //The helper for no_space
    private static ToggleHelper noSpaceToggle = ToggleHelper.noSpaceToggle;
    private static CommandSpec buildHelpSpec() {
        noSpaceToggle.setValue(null);
        return CommandSpec.builder()
            .description(Text.of("Displays help for AmberUtils plugin"))
            .permission("amberutils.command.help")
            .executor((src, args) -> {
                src.sendMessage(Text.of(TextColors.GREEN, "Available subcommands:"));
                if (src instanceof Player) {
                    Player player = (Player) src;
                    UUID playerId = player.getUniqueId();
                    if (noSpaceToggles.get(playerId) != null)
                    noSpaceToggle.setValue(noSpaceToggles.get(playerId));
                }
                src.sendMessage(Text.of(TextColors.YELLOW, "/amberutils nospace - Fight Wild Pokemon regardless of Space Enabled: " + noSpaceToggle.getValue()));
                noSpaceToggle.setValue(null);
                return CommandResult.success();
            })
            .build();
        }

    private static CommandSpec buildReloadSpec() {
        return CommandSpec.builder()
            .description(Text.of("Reloads configuration or data"))
            .permission("amberutils.command.reload")
            .executor((src, args) -> {
                if (src instanceof Player) {
                    src.sendMessage(Text.of(TextColors.GREEN, "Reloading AmberUtils..."));
                    logger.info("§aReloading AmberUtils...");
                } else {
                    logger.info("§aReloading AmberUtils...");
                }
                DatabaseManager.savePlayerData(src);
                DatabaseManager.loadPlayerData(src);
                return CommandResult.success();
            })
            .build();
        }

    public static boolean getNoSpaceToggle(UUID playerId) {
        return noSpaceToggles.getOrDefault(playerId, false);
    }
    
    private static void setNoSpaceToggle(UUID playerId, boolean value) {
        noSpaceToggles.put(playerId, value);
    }
}
