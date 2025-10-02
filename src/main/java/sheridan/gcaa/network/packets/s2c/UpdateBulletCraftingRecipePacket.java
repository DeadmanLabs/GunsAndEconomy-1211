package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.industrial.RecipeRegister;

public record UpdateBulletCraftingRecipePacket(String string) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateBulletCraftingRecipePacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "update_bullet_crafting_recipe"));

    public static final StreamCodec<ByteBuf, UpdateBulletCraftingRecipePacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, UpdateBulletCraftingRecipePacket::string,
        UpdateBulletCraftingRecipePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateBulletCraftingRecipePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            RecipeRegister.syncAmmunitionRecipeFromServer(packet.string);
        });
    }
}
