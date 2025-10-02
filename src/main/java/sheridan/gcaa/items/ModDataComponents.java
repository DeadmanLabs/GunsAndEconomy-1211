package sheridan.gcaa.items;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sheridan.gcaa.GCAA;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(GCAA.MODID);

    // Gun data component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> GUN_DATA = 
        DATA_COMPONENTS.registerComponentType("gun_data", builder -> builder
            .persistent(CompoundTag.CODEC)
            .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
        );

    // Ammunition data component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> AMMO_DATA = 
        DATA_COMPONENTS.registerComponentType("ammo_data", builder -> builder
            .persistent(CompoundTag.CODEC)
            .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
        );

    // Attachment ID component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ATTACHMENT_ID = 
        DATA_COMPONENTS.registerComponentType("attachment_id", builder -> builder
            .persistent(Codec.STRING)
            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
        );
}
