package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.entities.industrial.BulletCraftingBlockEntity;
import sheridan.gcaa.items.ammunition.Ammunition;

public record SelectBulletCraftingPacket(String id, int x, int y, int z) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SelectBulletCraftingPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "select_bullet_crafting"));

    public static final StreamCodec<ByteBuf, SelectBulletCraftingPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, SelectBulletCraftingPacket::id,
        ByteBufCodecs.INT, SelectBulletCraftingPacket::x,
        ByteBufCodecs.INT, SelectBulletCraftingPacket::y,
        ByteBufCodecs.INT, SelectBulletCraftingPacket::z,
        SelectBulletCraftingPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SelectBulletCraftingPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    BlockPos blockPos = new BlockPos(packet.x, packet.y, packet.z);
                    BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
                    Item value = BuiltInRegistries.ITEM.get(ResourceLocation.parse(packet.id));
                    if (value instanceof Ammunition ammunition && blockEntity instanceof BulletCraftingBlockEntity block) {
                        block.setCraftingBullet(ammunition);
                    }
                }
            });

    }
}
