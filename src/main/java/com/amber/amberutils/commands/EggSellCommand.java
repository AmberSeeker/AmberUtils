package com.amber.amberutils.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.config.AmberUtilsConfig;
import com.amber.amberutils.helpers.GeneralHelpers;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;

import java.util.Optional;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayerMP;

public class EggSellCommand implements CommandExecutor {

    private AmberUtils plugin;

    private EggSellCommand(AmberUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        // Disables the module
        if (!AmberUtilsConfig.eggsell) {
            src.sendMessage(Text.of(TextColors.RED, "This module is disabled!"));
            return CommandResult.success();
        }

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Only players can use this command."));
            return CommandResult.success();
        }
        Player player = (Player) src;

        String slotArg = args.<String>getOne("slot").orElse("");
        if (slotArg.isEmpty()) {
            player.sendMessage(Text.of(TextColors.RED, "You must specify a slot number between 1 and 6 or 'party'."));
            player.sendMessage(Text.of(TextColors.YELLOW, "Usage: /eggsell <slot>|party"));
            return CommandResult.success();
        }

        if (slotArg.equalsIgnoreCase("party")) {
            sellPartyEggs(player);
        } else {
            try {
                int slot = Integer.parseInt(slotArg);
                sellSlotEgg(player, slot);
            } catch (NumberFormatException e) {
                player.sendMessage(
                        Text.of(TextColors.RED, "Invalid slot. Slot must be a number between 1 and 6 or 'party'."));
            }
        }

