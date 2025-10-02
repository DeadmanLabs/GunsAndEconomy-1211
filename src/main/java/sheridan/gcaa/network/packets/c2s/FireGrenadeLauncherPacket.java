package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.attachments.functional.GrenadeLauncher;
import sheridan.gcaa.items.gun.IGun;

public record FireGrenadeLauncherPacket(long lastFire, int itemId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FireGrenadeLauncherPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "fire_grenade_launcher"));

    public static final StreamCodec<ByteBuf, FireGrenadeLauncherPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, FireGrenadeLauncherPacket::lastFire,
        ByteBufCodecs.VAR_INT, FireGrenadeLauncherPacket::itemId,
        FireGrenadeLauncherPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FireGrenadeLauncherPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack heldItem = player.getMainHandItem();
                    if (heldItem.getItem() instanceof IGun gun) {
                        Item item = Item.byId(packet.itemId);
                        if (item instanceof GrenadeLauncher launcher) {
                            GrenadeLauncher.shoot(heldItem, gun, player, packet.lastFire, launcher);
                        }
                    }
                }
            });

    }
}
