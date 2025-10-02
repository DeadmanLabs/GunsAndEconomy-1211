package sheridan.gcaa.capability;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.packets.c2s.SyncPlayerStatusPacket;
import sheridan.gcaa.network.packets.s2c.BroadcastPlayerStatusPacket;

@EventBusSubscriber(modid = GCAA.MODID)
public class PlayerStatusEvents {

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            PlayerStatus cap = player.getData(ModAttachments.PLAYER_STATUS);
            if (cap != null && cap.dataChanged) {
                PacketDistributor.sendToPlayersTrackingEntity(player,
                        new BroadcastPlayerStatusPacket(
                                player.getId(),
                                cap.getLastShoot(),
                                cap.getLastChamberAction(),
                                cap.getLocalTimeOffset(),
                                cap.getLatency(),
                                cap.getBalance(),
                                cap.isReloading()
                        ));
                cap.dataChanged = false;
            }
        } else {
            PlayerStatus cap = player.getData(ModAttachments.PLAYER_STATUS);
            if (cap != null && cap.dataChanged) {
                PacketDistributor.sendToServer(
                        new SyncPlayerStatusPacket(
                                cap.getLastShoot(),
                                cap.getLastChamberAction(),
                                cap.getLocalTimeOffset(),
                                cap.isReloading()
                        ));
                cap.dataChanged = false;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        PlayerStatus cap = player.getData(ModAttachments.PLAYER_STATUS);
        if (cap != null) {
            PacketDistributor.sendToPlayer(player,
                    new BroadcastPlayerStatusPacket(
                            player.getId(),
                            cap.getLastShoot(),
                            cap.getLastChamberAction(),
                            cap.getLocalTimeOffset(),
                            cap.getLatency(),
                            cap.getBalance(),
                            cap.isReloading()));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player original = event.getOriginal();
            Player newPlayer = event.getEntity();
            PlayerStatus oldData = original.getData(ModAttachments.PLAYER_STATUS);
            PlayerStatus newData = newPlayer.getData(ModAttachments.PLAYER_STATUS);
            if (oldData != null && newData != null) {
                newData.copyFrom(oldData);
            }
        }
    }


}
