package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.IGun;

public record SwitchFireModePacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SwitchFireModePacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "switch_fire_mode"));

    public static final StreamCodec<ByteBuf, SwitchFireModePacket> CODEC = StreamCodec.unit(new SwitchFireModePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SwitchFireModePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.getMainHandItem().getItem() instanceof IGun gun) {
                    gun.switchFireMode(player.getMainHandItem());
                    // Force sync the item to client - ItemStack component changes need explicit broadcast
                    player.inventoryMenu.broadcastChanges();
                }
            }
        });
    }
}
