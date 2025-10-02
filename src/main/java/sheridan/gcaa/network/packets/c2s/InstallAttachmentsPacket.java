package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.attachmentSys.common.AttachmentsRegister;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.client.screens.containers.GunModifyMenu;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.packets.s2c.UpdateGunModifyScreenGuiContextPacket;

public record InstallAttachmentsPacket(String attachmentName, String slotName, String modelSlotName, String parentUuid, String replaceableGunPartUuid, int itemSlotIndex, byte direction) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<InstallAttachmentsPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "install_attachments"));

    public static final StreamCodec<ByteBuf, InstallAttachmentsPacket> CODEC = StreamCodec.of(
        (buf, packet) -> {
            ByteBufCodecs.STRING_UTF8.encode(buf, packet.attachmentName);
            ByteBufCodecs.STRING_UTF8.encode(buf, packet.slotName);
            ByteBufCodecs.STRING_UTF8.encode(buf, packet.modelSlotName);
            ByteBufCodecs.STRING_UTF8.encode(buf, packet.parentUuid);
            ByteBufCodecs.STRING_UTF8.encode(buf, packet.replaceableGunPartUuid);
            ByteBufCodecs.VAR_INT.encode(buf, packet.itemSlotIndex);
            ByteBufCodecs.BYTE.encode(buf, packet.direction);
        },
        buf -> new InstallAttachmentsPacket(
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.STRING_UTF8.decode(buf),
            ByteBufCodecs.VAR_INT.decode(buf),
            ByteBufCodecs.BYTE.decode(buf)
        )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(InstallAttachmentsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            String attachmentName = packet.attachmentName;
            IAttachment attachment = AttachmentsRegister.get(attachmentName);
            if (attachment != null) {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() instanceof IGun gun) {
                        AttachmentsHandler.INSTANCE.serverSetAttachment(player, heldItem, gun, attachment, packet.slotName,
                                packet.modelSlotName, packet.parentUuid, packet.direction, packet.replaceableGunPartUuid);
                        ListTag attachments = gun.getAttachmentsListTag(heldItem);
                        if (player.containerMenu instanceof GunModifyMenu menu) {
                            menu.slots.get(packet.itemSlotIndex).set(ItemStack.EMPTY);
                        }
                        PacketDistributor.sendToPlayer(player, new UpdateGunModifyScreenGuiContextPacket(attachments));
                    }
                }
            }
        });

    }
}
