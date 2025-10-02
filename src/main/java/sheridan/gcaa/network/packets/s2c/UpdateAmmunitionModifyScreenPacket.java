package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;

public record UpdateAmmunitionModifyScreenPacket(String modsUUID, int maxModCapability, CompoundTag modsTag, long balance) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateAmmunitionModifyScreenPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "update_ammunition_modify_screen"));

    public static final StreamCodec<ByteBuf, UpdateAmmunitionModifyScreenPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, UpdateAmmunitionModifyScreenPacket::modsUUID,
        ByteBufCodecs.VAR_INT, UpdateAmmunitionModifyScreenPacket::maxModCapability,
        ByteBufCodecs.COMPOUND_TAG, UpdateAmmunitionModifyScreenPacket::modsTag,
        ByteBufCodecs.VAR_LONG, UpdateAmmunitionModifyScreenPacket::balance,
        UpdateAmmunitionModifyScreenPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateAmmunitionModifyScreenPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Clients.updateAmmunitionModifyScreen(packet.modsUUID, packet.maxModCapability, packet.modsTag, packet.balance);
        });
    }
}
