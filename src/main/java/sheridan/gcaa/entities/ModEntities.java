package sheridan.gcaa.entities;

import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import java.util.function.Supplier;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.blocks.ModBlocks;
import sheridan.gcaa.entities.industrial.BulletCraftingBlockEntity;
import sheridan.gcaa.entities.projectiles.Grenade;

import java.util.Objects;
import java.util.function.BiFunction;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES;
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;
    public static final Supplier<EntityType<Grenade>> GRENADE;
    /** 子弹制造台 */
    public static final Supplier<BlockEntityType<BulletCraftingBlockEntity>> BULLET_CRAFTING;
    static {
        ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, GCAA.MODID);
        BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GCAA.MODID);
        GRENADE = registerProjectile("grenade", Grenade::new, 3, 8, 0.5f, 0.5f);
        BULLET_CRAFTING = BLOCK_ENTITIES.register("bullet_crafting", () ->
                BlockEntityType.Builder.of(BulletCraftingBlockEntity::new,
                        ModBlocks.BULLET_CRAFTING.get()).build(null));
    }

    private static <T extends Entity> Supplier<EntityType<T>> registerProjectile(String id, BiFunction<EntityType<T>, Level, T> function, int updateInterval, int clientTrackingRange, float sizeX, float sizeY) {
        return ENTITIES.register(id, () -> {
            Objects.requireNonNull(function);
            return EntityType.Builder.of(function::apply, MobCategory.MISC).sized(sizeX, sizeY).updateInterval(updateInterval).clientTrackingRange(clientTrackingRange).fireImmune().build(id);
        });
    }
    public ModEntities() {}
}
