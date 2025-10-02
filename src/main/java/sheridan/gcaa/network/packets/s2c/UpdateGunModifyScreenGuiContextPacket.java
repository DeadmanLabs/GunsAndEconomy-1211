package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.Clients;

public record UpdateGunModifyScreenGuiContextPacket(ListTag attachments) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateGunModifyScreenGuiContextPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "update_gun_modify_screen_gui_context"));

    public static final StreamCodec<ByteBuf, UpdateGunModifyScreenGuiContextPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.TAG.map(tag -> (ListTag) tag, tag -> tag), UpdateGunModifyScreenGuiContextPacket::attachments,
        UpdateGunModifyScreenGuiContextPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateGunModifyScreenGuiContextPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Clients.updateGunModifyScreenGuiContext(packet.attachments);
        });
    }
}
