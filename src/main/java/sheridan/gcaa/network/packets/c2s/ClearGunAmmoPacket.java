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

public record ClearGunAmmoPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClearGunAmmoPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "clear_gun_ammo"));

    public static final StreamCodec<ByteBuf, ClearGunAmmoPacket> CODEC = StreamCodec.unit(new ClearGunAmmoPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClearGunAmmoPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack stack = player.getMainHandItem();
                    if (stack.getItem() instanceof IGun gun) {
                        gun.clearAmmo(stack, player);
                    }
                }
            });

    }
}
