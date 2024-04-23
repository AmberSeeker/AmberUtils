package com.amber.amberutils.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.amber.amberutils.listeners.EventListeners;

public class Commands implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        src.sendMessage(Text.of("This is AmberUtils plugin version 0.0.1"));
        src.sendMessage(Text.of("Description: Utility stuff for Pixelmon"));
        // Add more information as needed
        return CommandResult.success();
    }

    public static CommandSpec buildSpec() {
        return CommandSpec.builder()
            .description(Text.of("Displays information about AmberUtils plugin"))
            .permission("amberutils.command.info") // Optional: Add permission node
            .executor(new Commands())
            .child(buildNoSpaceCommand(), "nospace")
            .build();
    }

    private static CommandSpec buildNoSpaceCommand() {
        return CommandSpec.builder()
            .description(Text.of("Toggle the 'no space' behavior"))
            .permission("amberutils.command.nospace") // Optional: Add permission node
            .executor((src, args) -> {
                // Toggle the 'no space' behavior
                EventListeners.setNoSpaceEnabled(!EventListeners.isNoSpaceEnabled());
                src.sendMessage(Text.of("Toggled 'no space' behavior: " + EventListeners.isNoSpaceEnabled()));
                return CommandResult.success();
            })
            .build();
    }
}
