package sheridan.gcaa.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;


import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import sheridan.gcaa.Clients;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.HandActionHandler;
import sheridan.gcaa.client.ReloadingHandler;
import sheridan.gcaa.client.SprintingHandler;
import sheridan.gcaa.client.render.InertialBobbingHandler;
import sheridan.gcaa.items.attachments.Scope;
import sheridan.gcaa.items.gun.IGun;
import net.neoforged.neoforge.network.PacketDistributor;
import sheridan.gcaa.network.packets.c2s.RequestDataSyncPacket;

import java.util.Objects;

@EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerEvents {


    @SubscribeEvent
    public static void onClientPlayerTickPre(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player == null || !player.level().isClientSide || player != Minecraft.getInstance().player) {
            return;
        }
        Clients.clientPlayerId = player.getId();
        Clients.localTimeOffset = System.currentTimeMillis() - player.level().getGameTime() * 50;
        ItemStack stackMain = player.getMainHandItem();
        IGun gunMain = stackMain.getItem() instanceof IGun ? (IGun) stackMain.getItem() : null;
        boolean lastTickHoldingGun = Clients.MAIN_HAND_STATUS.holdingGun.get();
        Clients.MAIN_HAND_STATUS.holdingGun.set(gunMain != null);
        String itemIdentity = Clients.MAIN_HAND_STATUS.identity;
        if (gunMain != null) {
            Clients.MAIN_HAND_STATUS.fireDelay.set(gunMain.getFireDelay(stackMain));
            Clients.MAIN_HAND_STATUS.adsSpeed = Math.min(gunMain.getAdsSpeed(stackMain) * 0.05f, 0.25f);
            Clients.MAIN_HAND_STATUS.attachmentsStatus.checkAndUpdate(stackMain, gunMain, player);
            Clients.MAIN_HAND_STATUS.identity = gunMain.getIdentity(stackMain);
            Clients.MAIN_HAND_STATUS.weapon.set(stackMain);
            Clients.MAIN_HAND_STATUS.fireMode = gunMain.getFireMode(stackMain);
        } else {
            Clients.MAIN_HAND_STATUS.weapon.set(ItemStack.EMPTY);
            Clients.MAIN_HAND_STATUS.identity = "";
            Clients.MAIN_HAND_STATUS.fireMode = null;
        }
        if (!Objects.equals(itemIdentity, Clients.MAIN_HAND_STATUS.identity) && (lastTickHoldingGun || gunMain != null)) {
            if (gunMain != null) {
                gunMain.getGun().shouldCauseReequipAnimation(ItemStack.EMPTY, stackMain, true);
            }
            KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(0), false);
            KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(1), false);
            Clients.MAIN_HAND_STATUS.buttonDown.set(false);
        }
        Clients.MAIN_HAND_STATUS.updatePlayerSpread(stackMain, gunMain, player);
        Clients.MAIN_HAND_STATUS.updateChargeTick(stackMain, gunMain);
        Clients.MAIN_HAND_STATUS.handleAds(stackMain, gunMain, player);
        Clients.MAIN_HAND_STATUS.updateAmmunitionMods(stackMain, gunMain);
        if (Clients.MAIN_HAND_STATUS.attachmentsStatus.getEffectiveSight() instanceof Scope scope) {
            scope.handleMouseSensitivity();
        }
        SprintingHandler.INSTANCE.tick((LocalPlayer) player);
    }

    @SubscribeEvent
    public static void onClientPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player == null || !player.level().isClientSide || player != Minecraft.getInstance().player) {
            return;
        }
        InertialBobbingHandler jumpBobbingHandler = InertialBobbingHandler.getInstance();
        if (jumpBobbingHandler != null) {
            jumpBobbingHandler.handle((LocalPlayer) player);
        }
        ReloadingHandler.INSTANCE.tick(player);
        HandActionHandler.INSTANCE.tick(player);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void playerJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getId() == Clients.clientPlayerId) {
                Clients.MAIN_HAND_STATUS.spread += 0.5f;
            }
        }
    }

    @SubscribeEvent
    public static void clientJoinGame(ClientPlayerNetworkEvent.LoggingIn event) {
        PlayerStatusProvider.getStatus(event.getPlayer()).setLastShoot(1L);
        PlayerStatusProvider.updateLocalTimeOffset(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(ClientPlayerNetworkEvent.Clone event) {
        PacketDistributor.sendToServer(new RequestDataSyncPacket());
    }

}
