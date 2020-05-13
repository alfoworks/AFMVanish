package ru.allformine.afmvanish;

import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import ru.allformine.afmvanish.commands.VanishCommand;
import ru.allformine.afmvanish.commands.VanishNoInteractCommand;
import ru.allformine.afmvanish.listeners.VanishEventListener;
import ru.allformine.afmvanish.vanish.VanishPacketListener;

import java.util.Optional;

@Plugin(
id = "afmvanish",
name = "AFMVanish",
description = "Best Vanish plugin for Sponge.",
version = "1.0",
url = "http://allformine.ru",
authors = {
"Iterator, HeroBrine1st_Erq"
}
)
public class AFMVanish {
	
	@Listener
	public void preInit(GamePreInitializationEvent event) {
		Sponge.getEventManager().registerListeners(this, new VanishEventListener());
		
		CommandSpec vanishSpec = CommandSpec.builder()
		                         .description(Text.of("Vanish"))
		                         .permission("afmvanish.vanish")
		                         .executor(new VanishCommand())
		                         .arguments(
		                         GenericArguments.optional(GenericArguments.string(Text.of("subcmd")))
		                         )
		                         .build();
		
		Sponge.getCommandManager().register(this, vanishSpec, "vanish", "v");
		
		CommandSpec vanishNoInteractSpec = CommandSpec.builder()
		                                   .description(Text.of("No interact"))
		                                   .permission("afmvanish.vanish")
		                                   .executor(new VanishNoInteractCommand())
		                                   .build();
		
		Sponge.getCommandManager().register(this, vanishNoInteractSpec, "ni");
	}
	
	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		Optional<PacketGate> optional = Sponge.getServiceManager().provide(PacketGate.class);
		optional.ifPresent(packetGate -> packetGate.registerListener(new VanishPacketListener(), PacketListener.ListenerPriority.FIRST, SPacketPlayerListItem.class));
	}
}
