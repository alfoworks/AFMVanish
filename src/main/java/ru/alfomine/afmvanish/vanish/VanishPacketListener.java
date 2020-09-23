package ru.alfomine.afmvanish.vanish;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import org.spongepowered.api.Sponge;

import java.util.ArrayList;
import java.util.List;

public class VanishPacketListener extends PacketListenerAdapter {
    
    @Override
    public void onPacketWrite(PacketEvent event, PacketConnection connection) {
        try {
            if (!(event.getPacket() instanceof SPacketPlayerListItem)) {
                return;
            }
            
            SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();

            SPacketPlayerListItem.Action action = (SPacketPlayerListItem.Action) packet.getClass().getDeclaredMethod("accessor$getAction").invoke(packet);

            if (action == SPacketPlayerListItem.Action.REMOVE_PLAYER) return; // Мы убираем игроков, только если их обновляют/добавляют

            List<EntityPlayerMP> players = new ArrayList<>();

            for (SPacketPlayerListItem.AddPlayerData data : (List<SPacketPlayerListItem.AddPlayerData>) packet.getClass().getDeclaredMethod("accessor$getPlayerDatas").invoke(packet)) {
                if (VanishManager.tablistVisibleList.contains(data.getProfile().getName())) {
                    Sponge.getServer().getPlayer(data.getProfile().getId()).ifPresent(player -> players.add((EntityPlayerMP) player));
                }
            }

            // Если не осталось игроков - не будем лишний раз отправлять пакет

            if (players.size() == 0) {
                event.setCancelled(true);
                return;
            }

            event.setPacket(new SPacketPlayerListItem(action, players));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
