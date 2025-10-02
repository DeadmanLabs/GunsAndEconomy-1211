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
import sheridan.gcaa.items.attachments.grip.Flashlight;
import sheridan.gcaa.items.gun.IGun;

public record TurnFlashlightPacket(boolean turnOn) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TurnFlashlightPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "turn_flashlight"));

    public static final StreamCodec<ByteBuf, TurnFlashlightPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, TurnFlashlightPacket::turnOn,
        TurnFlashlightPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TurnFlashlightPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack stack = player.getMainHandItem();
                    if (stack.getItem() instanceof IGun gun) {
                        Flashlight.switchFlashlightMode(stack, gun);
                    }
                }
            });

    }
}
