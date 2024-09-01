package com.amber.amberutils.listeners;

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.amber.amberutils.handlers.ChatColorManager;

public class ChatListener {

    @Listener(order = Order.POST)
    public void onPlayerChat(MessageChannelEvent.Chat event) {
        if (!(event.getSource() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSource();
        TextColor color = ChatColorManager.getInstance().getPlayerColor(player.getUniqueId()).orElse(TextColors.WHITE);

        MessageEvent.MessageFormatter formatter = event.getFormatter();
        formatter.setBody(Text.of(color, event.getRawMessage()));
    }

    @Listener(order = Order.POST)
    public void uchatListener(SendChannelMessageEvent event) {
        if (!(event.getSender() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSender();
        TextColor color = ChatColorManager.getInstance().getPlayerColor(player.getUniqueId()).orElse(TextColors.RESET);
        String ncolor = "";
        if (!color.equals(TextColors.RESET)) {
            ncolor = getColorCode(color);
        }

        String originalContent = event.getMessage().toPlain();

        // System.out.println(ncolor);
        event.setMessage(ncolor+originalContent);
    }

    private String getColorCode(TextColor color) {
        return TextSerializers.FORMATTING_CODE.serialize(Text.of(color, ""));
    }
}
