package com.amber.amberutils.commands;

import com.amber.amberutils.config.AmberUtilsConfig;
import com.amber.amberutils.handlers.ChatColorManager;
import com.amber.amberutils.helpers.ColorArgument;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;

public class ChatColorCommand implements CommandExecutor {

    public static final Map<String, TextColor> COLOR_MAP = new HashMap<>();

    static {
        COLOR_MAP.put("reset", TextColors.RESET);
        COLOR_MAP.put("green", TextColors.GREEN);
        COLOR_MAP.put("aqua", TextColors.AQUA);
        COLOR_MAP.put("red", TextColors.RED);
        COLOR_MAP.put("light_purple", TextColors.LIGHT_PURPLE);
        COLOR_MAP.put("yellow", TextColors.YELLOW);
        COLOR_MAP.put("white", TextColors.WHITE);
        COLOR_MAP.put("black", TextColors.BLACK);
        COLOR_MAP.put("blue", TextColors.BLUE);
        COLOR_MAP.put("dark_aqua", TextColors.DARK_AQUA);
        COLOR_MAP.put("dark_blue", TextColors.DARK_BLUE);
        COLOR_MAP.put("dark_gray", TextColors.DARK_GRAY);
        COLOR_MAP.put("dark_green", TextColors.DARK_GREEN);
        COLOR_MAP.put("dark_purple", TextColors.DARK_PURPLE);
        COLOR_MAP.put("dark_red", TextColors.DARK_RED);
        COLOR_MAP.put("gold", TextColors.GOLD);
        COLOR_MAP.put("gray", TextColors.GRAY);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        // Disables the module
        if (!AmberUtilsConfig.chatcolor) {
            src.sendMessage(Text.of(TextColors.RED, "This module is disabled!"));
            return CommandResult.success();
        }

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Only players can use this command."));
            return CommandResult.success();
        }

        Player player = (Player) src;
        String colorName = args.<String>getOne("color").orElse("").toLowerCase();

        TextColor color = COLOR_MAP.get(colorName);

        if (color == null) {
            player.sendMessage(Text.of(TextColors.RED, "Invalid color. Available colors: ", String.join(", ", COLOR_MAP.keySet())));
            return CommandResult.success();
        }

        ChatColorManager.getInstance().setPlayerColor(player.getUniqueId(), color);
        player.sendMessage(Text.of(TextColors.GREEN, "Your chat color has been set to ", color, colorName, TextColors.GREEN, "."));

        return CommandResult.success();
    }

    public static CommandSpec buildSpec() {
        return CommandSpec.builder()
            .description(Text.of("Sets your chat color"))
            .permission("amberutils.command.setchatcolor")
            .arguments(new ColorArgument(Text.of("color")))
            .executor(new ChatColorCommand())
            .build();
    }
}
