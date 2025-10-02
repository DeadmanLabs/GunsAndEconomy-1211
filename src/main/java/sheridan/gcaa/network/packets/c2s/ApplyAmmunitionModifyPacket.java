package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.screens.containers.AmmunitionModifyMenu;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.network.packets.s2c.UpdateAmmunitionModifyScreenPacket;
import sheridan.gcaa.capability.PlayerStatusProvider;

import java.util.ArrayList;
import java.util.List;

public record ApplyAmmunitionModifyPacket(List<String> mods, long price) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ApplyAmmunitionModifyPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "apply_ammunition_modify"));

    public static final StreamCodec<ByteBuf, ApplyAmmunitionModifyPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8), ApplyAmmunitionModifyPacket::mods,
        ByteBufCodecs.VAR_LONG, ApplyAmmunitionModifyPacket::price,
        ApplyAmmunitionModifyPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ApplyAmmunitionModifyPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                if (packet.mods.size() > 0) {
                    long balance = PlayerStatusProvider.getStatus(player).getBalance();
                    if (player.containerMenu instanceof AmmunitionModifyMenu menu) {
                        ItemStack itemStack = menu.ammo.getItem(0);
                        if (itemStack.getItem() instanceof IAmmunition ammunition) {
                            long actualPrice = packet.price;
                            int ammoLeft = ammunition.getAmmoLeft(itemStack);
                            if (ammoLeft > 0) {
                                actualPrice = (long) (packet.price * ((double) ammoLeft / ammunition.get().getMaxDamage(itemStack)));
                            }
                            if (actualPrice <= balance) {
                                ammunition.addModsById(packet.mods, itemStack);
                                PlayerStatusProvider.getStatus(player).serverSetBalance(balance - actualPrice);
                            }
                            String modsUUID = ammunition.getModsUUID(itemStack);
                            int capacity = ammunition.getMaxModCapacity();
                            CompoundTag tag = ammunition.getModsTag(itemStack);
                            PacketDistributor.sendToPlayer(player, new UpdateAmmunitionModifyScreenPacket(
                                    modsUUID,
                                    capacity,
                                    tag,
                                    PlayerStatusProvider.getStatus(player).getBalance()
                            ));
                        }
                    }
                }
                }
            });

    }
}
