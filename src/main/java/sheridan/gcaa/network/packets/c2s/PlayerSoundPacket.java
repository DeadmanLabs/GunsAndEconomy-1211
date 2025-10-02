package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.sounds.ModSounds;

public record PlayerSoundPacket(String soundName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerSoundPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "player_sound"));

    public static final StreamCodec<ByteBuf, PlayerSoundPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, PlayerSoundPacket::soundName,
        PlayerSoundPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PlayerSoundPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ModSounds.sound(1, 1, player, ResourceLocation.parse(packet.soundName));
                }
            });

    }
}
