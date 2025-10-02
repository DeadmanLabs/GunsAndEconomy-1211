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
import sheridan.gcaa.client.screens.containers.GunModifyMenu;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.gun.IGun;

public record ScreenBindAmmunitionPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ScreenBindAmmunitionPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "screen_bind_ammunition"));

    public static final StreamCodec<ByteBuf, ScreenBindAmmunitionPacket> CODEC = StreamCodec.unit(new ScreenBindAmmunitionPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ScreenBindAmmunitionPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    if (player.containerMenu instanceof GunModifyMenu menu) {
                        ItemStack itemStack = menu.ammoSelector.getItem(0);
                        ItemStack gunStack = player.getMainHandItem();
                        if (itemStack.getItem() instanceof IAmmunition ammunition && gunStack.getItem() instanceof IGun gun) {
                            if (ammunition == gun.getGunProperties().caliber.ammunition) {
                                gun.bindAmmunition(gunStack, itemStack, ammunition);
                            }
                        }
                    }
                }
            });

    }
}
