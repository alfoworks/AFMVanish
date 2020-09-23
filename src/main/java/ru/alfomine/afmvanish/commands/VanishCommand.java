package ru.alfomine.afmvanish.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.alfomine.afmvanish.vanish.VanishManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VanishCommand extends AFMCPCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Optional<String> subCommand = args.getOne(Text.of("subcmd"));

        if (subCommand.isPresent()) {
            if (subCommand.get().equalsIgnoreCase("list")) {
                List<String> vanished = VanishManager.vanishList.stream().map(Player::getName).collect(Collectors.toList());

                if (vanished.size() < 1) {
                    reply(src, Text.of("В данный момент на сервере нет игроков в ванише."));

                    return CommandResult.success();
                }

                reply(src, Text.of(String.format("Список игроков в ванише: %s.", "-" + String.join("\n-", vanished))));
            } else {
                reply(src, Text.of("Неизвестная подкоманда."));
            }
        } else {
            if (!(src instanceof Player)) {
                reply(src, Text.of("Вы не можете выполнить это из консоли."));

                return CommandResult.success();
            }

            reply(src, Text.of(VanishManager.switchVanish((Player) src) ? "Вы включили ваниш." : "Вы выключили ваниш."));

        }

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
