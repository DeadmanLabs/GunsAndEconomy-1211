package sheridan.gcaa.common.server.projetile;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

import java.util.*;

@EventBusSubscriber
public class PlayerPosCacheHandler {
    private static final Map<UUID, PosCache> playerPosCache = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        playerPosCache.put(event.getEntity().getUUID(), new PosCache());
    }

    @SubscribeEvent
    public static void onPlayerLeaveServer(PlayerEvent.PlayerLoggedOutEvent event) {
        playerPosCache.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void updatePlayerPosCache(ServerTickEvent.Post event) {
            Set<UUID> removeSet = new HashSet<>();
            playerPosCache.forEach((playerUUID, posCache) -> {
                Player player = event.getServer().getPlayerList().getPlayer(playerUUID);
                if (player == null) {
                    removeSet.add(playerUUID);
                } else {
                    posCache.update(player);
                }
            });
            for (UUID id : removeSet) {
                playerPosCache.remove(id);
            }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PosCache posCache = playerPosCache.get(event.getEntity().getUUID());
        if (posCache != null) {
            posCache.clear();
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PosCache posCache = playerPosCache.get(event.getEntity().getUUID());
        if (posCache != null) {
            posCache.clear();
        }
    }

    public static AABB getPlayerAABB(Player player, int delay, float inflate) {
        if (delay == -1) {
            return player.getBoundingBox().inflate(inflate);
        }
        PosCache posCache = playerPosCache.get(player.getUUID());
        return posCache == null ? null :  posCache.getBoundingBox(delay, player, inflate);
    }
}
