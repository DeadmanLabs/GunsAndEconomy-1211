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
import sheridan.gcaa.client.screens.containers.providers.GunModifyMenuProvider;
import sheridan.gcaa.items.gun.IGun;

public record OpenGunModifyScreenPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenGunModifyScreenPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "open_gun_modify_screen"));

    public static final StreamCodec<ByteBuf, OpenGunModifyScreenPacket> CODEC = StreamCodec.unit(new OpenGunModifyScreenPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenGunModifyScreenPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() instanceof IGun) {
                        player.openMenu(new GunModifyMenuProvider());
                    }
                }
            });

    }
}
