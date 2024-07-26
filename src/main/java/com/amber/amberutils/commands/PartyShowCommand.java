package com.amber.amberutils.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class PartyShowCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
    
    public static CommandSpec buildSpec() {
        return CommandSpec.builder()
            .description(Text.of("Shows your pokemon in chat"))
            .permission("amberutils.command.partyshow")
            .executor(new PartyShowCommand())
            .build();
    }
}
