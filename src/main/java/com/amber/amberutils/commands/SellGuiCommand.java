package com.amber.amberutils.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.config.AmberUtilsConfig;

public class SellGuiCommand implements CommandExecutor {

    
    @Override
    public CommandResult execute(org.spongepowered.api.command.CommandSource src, org.spongepowered.api.command.args.CommandContext args) {
        
        // Disables the module
        if (!AmberUtilsConfig.sellgui) {
            src.sendMessage(Text.of(TextColors.RED, "This module is disabled!"));
            return CommandResult.success();
        }

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(TextColors.RED, "Only players can use this command."));
            return CommandResult.success();
        }

        Player player = (Player) src;
        Inventory testGuiInventory = createTestGuiInventory();

        player.openInventory(testGuiInventory);
        return CommandResult.success();
    }

    
    private Inventory createTestGuiInventory() {
        Inventory testGuiInventory = Inventory.builder()
                .of(InventoryArchetypes.DOUBLE_CHEST)
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("Sell GUI")))
                .listener(InteractInventoryEvent.Close.class, this::onInventoryClose)
                .build(AmberUtils.getInstance());

        return testGuiInventory;
    }

    @Listener
    private void onInventoryClose(InteractInventoryEvent.Close event) {
        if (!(event.getTargetInventory() instanceof Inventory)) {
            return;
        }

        Inventory closedInventory = (Inventory) event.getTargetInventory();
        Player player = event.getCause().first(Player.class).orElse(null);
        int count = 0;
        // Check if the closed inventory is the test GUI inventory
        if (closedInventory.getName().get().equals("Sell GUI") && player != null) {
            // Return items to the player's inventory
            player.sendMessage(Text.of("AAAAAAAAAAAAAA"));
            for (Inventory slot : closedInventory.slots()) {
                if (count >= 54)
                break;
                Optional<ItemStack> itemStackOptional = slot.peek();
                count++;
                itemStackOptional.ifPresent(itemStack -> {
                    player.getInventory().offer(itemStack);
                });
                slot.clear();
            }
        }
    }

    public static CommandSpec buildSpec() {
        return CommandSpec.builder()
                .description(Text.of("Opens a Sell GUI"))
                .permission("amberutils.command.sellgui")
                .executor(new SellGuiCommand())
                .build();
    }
}
