package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.IGun;

public record SetScopeMagnificationPacket(String attachmentId, float magnification) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetScopeMagnificationPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "set_scope_magnification"));

    public static final StreamCodec<ByteBuf, SetScopeMagnificationPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, SetScopeMagnificationPacket::attachmentId,
        ByteBufCodecs.FLOAT, SetScopeMagnificationPacket::magnification,
        SetScopeMagnificationPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetScopeMagnificationPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() instanceof IGun gun) {
                        gun.getGun().setMagnificationsRateFor(packet.attachmentId, heldItem, packet.magnification);
                    }
                }
            });

    }
}
