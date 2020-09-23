package ru.alfomine.afmvanish.listeners;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import ru.alfomine.afmvanish.vanish.VanishManager;
import ru.alfomine.afmvanish.vanish.VanishMessages;

import java.util.ArrayList;

public class VanishEventListener {
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		if (!event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
			VanishManager.tablistVisibleList.add(event.getTargetEntity().getName());
			VanishManager.sendTabListPacket(false, event.getTargetEntity());
		} else {
			VanishManager.vanishPlayer(event.getTargetEntity(), false, true);

			event.setMessageCancelled(true);

			VanishMessages.vanishNotifyPlayer("Вы скрытно вошли в игру. Напишите /vanish чтобы выйти из ваниша.", event.getTargetEntity());
		}
	}
	
	@Listener
	public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
		if (VanishManager.isVanished(event.getTargetEntity())) {
			VanishManager.unvanishPlayer(event.getTargetEntity(), false, true);
			VanishManager.interactDenyList.remove(event.getTargetEntity());
		}

		if (event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
			event.setMessageCancelled(true);
		}
	}
	
	@Listener
	public void onTabComplete(TabCompleteEvent event) {
		for (String text : new ArrayList<>(event.getTabCompletions())) {
			Sponge.getServer().getPlayer(text).ifPresent(player -> {
				if (VanishManager.isVanished(player)) {
					event.getTabCompletions().remove(text);
				}
			});
		}
	}
	
	// ========================================== //
	
	@Listener
	public void onInteract(InteractEvent event, @Root Player player) {
		// if (!VanishManager.isVanished(player)) return;
		if (VanishManager.canInteract(player)) return;
		
		event.setCancelled(true);
	}
	
	@Listener
	public void onPlayerChat(MessageChannelEvent.Chat event, @First Player player) {
		// if (!VanishManager.isVanished(player)) return;
		if (VanishManager.canInteract(player)) return;
		
		event.setCancelled(true);
	}
	
	@Listener
	public void onPickup(ChangeInventoryEvent.Pickup event, @Root Player player) {
		// if (!VanishManager.isVanished(player)) return;
		if (VanishManager.canInteract(player)) return;
		
		event.setCancelled(true);
	}
	
	@Listener
	public void onClickInventory(ClickInventoryEvent event, @Root Player player) {
		// if (!VanishManager.isVanished(player)) return;
		if (VanishManager.canInteract(player)) return;
		if (event instanceof ClickInventoryEvent.NumberPress) return;
		if (event instanceof ClickInventoryEvent.Middle) return;
		if (event.getTargetInventory().getArchetype() == InventoryArchetypes.PLAYER) return;
		event.setCancelled(true);
	}
	
	@Listener
	public void onCollide(CollideBlockEvent event, @Root Player player) {
		if (VanishManager.canInteract(player)) return;
		event.setCancelled(true);
	}
	
	// ========================================== //
	
	@Listener
	public void onClientPingServerEvent(ClientPingServerEvent event) {
		if (!event.getResponse().getPlayers().isPresent()) return;
		
		ClientPingServerEvent.Response.Players players = event.getResponse().getPlayers().get();
		players.setOnline(VanishManager.getOnlinePlayersNonVanished());
		
		players.getProfiles().removeIf(gameProfile -> VanishManager.isVanished(Sponge.getServer().getPlayer(gameProfile.getUniqueId()).get()));
	}
}
