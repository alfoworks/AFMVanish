package ru.alfomine.afmvanish.vanish;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

public class VanishMessages {
    public static void vanishNotify(String message) {
        MessageChannel channel = MessageChannel.permission(VanishManager.vanishPermission);

        channel.send(Text.of(TextColors.DARK_AQUA, message));
    }

    public static void vanishNotifyPlayer(String message, Player player) {
        player.sendMessage(Text.of(TextColors.DARK_AQUA, message));
    }
}
