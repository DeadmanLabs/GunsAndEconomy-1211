package sheridan.gcaa.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.minecraft.core.registries.BuiltInRegistries;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.animation.AnimationHandler;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.screens.RecoilModifyScreen;
import sheridan.gcaa.items.AutoRegister;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    private static int timeOffsetSyncTick = 0;

    @SubscribeEvent
    public static void onTickPre(LevelTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        try {
            if (minecraft.screen instanceof RecoilModifyScreen) {
                Clients.cancelLooperWork.set(false);
                Clients.cancelLooperWorkWithCoolDown.set(false);
            } else {
                Clients.cancelLooperWork.set(!Minecraft.getInstance().isWindowActive() || minecraft.isPaused() || minecraft.screen != null);
                Clients.cancelLooperWorkWithCoolDown.set(player == null || player.isSpectator() || player.isSwimming() || player.isInLava());
            }
            AnimationHandler.INSTANCE.onClientTick();
            Clients.LOCK.lock();
        } catch (Exception ignored) {}
        if (!Clients.clientRegistriesHandled) {
            BuiltInRegistries.ITEM.entrySet().forEach(entry -> {
                if (entry.getValue() instanceof AutoRegister autoRegister) {
                    autoRegister.clientRegister(entry);
                }
            });
            Clients.clientRegistriesHandled = true;
        }
    }

    @SubscribeEvent
    public static void onTickPost(LevelTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (Clients.LOCK.isLocked()) {
            Clients.LOCK.unlock();
        }
        Clients.lastClientTick = System.currentTimeMillis();
        Clients.equipDelayCoolDown();
        if (timeOffsetSyncTick == 20 && player != null) {
            PlayerStatusProvider.updateLocalTimeOffset(player);
            timeOffsetSyncTick = 0;
        }
        timeOffsetSyncTick++;
    }

}
