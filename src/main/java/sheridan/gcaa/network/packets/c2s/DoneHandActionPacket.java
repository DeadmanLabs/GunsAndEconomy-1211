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
import sheridan.gcaa.items.gun.HandActionGun;

public record DoneHandActionPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DoneHandActionPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "done_hand_action"));

    public static final StreamCodec<ByteBuf, DoneHandActionPacket> CODEC = StreamCodec.unit(new DoneHandActionPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DoneHandActionPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() instanceof HandActionGun gun) {
                        gun.setNeedHandAction(heldItem, gun.getAmmoLeft(heldItem) == 0);
                    }
                }
            });

    }
}
