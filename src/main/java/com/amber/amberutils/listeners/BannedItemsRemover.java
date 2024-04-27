package com.amber.amberutils.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.amber.amberutils.helpers.ItemNameHelper;
import com.amber.amberutils.config.BannedItemsList;

public class BannedItemsRemover {

    @Listener
    public void onCraftingGridClose(InteractInventoryEvent.Close event) {
        if (event.getTargetInventory() instanceof Inventory) {
            Inventory inventory = (Inventory) event.getTargetInventory();
            if (inventory.getName().get().equals("Result")) {
                for (Inventory slot : inventory.slots()) {
                    ItemStackSnapshot itemSnapshot = slot.peek().map(stack -> stack.createSnapshot()).orElse(null);
                    for (String item : BannedItemsList.banlist) {
                        if (itemSnapshot != null && itemSnapshot.getType().getName().equalsIgnoreCase(item)) {
                            slot.clear();
                            if (event.getCause().first(Player.class).isPresent()) {
                                Player player = event.getCause().first(Player.class).get();
                                player.sendMessage(Text.of(TextColors.RED, ItemNameHelper.snakeCaseToTitleCase(item) + " is banned and was deleted."));
                            }
                        }
                    }
                }
            }
        }
    }
}
