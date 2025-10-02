package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.entities.industrial.BulletCraftingBlockEntity;

public record StopBulletCraftingPacket(int x, int y, int z) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StopBulletCraftingPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "stop_bullet_crafting"));

    public static final StreamCodec<ByteBuf, StopBulletCraftingPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, StopBulletCraftingPacket::x,
        ByteBufCodecs.INT, StopBulletCraftingPacket::y,
        ByteBufCodecs.INT, StopBulletCraftingPacket::z,
        StopBulletCraftingPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StopBulletCraftingPacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    BlockPos blockPos = new BlockPos(packet.x, packet.y, packet.z);
                    BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
                    if (blockEntity instanceof BulletCraftingBlockEntity block) {
                        block.stopBulletCrafting();
                    }
                }
            });

    }
}
