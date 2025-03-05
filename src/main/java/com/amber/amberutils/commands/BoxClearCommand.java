package com.amber.amberutils.commands;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.config.AmberUtilsConfig;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;

import net.minecraft.entity.player.EntityPlayerMP;

public class BoxClearCommand implements CommandExecutor {

    private AmberUtils plugin;

    private Map<UUID, Integer> pending_boxclears = new HashMap<>();

    public BoxClearCommand(AmberUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!AmberUtilsConfig.boxclear || plugin.getEconomy() == null) {
            src.sendMessage(Text.of(TextColors.RED, "This module is unavailable."));
            return CommandResult.success();
        }

        if (!(src instanceof EntityPlayerMP)) {
            src.sendMessage(Text.of(TextColors.RED, "This command can only be executed by a player."));
            return CommandResult.success();
        }
        EntityPlayerMP player = (EntityPlayerMP) src;
        UUID playerID = player.getUniqueID();

        if (!args.hasAny("box")) {
            src.sendMessage(Text.of(TextColors.RED, "You must specify a box to clear.\n", TextColors.YELLOW, "Usage: /boxclear <box number>"));
            return CommandResult.success();
        }
        
        int box = args.<Integer>getOne("box").orElse(-1);
        if (box < 1 || box > PixelmonConfig.computerBoxes) {
            src.sendMessage(
                    Text.of(TextColors.RED, "Invalid slot. Please enter a number between 1 and " + PixelmonConfig.computerBoxes + "!"));
            return CommandResult.success();
        }

        UniqueAccount playerAccount = plugin.getEconomy()
                .getOrCreateAccount(playerID)
                .orElseThrow(() -> new IllegalStateException("You do not have an economy account."));

        PCBox storage = Pixelmon.storageManager.getPCForPlayer(player).getBox(box - 1);
        if (storage == null) {
            src.sendMessage(Text.of(TextColors.RED, "You do not have a box in that slot."));
            return CommandResult.success();
        }

        if (storage.countAll() < 1) {
            src.sendMessage(Text.of(TextColors.RED, "That box is empty."));
            return CommandResult.success();
        }

        BigDecimal price = BigDecimal.valueOf(AmberUtilsConfig.boxclear_price);
        BigDecimal balance = playerAccount.getBalance(plugin.getEconomy().getDefaultCurrency());
        if (balance.compareTo(price) < 0) {
            src.sendMessage(Text.of(TextColors.RED, "You do not have enough money to clear the box.\nYou need ", plugin.getEconomy().getDefaultCurrency().getSymbol(), price));
            return CommandResult.success();
        }

        if (!pending_boxclears.containsKey(playerID)) {
            pending_boxclears.put(playerID, box);
            src.sendMessage(Text.of(TextColors.YELLOW, "Warning all Pokemon including eggs contained in Box: ", box,
                    " will be cleared!\nThis is an irreversible process and costs ",
                    plugin.getEconomy().getDefaultCurrency().getSymbol(), price, "!"));
            src.sendMessage(Text.of(TextColors.GREEN, "Please retype the command to continue."));

            Sponge.getScheduler().createTaskBuilder()
                    .delay(30, TimeUnit.SECONDS)
                    .execute(() -> {
                        if (pending_boxclears.containsKey(playerID)) {
                            pending_boxclears.remove(playerID);
                            Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(playerID);
                            if (onlinePlayer.isPresent()) {
                                onlinePlayer.get().sendMessage(Text.of(TextColors.RED, "Your pending box clear has expired."));
                            }
                        }
                    })
                    .submit(plugin);
            return CommandResult.success();
        }

        int pending = pending_boxclears.get(playerID);
        if (pending != box) {
            src.sendMessage(Text.of(
                    TextColors.RED, "You have already started clearing a different box. Please finish that first.\nCommand execution stopped."));
            pending_boxclears.remove(playerID);
            return CommandResult.success();
        }

        playerAccount.withdraw(plugin.getEconomy().getDefaultCurrency(), price,
                Sponge.getCauseStackManager().getCurrentCause());

        String pokemonList = Arrays.stream(storage.getAll())
                .filter(poke -> poke != null)
                .map(poke -> poke.getSpecies().name)
                .collect(Collectors.joining(", "));

        plugin.getLogger()
                .info("Clearing Box: " + box + " of Player: " + player.getName() + "!\nPokemon: " + pokemonList + ".");

        for (int i = 0; i < 30; i++) {
            storage.set(i, null);
        }

        src.sendMessage(Text.of(TextColors.GREEN, "Box " + box + " has been cleared!"));
        pending_boxclears.remove(playerID);
        return CommandResult.success();
    }

    public static CommandSpec buildSpec(AmberUtils plugin) {
        CommandElement boxArg = GenericArguments.choices(Text.of("box"), IntStream.rangeClosed(1, PixelmonConfig.computerBoxes)
                .boxed()
                .collect(Collectors.toMap(i -> i.toString(), i -> i)));
        return CommandSpec.builder()
                .description(Text.of("Clear a PC Box."))
                .permission("amberutils.command.boxclear")
                .arguments(boxArg)
                .executor(new BoxClearCommand(plugin))
                .build();
    }
}
