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
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.IGun;

public record GrenadeLauncherReloadPacket(String attachmentId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GrenadeLauncherReloadPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "grenade_launcher_reload"));

    public static final StreamCodec<ByteBuf, GrenadeLauncherReloadPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, GrenadeLauncherReloadPacket::attachmentId,
        GrenadeLauncherReloadPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GrenadeLauncherReloadPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() instanceof IGun gun) {
                        GrenadeLauncher.reload(packet.attachmentId, heldItem, gun, player);
                    }
                }
            });

    }
}
