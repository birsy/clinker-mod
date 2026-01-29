package birsy.clinker.core.registry;

import birsy.clinker.common.world.block.*;
import birsy.clinker.common.world.block.plant.*;
import birsy.clinker.core.Clinker;
import birsy.clinker.mixin.common.BlockBehavior$PropertiesAccessor;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.WaterloggedVegetationPatchFeature;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ClinkerBlocks
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Clinker.MOD_ID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(Clinker.MOD_ID);
    
    // Blocks
    //Alchemy
    public static final DeferredBlock<Block> FERMENTATION_BARREL = createBlock("fermentation_barrel", FermentationBarrelBlock::new);
    public static final DeferredBlock<Block> COUNTER = createBlock("counter", () -> new CounterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(1.0f, 2.0f).sound(SoundType.WOOD)));
    
    public static final DeferredBlock<Block> STOVE = createBlock("stove", () -> new StoveControllerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).sound(SoundType.NETHER_BRICKS)));
    public static final DeferredBlock<Block> STOVE_DUMMY = createBlockNoItem("stove_dummy", () -> new StoveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).sound(SoundType.NETHER_BRICKS)));
    public static final DeferredBlock<Block> STOVE_CHIMNEY = createBlockNoItem("stove_chimney", () -> new StoveChimneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).sound(SoundType.NETHER_BRICKS).noOcclusion()));

    public static final DeferredBlock<Block> MORTAR = createBlock("mortar",
            () -> new MortarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BASALT).noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> SALTPETRE_LEACHED_DIRT = createBlock("saltpetre_leached_dirt",
            () -> new SaltpetreLeachedDirtBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).sound(SoundType.ROOTED_DIRT)));

    //Material Blocks
    public static final DeferredBlock<Block> LEAD_BLOCK = createBlock("lead_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5.0f, 6.0f).sound(SoundType.NETHERITE_BLOCK)));
    public static final DeferredBlock<Block> RAW_LEAD_BLOCK = createBlock("raw_lead_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.5f, 3.0f).sound(SoundType.ANCIENT_DEBRIS)));

    //Soils
    public static final DeferredBlock<Block> ASH = createBlock("ash", () -> new ColoredFallingBlock(new ColorRGBA(8616308), Block.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.5F).sound(SoundType.SNOW)));
    public static final DeferredBlock<Block> ASH_LAYER = createBlock("ash_layers", () -> new FallingLayerBlock(new ColorRGBA(8616308), BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW).mapColor(MapColor.COLOR_GRAY)));

    public static final DeferredBlock<Block> ASHEN_REGOLITH = createBlock("ashen_regolith", () -> new MudBlock(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.5F).sound(SoundType.NYLIUM)));

    public static final DeferredBlock<Block> MUD = createBlock("mud", () -> new SulfricMudBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(0.5F).sound(SoundType.WET_GRASS)));

    public static final DeferredBlock<Block> BARRIERROCK = createBlock("barrierrock", () -> new Block(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .strength(80.0F, 6.0F)
                    .sound(SoundType.POLISHED_DEEPSLATE)
            )
    );

    //Brimstone
    public static final DeferredBlock<Block> BRIMSTONE = createBlock("brimstone", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN)
            .strength(2.75F, 75.0F)
            .sound(SoundType.DRIPSTONE_BLOCK).requiresCorrectToolForDrops())
    );
    //new LeavesBlock(Properties.of().mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(soundType).noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never).isViewBlocking(Blocks::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never))

    //public static final DeferredBlock<Block> SALTBRUSH = createBlock("saltbrush", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));

    public static final DeferredBlock<Block> BRIMSTONE_SLAB = createBlock("brimstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));
    public static final DeferredBlock<Block> BRIMSTONE_STAIRS = createBlock("brimstone_stairs", () -> new StairBlock(BRIMSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));
    public static final DeferredBlock<Block> BRIMSTONE_WALL = createBlock("brimstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));

    public static final DeferredBlock<Block> BRIMSTONE_PILLAR = createBlock("brimstone_pillar", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));

    public static final DeferredBlock<Block> COBBLED_BRIMSTONE = createBlock("cobbled_brimstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get()).sound(SoundType.GILDED_BLACKSTONE)));
    public static final DeferredBlock<Block> COBBLED_BRIMSTONE_SLAB = createBlock("cobbled_brimstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get()).sound(SoundType.GILDED_BLACKSTONE)));
    public static final DeferredBlock<Block> COBBLED_BRIMSTONE_STAIRS = createBlock("cobbled_brimstone_stairs", () -> new StairBlock(COBBLED_BRIMSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get()).sound(SoundType.GILDED_BLACKSTONE)));
    public static final DeferredBlock<Block> COBBLED_BRIMSTONE_WALL = createBlock("cobbled_brimstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get()).sound(SoundType.GILDED_BLACKSTONE)));

    public static final DeferredBlock<Block> POLISHED_BRIMSTONE = createBlock("polished_brimstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));
    public static final DeferredBlock<Block> POLISHED_BRIMSTONE_SLAB = createBlock("polished_brimstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));
    public static final DeferredBlock<Block> POLISHED_BRIMSTONE_STAIRS = createBlock("polished_brimstone_stairs", () -> new StairBlock(POLISHED_BRIMSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));
    public static final DeferredBlock<Block> POLISHED_BRIMSTONE_WALL = createBlock("polished_brimstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));

    public static final DeferredBlock<Block> BRIMSTONE_BRICKS = createBlock("brimstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));
    public static final DeferredBlock<Block> BRIMSTONE_BRICK_SLAB = createBlock("brimstone_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE_BRICKS.get())));
    public static final DeferredBlock<Block> BRIMSTONE_BRICK_STAIRS = createBlock("brimstone_brick_stairs", () -> new StairBlock(BRIMSTONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BRIMSTONE_BRICKS.get())));
    public static final DeferredBlock<Block> BRIMSTONE_BRICK_WALL = createBlock("brimstone_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE_BRICKS.get())));

    public static final DeferredBlock<Block> CRACKED_BRIMSTONE_BRICKS = createBlock("cracked_brimstone_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE_BRICKS.get())));
    public static final DeferredBlock<Block> CRACKED_BRIMSTONE_BRICK_SLAB = createBlock("cracked_brimstone_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE_BRICKS.get())));
    public static final DeferredBlock<Block> CRACKED_BRIMSTONE_BRICK_STAIRS = createBlock("cracked_brimstone_brick_stairs", () -> new StairBlock(BRIMSTONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BRIMSTONE_BRICKS.get())));
    public static final DeferredBlock<Block> CRACKED_BRIMSTONE_BRICK_WALL = createBlock("cracked_brimstone_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE_BRICKS.get())));

    public static final DeferredBlock<Block> CHISELED_BRIMSTONE = createBlock("chiseled_brimstone", () -> new GlazedTerracottaBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));

    public static final DeferredBlock<Block> SMOOTH_BRIMSTONE = createBlock("smooth_brimstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));


    public static final DeferredBlock<Block> CALC = createBlock("calc", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY)
            .strength(1.5F, 1.0F)
            .sound(SoundType.NETHERRACK).requiresCorrectToolForDrops()));
    public static final DeferredBlock<SlabBlock> CALC_SLAB = createBlock("calc_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CALC.get())));
    public static final DeferredBlock<StairBlock> CALC_STAIRS = createBlock("calc_stairs", () -> new StairBlock(CALC.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CALC.get())));
    public static final DeferredBlock<WallBlock> CALC_WALL = createBlock("calc_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(CALC.get())));

    public static final DeferredBlock<Block> CALC_BRICKS = createBlock("calc_bricks", () -> new Block(BlockBehaviour.Properties.ofFullCopy(CALC.get()).sound(SoundType.TUFF_BRICKS)));
    public static final DeferredBlock<SlabBlock> CALC_BRICK_SLAB = createBlock("calc_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CALC_BRICKS.get())));
    public static final DeferredBlock<StairBlock> CALC_BRICK_STAIRS = createBlock("calc_brick_stairs", () -> new StairBlock(CALC.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CALC_BRICKS.get())));
    public static final DeferredBlock<WallBlock> CALC_BRICK_WALL = createBlock("calc_brick_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(CALC_BRICKS.get())));

    public static final DeferredBlock<Block> POLISHED_CALC = createBlock("polished_calc", () -> new Block(BlockBehaviour.Properties.ofFullCopy(CALC_BRICKS.get())));
    public static final DeferredBlock<SlabBlock> POLISHED_CALC_SLAB = createBlock("polished_calc_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_CALC.get())));
    public static final DeferredBlock<StairBlock> POLISHED_CALC_STAIRS = createBlock("polished_calc_stairs", () -> new StairBlock(CALC.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(POLISHED_CALC.get())));
    public static final DeferredBlock<WallBlock> POLISHED_CALC_WALL = createBlock("polished_calc_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_CALC.get())));

    public static final DeferredBlock<SaltmossBlock> SALTMOSS = createBlock("saltmoss", () -> new SaltmossBlock(BlockBehaviour.Properties.ofFullCopy(CALC.get()).sound(SoundType.NYLIUM).mapColor(MapColor.COLOR_RED).randomTicks()));

    public static final DeferredBlock<ColoredFallingBlock> SALT_GRAVEL = createBlock("salt_gravel",
            () -> new ColoredFallingBlock(new ColorRGBA(0x777472), BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).sound(SoundType.SOUL_SOIL))
    );

    public static final DeferredBlock<Block> WATER_FERN = createBlockNoItem("water_fern", () -> new WaterFernBlock(
            BlockBehaviour.Properties.of()
                    .instabreak().replaceable()
                    .mapColor(MapColor.PLANT).sound(SoundType.BIG_DRIPLEAF).pushReaction(PushReaction.DESTROY)
                    .noOcclusion().noCollission().noLootTable()));

    public static final DeferredItem<Item> WATER_FERN_ITEM = BLOCK_ITEMS.register("water_fern", () -> new PlaceOnWaterBlockItem(WATER_FERN.get(), new Item.Properties()));

    public static final DeferredBlock<SeaShellBlock> SEA_SHELL = createBlock("sea_shell", () -> new SeaShellBlock(
            BlockBehaviour.Properties.of()
                    .sound(SoundType.CALCITE)
                    .mapColor(MapColor.METAL)
                    .dynamicShape()
                    .instabreak()
                    .noCollission()
                    .noOcclusion()
                    .offsetType(BlockBehaviour.OffsetType.XZ))
    );


    //Calamine
    public static BlockBehaviour.Properties getCalamineProperties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
                .strength(1.5F, 3.0F)
                .sound(SoundType.CALCITE);
    }

    public static final DeferredBlock<Block> CALAMINE = createBlock("calamine", () -> new Block(getCalamineProperties()));
    public static final DeferredBlock<Block> POLISHED_CALAMINE = createBlock("polished_calamine", () -> new Block(getCalamineProperties()));
    public static final DeferredBlock<Block> CALAMINE_BRICKS = createBlock("calamine_bricks", () -> new Block(getCalamineProperties()));
    //Capstone
    public static BlockBehaviour.Properties getCapstoneProperties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
                .strength(1.5F, 3.0F)
                .sound(SoundType.NETHER_BRICKS);
    }

    public static final DeferredBlock<Block> CAPSTONE = createBlock("capstone", () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
            .strength(1.5F, 3.0F)
            .sound(SoundType.NETHER_BRICKS))
    );
    public static final DeferredBlock<Block> CAPSTONE_SLAB = createBlock("capstone_slab", () -> new SlabBlock(getCapstoneProperties()));
    public static final DeferredBlock<Block> CAPSTONE_STAIRS = createBlock("capstone_stairs", () -> new StairBlock(CAPSTONE.get().defaultBlockState(), getCapstoneProperties()));
    public static final DeferredBlock<Block> CAPSTONE_WALL = createBlock("capstone_wall", () -> new WallBlock(getCapstoneProperties()));

    public static final DeferredBlock<Block> POLISHED_CAPSTONE = createBlock("polished_capstone", () -> new Block(getCapstoneProperties()));
    public static final DeferredBlock<Block> POLISHED_CAPSTONE_SLAB = createBlock("polished_capstone_slab", () -> new SlabBlock(getCapstoneProperties()));
    public static final DeferredBlock<Block> POLISHED_CAPSTONE_STAIRS = createBlock("polished_capstone_stairs", () -> new StairBlock(POLISHED_CAPSTONE.get().defaultBlockState(), getCapstoneProperties()));
    public static final DeferredBlock<Block> POLISHED_CAPSTONE_WALL = createBlock("polished_capstone_wall", () -> new WallBlock(getCapstoneProperties()));

    public static final DeferredBlock<Block> CAPSTONE_BRICKS = createBlock("capstone_bricks", () -> new Block(getCapstoneProperties()));
    public static final DeferredBlock<Block> CAPSTONE_BRICK_SLAB = createBlock("capstone_brick_slab", () -> new SlabBlock(getCapstoneProperties()));
    public static final DeferredBlock<Block> CAPSTONE_BRICK_STAIRS = createBlock("capstone_brick_stairs", () -> new StairBlock(CAPSTONE_BRICKS.get().defaultBlockState(), getCapstoneProperties()));
    public static final DeferredBlock<Block> CAPSTONE_BRICK_WALL = createBlock("capstone_brick_wall", () -> new WallBlock(getCapstoneProperties()));

    //Sulfur
    public static final DeferredBlock<Block> SULFUR_CRYSTAL_BLOCK = createBlock("sulfur_crystal_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
            .strength(1.5F, 6.0F)
            .sound(SoundType.AMETHYST_CLUSTER)));

    public static final DeferredBlock<Block> SULFUR_ROCK_BLOCK = createBlock("sulfur_rock_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
            .strength(1.5F, 6.0F)
            .sound(SoundType.GILDED_BLACKSTONE)));

    //Peat
    public static final DeferredBlock<Block> PEAT_MOSS = createBlock("peat_moss", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN)
            .strength(0.25F,1F)
            .sound(SoundType.BIG_DRIPLEAF)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
    ));

    //Shale
    public static BlockBehaviour.Properties getShaleProperties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_GRAY)
                  .strength(2.75F, 10.0F)
                  .sound(SoundType.ANCIENT_DEBRIS);
    }

    public static final DeferredBlock<Block> SHALE = createBlock("shale", () -> new RotatedPillarBlock(getShaleProperties()));
    public static final DeferredBlock<Block> SHALE_PILLAR = createBlock("shale_pillar", () -> new RotatedPillarBlock(getShaleProperties()));

    public static final DeferredBlock<Block> SMOOTH_SHALE = createBlock("smooth_shale", () -> new Block(getShaleProperties()));
    public static final DeferredBlock<Block> SMOOTH_SHALE_SLAB = createBlock("smooth_shale_slab", () -> new SlabBlock(getShaleProperties()));
    public static final DeferredBlock<Block> SMOOTH_SHALE_STAIRS = createBlock("smooth_shale_stairs", () -> new StairBlock(SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()));
    public static final DeferredBlock<Block> SMOOTH_SHALE_WALL = createBlock("smooth_shale_wall", () -> new WallBlock(getShaleProperties()));

    public static final DeferredBlock<Block> POLISHED_SHALE = createBlock("polished_shale", () -> new Block(getShaleProperties()));
    public static final DeferredBlock<Block> POLISHED_SHALE_SLAB = createBlock("polished_shale_slab", () -> new SlabBlock(getShaleProperties()));
    public static final DeferredBlock<Block> POLISHED_SHALE_STAIRS = createBlock("polished_shale_stairs", () -> new StairBlock(SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()));
    public static final DeferredBlock<Block> POLISHED_SHALE_WALL = createBlock("polished_shale_wall", () -> new WallBlock(getShaleProperties()));

    public static final DeferredBlock<Block> SHALE_BRICKS = createBlock("shale_bricks", () -> new Block(getShaleProperties()));
    public static final DeferredBlock<Block> SHALE_BRICKS_SLAB = createBlock("shale_bricks_slab", () -> new SlabBlock(getShaleProperties()));
    public static final DeferredBlock<Block> SHALE_BRICKS_STAIRS = createBlock("shale_bricks_stairs", () -> new StairBlock(SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()));
    public static final DeferredBlock<Block> SHALE_BRICKS_WALL = createBlock("shale_bricks_wall", () -> new WallBlock(getShaleProperties()));

    public static final DeferredBlock<Block> SMALL_SHALE_BRICKS = createBlock("small_shale_bricks", () -> new Block(getShaleProperties()));
    public static final DeferredBlock<Block> SMALL_SHALE_BRICKS_SLAB = createBlock("small_shale_bricks_slab", () -> new SlabBlock(getShaleProperties()));
    public static final DeferredBlock<Block> SMALL_SHALE_BRICKS_STAIRS = createBlock("small_shale_bricks_stairs", () -> new StairBlock(SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()));
    public static final DeferredBlock<Block> SMALL_SHALE_BRICKS_FENCE = createBlock("small_shale_bricks_fence", () -> new FenceBlock(getShaleProperties()));


//    Ancient Bricks
//    redo this at some point. sorry ender
//    public static final DeferredBlock<Block> ANCIENT_BRICKS = createBlock("ancient_bricks", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).requiresCorrectToolForDrops().strength(25.0F, 1200.0F)));
//    public static final DeferredBlock<Block> ANCIENT_BRICK_SLAB = createBlock("ancient_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(ANCIENT_BRICKS.get())));
//    public static final DeferredBlock<Block> ANCIENT_BRICK_STAIRS = createBlock("ancient_brick_stairs", () -> new StairBlock(ClinkerBlocks.ANCIENT_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(ANCIENT_BRICKS.get())));
//    public static final DeferredBlock<Block> ANCIENT_SMOOTH_BRICK = createBlock("ancient_smooth_brick", () -> new Block(BlockBehaviour.Properties.ofFullCopy(ANCIENT_BRICKS.get())));
//    public static final DeferredBlock<Block> ANCIENT_STONE = createBlock("ancient_stone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(ANCIENT_BRICKS.get())));
//    public static final DeferredBlock<Block> ANCIENT_BRICK_FLAT = createBlock("ancient_brick_flat", AncientBrickFlatBlock::new);
//    public static final DeferredBlock<Block> ANCIENT_RUNE = createBlock("ancient_rune", AncientBrickRunesBlock::new);


    //Metal Ores
    public static final DeferredBlock<Block> LEAD_ORE = createBlock("lead_ore", () -> new Block(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get())));
    
    
    //Wood Types
    public static BlockBehaviour.Properties getOthershoreWoodProperties(MapColor colorIn) {
        return BlockBehaviour.Properties.of().mapColor(colorIn)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD);
    }
    
    public static final DeferredBlock<Block> LOCUST_LOG = createBlock("locust_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.TERRACOTTA_GREEN : MapColor.STONE).strength(2.0F).sound(SoundType.STEM)));
    public static final DeferredBlock<Block> TRIMMED_LOCUST_LOG = createBlock("trimmed_locust_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.TERRACOTTA_GREEN : MapColor.STONE).strength(2.0F).sound(SoundType.STEM)));
    public static final DeferredBlock<Block> STRIPPED_LOCUST_LOG = createBlock("stripped_locust_log", 
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.TERRACOTTA_GREEN : MapColor.STONE).strength(2.0F).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> LOCUST_PLANKS = createBlock("locust_planks", () -> new Block(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
    public static final DeferredBlock<Block> LOCUST_STAIRS = createBlock("locust_stairs", () -> new StairBlock(ClinkerBlocks.LOCUST_PLANKS.get().defaultBlockState(), getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
    public static final DeferredBlock<Block> LOCUST_SLAB = createBlock("locust_slab", () -> new SlabBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
    
    //public static final DeferredBlock<Block> LOCUST_FENCE = createBlock("locust_fence", () -> new FenceBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
    //public static final DeferredBlock<Block> LOCUST_FENCE_GATE = createBlock("locust_fence_gate", () -> new FenceGateBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
    //public static final DeferredBlock<Block> LOCUST_DOOR = createBlock("locust_door", () -> new DoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.CRIMSON));
    //public static final DeferredBlock<Block> LOCUST_TRAPDOOR = createBlock("locust_trapdoor", () -> new TrapDoorBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
    //public static final DeferredBlock<Block> LOCUST_BUTTON = createBlock("locust_button", () -> new ButtonBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
    //public static final DeferredBlock<Block> LOCUST_PRESSURE_PLATE = createBlock("locust_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));

    // dismal aspen
    public static final BlockSetType DISMAL_ASPEN_BLOCK_SET_TYPE = BlockSetType.register(new BlockSetType("dismal_aspen"));
    public static final WoodType DISMAL_ASPEN_WOOD_TYPE = WoodType.register(
            new WoodType(
                "dismal_aspen",
                DISMAL_ASPEN_BLOCK_SET_TYPE,
                SoundType.NETHER_WOOD,
                SoundType.NETHER_WOOD_HANGING_SIGN,
                SoundEvents.NETHER_WOOD_FENCE_GATE_CLOSE,
                SoundEvents.NETHER_WOOD_FENCE_GATE_OPEN
            )
    );
    public static final DeferredBlock<Block> DISMAL_ASPEN_HEART = createBlock("dismal_aspen_heart", () -> new DismalAspenHeartBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_STEM).mapColor(MapColor.TERRACOTTA_GRAY).randomTicks())
    );
    public static final DeferredBlock<ThinLogBlock> DISMAL_ASPEN_LOG = createBlock("dismal_aspen_log", () -> new ThinLogBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_STEM).mapColor(MapColor.COLOR_LIGHT_GRAY), 4)
    );
    public static final DeferredBlock<RotatedPillarBlock> BUNDLED_DISMAL_ASPEN_LOGS = createBlock("bundled_dismal_aspen_logs", () -> new RotatedPillarBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_STEM).mapColor(MapColor.COLOR_LIGHT_GRAY))
    );
    public static final DeferredBlock<Block> DISMAL_ASPEN_PLANKS = createBlock("dismal_aspen_planks", () -> new Block(
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_PLANKS).mapColor(MapColor.COLOR_LIGHT_GRAY))
    );
    public static final DeferredBlock<StairBlock> DISMAL_ASPEN_STAIRS = createBlock("dismal_aspen_stairs", () -> new StairBlock(
            DISMAL_ASPEN_PLANKS.get().defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_STAIRS).mapColor(MapColor.COLOR_LIGHT_GRAY))
    );
    public static final DeferredBlock<SlabBlock> DISMAL_ASPEN_SLAB = createBlock("dismal_aspen_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_SLAB).mapColor(MapColor.COLOR_LIGHT_GRAY))
    );
    public static final DeferredBlock<DoorBlock> DISMAL_ASPEN_DOOR = createBlock("dismal_aspen_door", () -> new DoorBlock(
            DISMAL_ASPEN_BLOCK_SET_TYPE,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_DOOR).mapColor(MapColor.COLOR_LIGHT_GRAY))
    );
    public static final DeferredBlock<TrapDoorBlock> DISMAL_ASPEN_TRAPDOOR = createBlock("dismal_aspen_trapdoor", () -> new TrapDoorBlock(
            DISMAL_ASPEN_BLOCK_SET_TYPE,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_TRAPDOOR).mapColor(MapColor.COLOR_LIGHT_GRAY))
    );
    public static final DeferredBlock<FenceBlock> DISMAL_ASPEN_FENCE = createBlock("dismal_aspen_fence", () -> new FenceBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_FENCE).mapColor(MapColor.COLOR_LIGHT_GRAY))
    );
    public static final DeferredBlock<FenceGateBlock> DISMAL_ASPEN_FENCE_GATE = createBlock("dismal_aspen_fence_gate", () -> new FenceGateBlock(
            DISMAL_ASPEN_WOOD_TYPE,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_FENCE_GATE).mapColor(MapColor.COLOR_LIGHT_GRAY))
    );
    public static final DeferredBlock<PressurePlateBlock> DISMAL_ASPEN_PRESSURE_PLATE = createBlock("dismal_aspen_pressure_plate", () -> new PressurePlateBlock(
            DISMAL_ASPEN_BLOCK_SET_TYPE,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_PRESSURE_PLATE).mapColor(MapColor.COLOR_LIGHT_GRAY))
    );
    public static final DeferredBlock<ButtonBlock> DISMAL_ASPEN_BUTTON = createBlock("dismal_aspen_button", () -> new ButtonBlock(
            DISMAL_ASPEN_BLOCK_SET_TYPE,
            30,
            BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_BUTTON))
    );


    //Plants
    public static final DeferredBlock<Block> TALL_MUD_REEDS = createBlock("tall_mud_reeds", () -> new DoubleMudReedsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS).mapColor(MapColor.COLOR_BROWN).sound(SoundType.HANGING_ROOTS)));
    public static final DeferredBlock<Block> SHORT_MUD_REEDS = createBlock("short_mud_reeds", () -> new MudReedsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS).mapColor(MapColor.COLOR_BROWN).sound(SoundType.HANGING_ROOTS)));
    public static final DeferredBlock<Block> MUD_REEDS = createBlock("mud_reeds", () -> new MudReedsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS).mapColor(MapColor.COLOR_BROWN).sound(SoundType.HANGING_ROOTS)));

    public static final DeferredBlock<Block> CAVE_FIG_STEM = createBlock("cave_fig_stem", () -> new HugeMushroomBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.CALCITE)));
    public static final DeferredBlock<Block> CAVE_FIG_ROOTS = createBlock("cave_fig_roots", () -> new CaveFigRootsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).noOcclusion().sound(SoundType.CALCITE)));

    public static final DeferredBlock<Block> FAIRY_FRUIT_BLOCK = createBlockNoItem("fairy_fruit_block", () -> new FairyFruitBlock(BlockBehaviour.Properties.of().noCollission().instabreak().noOcclusion().sound(SoundType.HANGING_ROOTS).offsetType(BlockBehaviour.OffsetType.XZ).lightLevel((state) -> 10)));

    public static final DeferredBlock<Block> DRIED_CLOVERS = createBlock("dried_clovers", () -> new DriedCloversBlock(BlockBehaviour.Properties.of().noOcclusion().mapColor(MapColor.COLOR_ORANGE).ignitedByLava().strength(0.1F).sound(SoundType.HANGING_ROOTS).replaceable().pushReaction(PushReaction.DESTROY).noCollission()));

    public static final DeferredBlock<Block> FULMINA_FLOWER = createBlock("fulmina_flower",
            () -> new FulminaFlowerBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .mapColor(MapColor.COLOR_GRAY)
                    .instabreak()
                    .sound(SoundType.HANGING_ROOTS)
                    .pushReaction(PushReaction.DESTROY)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .dynamicShape()));

    public static final DeferredBlock<Block> THORNY_STEM = createBlock("thorny_stem", () ->
            new ThornyStemBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .strength(4.0F)
                            .mapColor(MapColor.TERRACOTTA_BLACK)
                            .sound(SoundType.HANGING_ROOTS)
                            .speedFactor(0.5F)
                            .pushReaction(PushReaction.DESTROY)
            ));
    public static final DeferredBlock<Block> SALTY_STEM = createBlock("salty_stem", () ->
            new ThornyStemBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .strength(4.0F)
                            .mapColor(MapColor.TERRACOTTA_BLACK)
                            .sound(SoundType.HANGING_ROOTS)
                            .speedFactor(0.45F)
                            .pushReaction(PushReaction.DESTROY)
            ));

    public static final DeferredBlock<BrambleBlossomBlock> BRAMBLE_BLOSSOM = createBlock("bramble_blossom", () ->
            new BrambleBlossomBlock(
                    new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(MobEffects.SATURATION, Mth.floor(30 * 20.0F)))),
                    BlockBehaviour.Properties.ofFullCopy(ClinkerBlocks.THORNY_STEM.get())
                            .mapColor(MapColor.QUARTZ)
            ));

    public static final DeferredBlock<WitheringBrambleBlossomBlock> WITHERING_BRAMBLE_BLOSSOM = createBlock("withering_bramble_blossom", () ->
            new WitheringBrambleBlossomBlock(
                    BlockBehaviour.Properties.ofFullCopy(ClinkerBlocks.THORNY_STEM.get())
                            .mapColor(MapColor.COLOR_BLACK)
            ));

    public static final DeferredBlock<OthershorePlantBlock> SALTMOSS_SPROUTS = createBlock("saltmoss_sprouts", () -> new OthershorePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_SPROUTS).mapColor(MapColor.COLOR_RED).sound(SoundType.HANGING_ROOTS)));
    public static final DeferredBlock<OthershorePlantBlock> DRIED_SALTMOSS_SPROUTS = createBlock("dried_saltmoss_sprouts", () -> new OthershorePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_SPROUTS).mapColor(MapColor.COLOR_RED).sound(SoundType.HANGING_ROOTS)));
    public static final DeferredBlock<SaltmossBlossomBlock> SALTMOSS_BLOSSOM = createBlock("saltmoss_blossom", () -> new SaltmossBlossomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_SPROUTS).mapColor(MapColor.COLOR_RED).sound(SoundType.HANGING_ROOTS)));
    public static final DeferredBlock<OthershorePlantBlock> YARROW = createBlock("yarrow", () -> new SaltmossBlossomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_SPROUTS).mapColor(MapColor.COLOR_YELLOW).sound(SoundType.HANGING_ROOTS)));
    public static final DeferredBlock<OthershorePlantBlock> CAVE_SPROUTS = createBlock("cave_sprouts", () -> new OthershorePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_SPROUTS).mapColor(MapColor.COLOR_BROWN).sound(SoundType.AZALEA_LEAVES)));

    private static Supplier<BlockBehaviour.Properties> STROMATOLITE_PROPERTIES = () -> {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_HYPHAE)
                .mapColor(MapColor.COLOR_LIGHT_GRAY)
                .dynamicShape()
                .randomTicks();
        ((BlockBehavior$PropertiesAccessor) props).setOffsetFunction((state, level, pos) -> {
            long seed = Mth.getSeed(pos.getX(), 0, pos.getZ());
            int maxOffsetXZ = StromatoliteBlock.MAX_SIZE_INCLUSIVE - state.getValue(StromatoliteBlock.SIZE);
            double xOffset = (((seed >>  0 & 63L) / 64F) - 0.5) * 2 * (0.5F / 16.0F);
            double yOffset =  ((seed >>  8 & 63L) / 64F) * (-1.95F / 16.0F);
            double zOffset = (((seed >> 16 & 63L) / 64F) - 0.5) * 2 * (0.5F / 16.0F);
            return new Vec3(xOffset, yOffset, zOffset);
        });
        return props;
    };
    public static final DeferredBlock<StromatoliteBlock> STROMATOLITE = createBlock("stromatolite", () -> new StromatoliteBlock(STROMATOLITE_PROPERTIES.get()));

    private static Supplier<BlockBehaviour.Properties> SHEET_MOSS_PROPERTIES = () -> {
        BlockBehaviour.Properties props = BlockBehaviour.Properties.ofFullCopy(Blocks.HANGING_ROOTS)
                .mapColor(MapColor.COLOR_GRAY)
                .sound(SoundType.PINK_PETALS);
        ((BlockBehavior$PropertiesAccessor) props).setOffsetFunction((state, level, pos) -> {
            long seed = Mth.getSeed(pos.getX(), 0, pos.getZ());
            double xOffset = (((seed >>  0 & 63L) / 64F) - 0.5) * (6.0F / 16.0F);
            double yOffset =  ((seed >>  8 & 63L) / 64F) * (4.0F / 16.0F);
            double zOffset = (((seed >> 16 & 63L) / 64F) - 0.5) * (6.0F / 16.0F);
            return new Vec3(xOffset, yOffset, zOffset);
        });
        return props;
    };
    public static final DeferredBlock<SheetMossBlock> SHEET_MOSS = createBlock("sheet_moss", () -> new SheetMossBlock(SHEET_MOSS_PROPERTIES.get()));
    public static final DeferredBlock<DoubleSheetMossBlock> LONG_SHEET_MOSS = createBlock("long_sheet_moss", () -> new DoubleSheetMossBlock(SHEET_MOSS_PROPERTIES.get()));

    public static final DeferredBlock<OthershorePlantBlock> PEAT_MOSS_BUDS = createBlock("peat_moss_buds", () -> new OthershorePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_SPROUTS).mapColor(MapColor.COLOR_ORANGE).sound(SoundType.HANGING_ROOTS)));
    public static final DeferredBlock<OthershorePlantBlock> INDIGO_TORMENTIL = createBlock("indigo_tormentil", () -> new OthershorePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).mapColor(MapColor.COLOR_PURPLE).sound(SoundType.PINK_PETALS)));
    public static final DeferredBlock<OthershorePlantBlock> YELLOW_TORMENTIL = createBlock("yellow_tormentil", () -> new OthershorePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).mapColor(MapColor.COLOR_YELLOW).sound(SoundType.PINK_PETALS)));

    public static final DeferredBlock<SpotreedBlock> SPOTREED = createBlock("spotreed", () -> new SpotreedBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO).mapColor(MapColor.CRIMSON_STEM).sound(SoundType.SPONGE)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY)
            .offsetType(BlockBehaviour.OffsetType.XZ)
    ));

    // fluids
    public static DeferredBlock<LiquidBlock> VITRIOL_BLOCK = BLOCKS.register("vitriol", () -> new LiquidBlock(
            ClinkerFluids.VITRIOL.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.DIRT))
    );

    //Special
    public static final DeferredBlock<Block> BLANK_SARCOPHAGUS = createBlock("blank_sarcophagus", () -> new SarcophagusBlock(BlockBehaviour.Properties.ofFullCopy(BRIMSTONE.get()).noOcclusion()));

    public static void defineFlammability(FireBlock fire) {
        fire.setFlammable(THORNY_STEM.get(), 60, 5);
        fire.setFlammable(BRAMBLE_BLOSSOM.get(), 60, 5);
        fire.setFlammable(WITHERING_BRAMBLE_BLOSSOM.get(), 60, 5);
        fire.setFlammable(PEAT_MOSS.get(), 300, 12);
        fire.setFlammable(PEAT_MOSS_BUDS.get(), 300, 400);
    }

    public static <T extends Block> DeferredBlock<T> createBlock(String name, final Supplier<T> supplier) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ClinkerBlocks.BLOCK_ITEMS.registerSimpleBlockItem(name, block);
        return block;
    }

    public static DeferredBlock<Block> createBlockNoItem(String name, final Supplier<? extends Block> supplier) {
        DeferredBlock<Block> block = BLOCKS.register(name, supplier);
        return block;
    }
}
