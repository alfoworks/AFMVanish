package ru.alfomine.afmvanish.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.alfomine.afmvanish.vanish.VanishManager;

public class VanishNoInteractCommand extends AFMCPCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) {
            reply(src, Text.of("Вы не можете выполнить это из консоли."));

            return CommandResult.success();
        }

        if (!VanishManager.isVanished((Player) src)) {
            reply(src, Text.of("Для выполнения этой команды вы должны находиться в ванише."));
            return CommandResult.success();
        }

        reply(src, Text.of(String.format("Теперь вы %s взаимодействовать с миром.",
                VanishManager.switchInteractAbility((Player) src) ? "можете" : "не можете")));

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "Vanish";
    }

    @Override
    public TextColor getColor() {
        return TextColors.DARK_AQUA;
    }
}