        return CommandResult.success();
    }

    public static CommandSpec buildSpec(AmberUtils plugin) {
        return CommandSpec.builder()
                .description(Text.of("Sells eggs from either slot or all of party."))
                .permission("amberutils.command.eggsell")
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("slot"))))
                .executor(new EggSellCommand(plugin))
                .build();
    }

    private void sellSlotEgg(Player player, int slot) {
        if (slot < 1 || slot > 6) {
            player.sendMessage(Text.of(TextColors.RED, "Invalid Slot. Slot number must be between 1 and 6."));
            return;
        }

        slot--;
        PlayerPartyStorage partyStorage = Pixelmon.storageManager.getParty((EntityPlayerMP) player);
        Pokemon pokemon = partyStorage.get(slot);

        if (pokemon == null || !pokemon.isEgg()) {
            player.sendMessage(Text.of(TextColors.RED, "That Pokemon doesn't exist or is not an egg."));
            return;
        }

        if (GeneralHelpers.isHA(pokemon) && !AmberUtilsConfig.sell_ha) {
            player.sendMessage(Text.of(TextColors.RED, "You can't sell HA Pokemon eggs!"));
            return;
        }

        if (pokemon.isShiny() && !AmberUtilsConfig.sell_shiny) {
            player.sendMessage(Text.of(TextColors.RED, "You can't sell Shiny Pokemon eggs!"));
            return;
        }

        int price = getEggPrice(pokemon);
        if (processPayment(player.getUniqueId(), price)) {

            Text message = Text.builder()
                    .append(Text.of(TextColors.GREEN, "Sold the "))
                    .append(getSpeciesHover(pokemon))
                    .append(Text.of(TextColors.GREEN, " Egg in slot: ", (slot + 1), " for $", price, "!"))
                    .build();

            player.sendMessage(message);
            partyStorage.set(slot, null);
            return;
        }

        player.sendMessage(
                Text.of(TextColors.RED, "An error occurred while processing your payment. The egg has not been sold."));
        return;
    }

    private void sellPartyEggs(Player player) {
        PlayerPartyStorage partyStorage = Pixelmon.storageManager.getParty((EntityPlayerMP) player);

        if (partyStorage.countEggs() == 0) {
            player.sendMessage(Text.of(TextColors.RED, "You have no eggs in your party."));
            return;
        }

        int price = 0;
        Text.Builder speciesHoversBuilder = Text.builder();
        ArrayList<StoragePosition> pokeToRemove = new ArrayList<>();
        for (Pokemon pokemon : partyStorage.getAll()) {
            if (pokemon == null || !pokemon.isEgg())
                continue;
            if (GeneralHelpers.isHA(pokemon) && !AmberUtilsConfig.sell_ha)
                continue;
            if (pokemon.isShiny() && !AmberUtilsConfig.sell_shiny)
                continue;
            speciesHoversBuilder.append(getSpeciesHover(pokemon)).append(Text.of(" "));
            price += getEggPrice(pokemon);
            // partyStorage.set(partyStorage.getPosition(pokemon), null);
            pokeToRemove.add(partyStorage.getPosition(pokemon));
        }

        if(pokeToRemove.isEmpty()) {
            player.sendMessage(Text.of(TextColors.RED, "You have no eggs eligible to be sold in your party."));
            return;
        }

        if (processPayment(player.getUniqueId(), price)) {
            for (StoragePosition spt : pokeToRemove) {
                partyStorage.set(spt, null);
            }
            Text speciesHover = speciesHoversBuilder.build();
            Text message = Text.builder()
                    .append(Text.of(TextColors.GREEN, "Sold all eligible Eggs for $", price, "!\n"))
                    .append(speciesHover)
                    .build();
            player.sendMessage(message);
            return;
        }
        player.sendMessage(
                Text.of(TextColors.RED, "An error occurred while processing your payment. The egg has not been sold."));
        return;
    }

    private int getEggPrice(Pokemon pokemon) {
        int price = AmberUtilsConfig.base_price;
        String[] goodNatures = { "Adamant", "Jolly", "Modest", "Timid" };

        if (pokemon.isShiny())
            price += AmberUtilsConfig.shiny_price;

        if (GeneralHelpers.isHA(pokemon))
            price += AmberUtilsConfig.ha_price;

        if (pokemon.getIVs().getTotal() == 186)
            price += AmberUtilsConfig.hundo_price;
        else if (Arrays.stream(pokemon.getIVs().getArray()).filter(value -> value == 31).count() >= 5)
            price += AmberUtilsConfig.perf_price;

        for (String nature : goodNatures)
            if (pokemon.getNature().getName().equalsIgnoreCase(nature))
                price += AmberUtilsConfig.nature_price;
        return price;
    }

    private boolean processPayment(UUID id, int amount) {

        Optional<UniqueAccount> accOpt = plugin.getEconomy().getOrCreateAccount(id);
        if (!accOpt.isPresent()) {
            return false;
        }

        UniqueAccount account = accOpt.get();
        Currency currency = plugin.getEconomy().getDefaultCurrency();
        BigDecimal amt = BigDecimal.valueOf(amount);

        try {
            account.deposit(currency, amt, Sponge.getCauseStackManager().getCurrentCause());
        } catch (Exception e) {
            plugin.getLogger().error("Payment processing failed for player: " + id, e);
            return false;
        }
        return true;
    }

    private Text getSpeciesHover(Pokemon pokemon) {
        Text speciesText = Text.builder()
                .append(Text.of(TextColors.GRAY,"["))
                .append(Text.of(TextColors.GOLD,pokemon.getSpecies().getPokemonName()))
                .append(Text.of(TextColors.GRAY,"]"))
                .color(TextColors.GOLD)
                .onHover(TextActions.showText(
                        Text.of(
                                TextColors.YELLOW, "Species: ", TextColors.WHITE, pokemon.getSpecies().getPokemonName(), "\n",
                                TextColors.YELLOW, "Nature: ", TextColors.WHITE, pokemon.getNature().getName(),
                                "\n",
                                TextColors.YELLOW, "Ability: ", TextColors.WHITE, pokemon.getAbilityName(),
                                "\n",
                                TextColors.YELLOW, "IVs: ", TextColors.WHITE,
                                pokemon.getIVs().getPercentageString(2), "\n",
                                TextColors.YELLOW, "Shiny: ", TextColors.WHITE, pokemon.isShiny() ? "Yes" : "No",
                                "\n",
                                TextColors.YELLOW, "Price: ", TextColors.WHITE, getEggPrice(pokemon))))
                .build();
        return speciesText;
    }
}