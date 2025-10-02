package sheridan.gcaa.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.blocks.industrial.BulletCrafting;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GCAA.MODID);

    public static final Supplier<Block> AMMUNITION_PROCESSOR = BLOCKS.register(
            "ammunition_processor", () -> new AmmunitionProcessor(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .sound(SoundType.METAL)
                            .strength(1.0f)
                            .explosionResistance(180)
                            .noOcclusion()
                            .lightLevel(value -> 10)
            ));

    public static final Supplier<Block> VENDING_MACHINE = BLOCKS.register(
            "vending_machine", () -> new VendingMachine(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .sound(SoundType.METAL)
                            .strength(1.0f)
                            .explosionResistance(180)
                            .pushReaction(PushReaction.DESTROY)
            ));
    /** 弹药制造台注册 */
    public static final Supplier<Block> BULLET_CRAFTING = BLOCKS.register(
            "bullet_crafting_table", () -> new BulletCrafting(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .sound(SoundType.METAL)
                            .strength(1.0f)
                            .explosionResistance(180)
                            .lightLevel(value -> 10)
            )
    );
    public static final Supplier<Block> ORE_LEAD = BLOCKS.register(
            "ore_lead", () -> new Block(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE))
    );
    public static final Supplier<Block> LEAD_BLOCK = BLOCKS.register(
            "lead_block", () -> new Block(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
            )
    );
    public static final Supplier<Block> ORE_ASPHALT = BLOCKS.register(
            "ore_asphalt", () -> new Block(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE)
            )
    );
    public static final Supplier<Block> AIR_LIGHT_BLOCK = BLOCKS.register(
            "air_light_block", () -> new AirLightBlock(
                    BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().air().lightLevel((x) -> 10)
            )
    );
}
