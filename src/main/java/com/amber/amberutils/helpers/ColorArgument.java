package com.amber.amberutils.helpers;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import com.amber.amberutils.commands.ChatColorCommand;

import java.util.List;
import java.util.stream.Collectors;

public class ColorArgument extends CommandElement {

    public ColorArgument(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        return args.next();
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        String word = args.nextIfPresent().orElse("").toLowerCase();

        if (word.isEmpty()) {
            return ChatColorCommand.COLOR_MAP.keySet().stream().collect(Collectors.toList());
        }

        // Filter colors based on the prefix
        return ChatColorCommand.COLOR_MAP.keySet().stream()
                .filter(color -> color.startsWith(word))
                .collect(Collectors.toList());
    }
}
