package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.network.packets.s2c.UpdateGunModifyScreenGuiContextPacket;

public record UninstallAttachmentPacket(String uuid, String replaceableGunPartUuid) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UninstallAttachmentPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "uninstall_attachment"));

    public static final StreamCodec<ByteBuf, UninstallAttachmentPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, UninstallAttachmentPacket::uuid,
        ByteBufCodecs.STRING_UTF8, UninstallAttachmentPacket::replaceableGunPartUuid,
        UninstallAttachmentPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UninstallAttachmentPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() instanceof IGun gun) {
                        ItemStack stackToReturn = AttachmentsHandler.INSTANCE.serverUninstallAttachment(player, heldItem, gun, packet.uuid, packet.replaceableGunPartUuid);
                        if (stackToReturn != null) {
                            if (!player.addItem(stackToReturn)) {
                                ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stackToReturn);
                                player.level().addFreshEntity(entity);
                            }
                        }
                        ListTag attachments = gun.getAttachmentsListTag(heldItem);
                        PacketDistributor.sendToPlayer(player, new UpdateGunModifyScreenGuiContextPacket(attachments));
                    }
                }
            });

    }
}
