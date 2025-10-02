package sheridan.gcaa.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.packets.c2s.*;
import sheridan.gcaa.network.packets.s2c.*;

public class PacketRegistry {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(GCAA.MODID)
                .versioned("1.0")
                .optional();

        // Client to Server packets
        registrar.playToServer(
                SyncPlayerStatusPacket.TYPE,
                SyncPlayerStatusPacket.CODEC,
                SyncPlayerStatusPacket::handle
        );

        registrar.playToServer(
                RequestDataSyncPacket.TYPE,
                RequestDataSyncPacket.CODEC,
                RequestDataSyncPacket::handle
        );

        registrar.playToServer(
                GunFirePacket.TYPE,
                GunFirePacket.CODEC,
                GunFirePacket::handle
        );

        registrar.playToServer(
                GunReloadPacket.TYPE,
                GunReloadPacket.CODEC,
                GunReloadPacket::handle
        );

        registrar.playToServer(
                SwitchFireModePacket.TYPE,
                SwitchFireModePacket.CODEC,
                SwitchFireModePacket::handle
        );

        registrar.playToServer(
                OpenGunModifyScreenPacket.TYPE,
                OpenGunModifyScreenPacket.CODEC,
                OpenGunModifyScreenPacket::handle
        );

        registrar.playToServer(
                InstallAttachmentsPacket.TYPE,
                InstallAttachmentsPacket.CODEC,
                InstallAttachmentsPacket::handle
        );

        registrar.playToServer(
                UninstallAttachmentPacket.TYPE,
                UninstallAttachmentPacket.CODEC,
                UninstallAttachmentPacket::handle
        );

        registrar.playToServer(
                SetEffectiveSightPacket.TYPE,
                SetEffectiveSightPacket.CODEC,
                SetEffectiveSightPacket::handle
        );

        registrar.playToServer(
                PlayerSoundPacket.TYPE,
                PlayerSoundPacket.CODEC,
                PlayerSoundPacket::handle
        );

        registrar.playToServer(
                DoneHandActionPacket.TYPE,
                DoneHandActionPacket.CODEC,
                DoneHandActionPacket::handle
        );

        registrar.playToServer(
                SetScopeMagnificationPacket.TYPE,
                SetScopeMagnificationPacket.CODEC,
                SetScopeMagnificationPacket::handle
        );

        registrar.playToServer(
                FireGrenadeLauncherPacket.TYPE,
                FireGrenadeLauncherPacket.CODEC,
                FireGrenadeLauncherPacket::handle
        );

        registrar.playToServer(
                GrenadeLauncherReloadPacket.TYPE,
                GrenadeLauncherReloadPacket.CODEC,
                GrenadeLauncherReloadPacket::handle
        );

        registrar.playToServer(
                TurnFlashlightPacket.TYPE,
                TurnFlashlightPacket.CODEC,
                TurnFlashlightPacket::handle
        );

        registrar.playToServer(
                AmmunitionManagePacket.TYPE,
                AmmunitionManagePacket.CODEC,
                AmmunitionManagePacket::handle
        );

        registrar.playToServer(
                ApplyAmmunitionModifyPacket.TYPE,
                ApplyAmmunitionModifyPacket.CODEC,
                ApplyAmmunitionModifyPacket::handle
        );

        registrar.playToServer(
                ScreenBindAmmunitionPacket.TYPE,
                ScreenBindAmmunitionPacket.CODEC,
                ScreenBindAmmunitionPacket::handle
        );

        registrar.playToServer(
                ClearGunAmmoPacket.TYPE,
                ClearGunAmmoPacket.CODEC,
                ClearGunAmmoPacket::handle
        );

        registrar.playToServer(
                ExchangePacket.TYPE,
                ExchangePacket.CODEC,
                ExchangePacket::handle
        );

        registrar.playToServer(
                BuyProductPacket.TYPE,
                BuyProductPacket.CODEC,
                BuyProductPacket::handle
        );

        registrar.playToServer(
                RecycleItemPacket.TYPE,
                RecycleItemPacket.CODEC,
                RecycleItemPacket::handle
        );

        registrar.playToServer(
                TransactionTerminalRequestPacket.TYPE,
                TransactionTerminalRequestPacket.CODEC,
                TransactionTerminalRequestPacket::handle
        );

        registrar.playToServer(
                TransferAccountsPacket.TYPE,
                TransferAccountsPacket.CODEC,
                TransferAccountsPacket::handle
        );

        registrar.playToServer(
                SelectBulletCraftingPacket.TYPE,
                SelectBulletCraftingPacket.CODEC,
                SelectBulletCraftingPacket::handle
        );

        registrar.playToServer(
                StopBulletCraftingPacket.TYPE,
                StopBulletCraftingPacket.CODEC,
                StopBulletCraftingPacket::handle
        );

        // Server to Client packets
        registrar.playToClient(
                BroadcastPlayerStatusPacket.TYPE,
                BroadcastPlayerStatusPacket.CODEC,
                BroadcastPlayerStatusPacket::handle
        );

        registrar.playToClient(
                UpdateGunModifyScreenGuiContextPacket.TYPE,
                UpdateGunModifyScreenGuiContextPacket.CODEC,
                UpdateGunModifyScreenGuiContextPacket::handle
        );

        registrar.playToClient(
                ClientSoundPacket.TYPE,
                ClientSoundPacket.CODEC,
                ClientSoundPacket::handle
        );

        registrar.playToClient(
                HeadShotFeedBackPacket.TYPE,
                HeadShotFeedBackPacket.CODEC,
                HeadShotFeedBackPacket::handle
        );

        registrar.playToClient(
                UpdateAmmunitionModifyScreenPacket.TYPE,
                UpdateAmmunitionModifyScreenPacket.CODEC,
                UpdateAmmunitionModifyScreenPacket::handle
        );

        registrar.playToClient(
                ClientHitBlockPacket.TYPE,
                ClientHitBlockPacket.CODEC,
                ClientHitBlockPacket::handle
        );

        registrar.playToClient(
                UpdateVendingMachineScreenPacket.TYPE,
                UpdateVendingMachineScreenPacket.CODEC,
                UpdateVendingMachineScreenPacket::handle
        );

        registrar.playToClient(
                UpdateTransactionDataPacket.TYPE,
                UpdateTransactionDataPacket.CODEC,
                UpdateTransactionDataPacket::handle
        );

        registrar.playToClient(
                UpdateTransferBalancePacket.TYPE,
                UpdateTransferBalancePacket.CODEC,
                UpdateTransferBalancePacket::handle
        );

        registrar.playToClient(
                UpdateGunPropertiesPacket.TYPE,
                UpdateGunPropertiesPacket.CODEC,
                UpdateGunPropertiesPacket::handle
        );

        registrar.playToClient(
                UpdateVendingMachineProductsPacket.TYPE,
                UpdateVendingMachineProductsPacket.CODEC,
                UpdateVendingMachineProductsPacket::handle
        );

        registrar.playToClient(
                UpdateBulletCraftingRecipePacket.TYPE,
                UpdateBulletCraftingRecipePacket.CODEC,
                UpdateBulletCraftingRecipePacket::handle
        );
    }
}
