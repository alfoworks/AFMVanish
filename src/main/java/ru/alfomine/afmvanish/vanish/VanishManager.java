package ru.alfomine.afmvanish.vanish;

import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import java.util.*;

public class VanishManager {
	
	public static final String vanishPermission = "afmvanish.vanish.staff";
	
	public static Set<Player> vanishList = new HashSet<>();
	public static Set<Player> interactDenyList = new HashSet<>();

	public static Set<String> tablistVisibleList = new HashSet<>();

	public static boolean isVanished(Player player) {
		return vanishList.contains(player);
	}

	public static boolean canInteract(Player player) {
		return !interactDenyList.contains(player);
	}

	public static int getOnlinePlayersNonVanished() {
		return tablistVisibleList.size();
	}

	public static void vanishPlayer(Player player, boolean effect, boolean onJoin) {
		setVanish(player, true, effect);

		vanishList.add(player);
		interactDenyList.add(player);

		if (!onJoin) {
			sendTabListPacket(true, player);
			tablistVisibleList.remove(player.getName());
		}

		VanishMessages.vanishNotify(String.format(onJoin ? "%s скрытно вошёл в игру" : "%s вошёл в ваниш.", player.getName()));
	}

	public static void unvanishPlayer(Player player, boolean effect, boolean onLeave) {
		setVanish(player, false, effect);

		vanishList.remove(player);
		interactDenyList.remove(player);

		if (!onLeave) {
			sendTabListPacket(false, player);
			tablistVisibleList.add(player.getName());
		}

		VanishMessages.vanishNotify(String.format(onLeave ? "%s скрытно вышел из игры." : "%s вышёл из ваниша.", player.getName()));
	}

	public static boolean switchVanish(Player player) {
		if (isVanished(player)) {
			unvanishPlayer(player, true, false);
			return false;
		} else {
			vanishPlayer(player, true, false);
			return true;
		}
	}

	public static boolean switchInteractAbility(Player player) {
		if (interactDenyList.contains(player)) {
			interactDenyList.remove(player);
			return true;
		} else {
			interactDenyList.add(player);
			return false;
		}
	}

	public static void sendTabListPacket(boolean remove, Player player) {
		Sponge.getServiceManager().provide(PacketGate.class).ifPresent(packetGate -> {
			packetGate.connectionByPlayer(player).ifPresent(packetConnection -> {
				SPacketPlayerListItem.Action action = remove ? SPacketPlayerListItem.Action.REMOVE_PLAYER : SPacketPlayerListItem.Action.ADD_PLAYER;
				List<EntityPlayerMP> playerList = new ArrayList<>();

				playerList.add((EntityPlayerMP) player);

				packetConnection.sendPacket(new SPacketPlayerListItem(action, playerList));
			});
		});
	}

	private static void setVanish(Player player, boolean enable, boolean effect) {
		player.offer(Keys.INVISIBLE, enable);
		player.offer(Keys.VANISH, enable);
		player.offer(Keys.VANISH_IGNORES_COLLISION, enable);
		player.offer(Keys.VANISH_PREVENTS_TARGETING, enable);

		if (effect) VanishEffects.applyVanishEffect(player);
	}
}