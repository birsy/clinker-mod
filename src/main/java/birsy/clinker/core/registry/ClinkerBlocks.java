package birsy.clinker.core.registry;

import birsy.clinker.common.world.block.*;
import birsy.clinker.common.world.block.plant.*;
import birsy.clinker.common.world.block.plant.aspen.SwampAspenLogBlock;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ClinkerBlocks
{
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Clinker.MOD_ID);
	public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(Clinker.MOD_ID);

	public static void init()
	{
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCK_ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	// Blocks
	//Alchemy
	public static final DeferredBlock<Block> FERMENTATION_BARREL = createBlock("fermentation_barrel", FermentationBarrelBlock::new);
	public static final DeferredBlock<Block> COUNTER = createBlock("counter", () -> new CounterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(1.0f, 2.0f).sound(SoundType.WOOD)));

	//public static final DeferredBlock<Block> TEST_HEAT_SUPPLIER = createBlock("fermentation_barrel", FermentationBarrelBlock::new);

	public static final DeferredBlock<Block> BLANK_SARCOPHAGUS = createBlock("blank_sarcophagus", () -> new SarcophagusBlock(getBrimstoneProperties().noOcclusion()));
	public static final DeferredBlock<Block> STOVE = createBlock("stove", () -> new StoveControllerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).sound(SoundType.NETHER_BRICKS)));
	public static final DeferredBlock<Block> STOVE_DUMMY = createBlockNoItem("stove_dummy", () -> new StoveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).sound(SoundType.NETHER_BRICKS)));
	public static final DeferredBlock<Block> STOVE_CHIMNEY = createBlockNoItem("stove_chimney", () -> new StoveChimneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).sound(SoundType.NETHER_BRICKS).noOcclusion()));

	//Material Blocks
	public static final DeferredBlock<Block> LEAD_BLOCK = createBlock("lead_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(5.0f, 6.0f).sound(SoundType.NETHERITE_BLOCK)));
	public static final DeferredBlock<Block> RAW_LEAD_BLOCK = createBlock("raw_lead_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.5f, 3.0f).sound(SoundType.ANCIENT_DEBRIS)));
	
	//Soils
	public static final DeferredBlock<Block> ASH = createBlock("ash", () -> new AshBlock(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.5F).sound(SoundType.SNOW)));
	public static final DeferredBlock<Block> ASH_LAYER = createBlock("ash_layers", AshLayerBlock::new);

	public static final DeferredBlock<Block> ASHEN_REGOLITH = createBlock("ashen_regolith", () -> new MudBlock(Block.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.5F).sound(SoundType.NYLIUM)));

	public static final DeferredBlock<Block> MUD = createBlock("mud", () -> new SulfricMudBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(0.5F).sound(SoundType.WET_GRASS)));

	//Brimstone
	public static BlockBehaviour.Properties getBrimstoneProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN)
				  .strength(2.75F, 75.0F)
				  .sound(SoundType.DRIPSTONE_BLOCK);
	}
	
	public static final DeferredBlock<Block> BRIMSTONE = createBlock("brimstone", () -> new Block(getBrimstoneProperties()));
	public static final DeferredBlock<Block> BRIMSTONE_SLAB = createBlock("brimstone_slab", () -> new SlabBlock(getBrimstoneProperties()));
	public static final DeferredBlock<Block> BRIMSTONE_STAIRS = createBlock("brimstone_stairs", () -> new StairBlock(() -> BRIMSTONE.get().defaultBlockState(), getBrimstoneProperties()));
	public static final DeferredBlock<Block> BRIMSTONE_WALL = createBlock("brimstone_wall", () -> new WallBlock(getBrimstoneProperties()));
	
	public static final DeferredBlock<Block> BRIMSTONE_PILLAR = createBlock("brimstone_pillar", () -> new RotatedPillarBlock(getBrimstoneProperties()));
	
	public static final DeferredBlock<Block> COBBLED_BRIMSTONE = createBlock("cobbled_brimstone", () -> new Block(getBrimstoneProperties().sound(SoundType.GILDED_BLACKSTONE)));
	public static final DeferredBlock<Block> COBBLED_BRIMSTONE_SLAB = createBlock("cobbled_brimstone_slab", () -> new SlabBlock(getBrimstoneProperties().sound(SoundType.GILDED_BLACKSTONE)));
	public static final DeferredBlock<Block> COBBLED_BRIMSTONE_STAIRS = createBlock("cobbled_brimstone_stairs", () -> new StairBlock(() -> COBBLED_BRIMSTONE.get().defaultBlockState(), getBrimstoneProperties().sound(SoundType.GILDED_BLACKSTONE)));
	public static final DeferredBlock<Block> COBBLED_BRIMSTONE_WALL = createBlock("cobbled_brimstone_wall", () -> new WallBlock(getBrimstoneProperties().sound(SoundType.GILDED_BLACKSTONE)));
	
	public static final DeferredBlock<Block> POLISHED_BRIMSTONE = createBlock("polished_brimstone", () -> new Block(getBrimstoneProperties()));
	public static final DeferredBlock<Block> POLISHED_BRIMSTONE_SLAB = createBlock("polished_brimstone_slab", () -> new SlabBlock(getBrimstoneProperties()));
	public static final DeferredBlock<Block> POLISHED_BRIMSTONE_STAIRS = createBlock("polished_brimstone_stairs", () -> new StairBlock(() -> POLISHED_BRIMSTONE.get().defaultBlockState(), getBrimstoneProperties()));
	public static final DeferredBlock<Block> POLISHED_BRIMSTONE_WALL = createBlock("polished_brimstone_wall", () -> new WallBlock(getBrimstoneProperties()));
	
	public static final DeferredBlock<Block> BRIMSTONE_BRICKS = createBlock("brimstone_bricks", () -> new Block(getBrimstoneProperties()));
	public static final DeferredBlock<Block> BRIMSTONE_BRICKS_SLAB = createBlock("brimstone_bricks_slab", () -> new SlabBlock(getBrimstoneProperties()));
	public static final DeferredBlock<Block> BRIMSTONE_BRICKS_STAIRS = createBlock("brimstone_bricks_stairs", () -> new StairBlock(() -> BRIMSTONE_BRICKS.get().defaultBlockState(), getBrimstoneProperties()));
	public static final DeferredBlock<Block> BRIMSTONE_BRICKS_WALL = createBlock("brimstone_bricks_wall", () -> new WallBlock(getBrimstoneProperties()));

	public static final DeferredBlock<Block> SMOOTH_BRIMSTONE = createBlock("smooth_brimstone", () -> new Block(getBrimstoneProperties()));


	//Calamine
	public static BlockBehaviour.Properties getCalamineProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
				.strength(1.5F, 3.0F)
				.sound(SoundType.CALCITE);
	}

	public static final DeferredBlock<Block> CALAMINE = createBlock("calamine", () -> new Block(getCalamineProperties()));
	//public static final DeferredBlock<Block> CALAMINE_SLAB = createBlock("calamine_slab", () -> new SlabBlock(getCalamineProperties()));
	//public static final DeferredBlock<Block> CALAMINE_STAIRS = createBlock("calamine_stairs", () -> new StairBlock(() -> CALAMINE.get().defaultBlockState(), getCalamineProperties()));
	//public static final DeferredBlock<Block> CALAMINE_WALL = createBlock("calamine_wall", () -> new WallBlock(getCalamineProperties()));

	public static final DeferredBlock<Block> POLISHED_CALAMINE = createBlock("polished_calamine", () -> new Block(getCalamineProperties()));
	//public static final DeferredBlock<Block> POLISHED_CALAMINE_SLAB = createBlock("polished_calamine_slab", () -> new SlabBlock(getCalamineProperties()));
	//public static final DeferredBlock<Block> POLISHED_CALAMINE_STAIRS = createBlock("polished_calamine_stairs", () -> new StairBlock(() -> POLISHED_CALAMINE.get().defaultBlockState(), getCalamineProperties()));
	//public static final DeferredBlock<Block> POLISHED_CALAMINE_WALL = createBlock("polished_calamine_wall", () -> new WallBlock(getCalamineProperties()));

	public static final DeferredBlock<Block> CALAMINE_BRICKS = createBlock("calamine_bricks", () -> new Block(getCalamineProperties()));
	//public static final DeferredBlock<Block> CALAMINE_BRICKS_SLAB = createBlock("calamine_bricks_slab", () -> new SlabBlock(getCalamineProperties()));
	//public static final DeferredBlock<Block> CALAMINE_BRICKS_STAIRS = createBlock("calamine_bricks_stairs", () -> new StairBlock(() -> CALAMINE_BRICKS.get().defaultBlockState(), getCalamineProperties()));
	//public static final DeferredBlock<Block> CALAMINE_BRICKS_WALL = createBlock("calamine_bricks_wall", () -> new WallBlock(getCalamineProperties()));

	//Capstone
	public static BlockBehaviour.Properties getCapstoneProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
				.strength(1.5F, 3.0F)
				.sound(SoundType.NETHER_BRICKS);
	}

	public static final DeferredBlock<Block> CAPSTONE = createBlock("capstone", () -> new Block(getCapstoneProperties()));
	public static final DeferredBlock<Block> CAPSTONE_SLAB = createBlock("capstone_slab", () -> new SlabBlock(getCapstoneProperties()));
	public static final DeferredBlock<Block> CAPSTONE_STAIRS = createBlock("capstone_stairs", () -> new StairBlock(() -> CAPSTONE.get().defaultBlockState(), getCapstoneProperties()));
	public static final DeferredBlock<Block> CAPSTONE_WALL = createBlock("capstone_wall", () -> new WallBlock(getCapstoneProperties()));

	public static final DeferredBlock<Block> POLISHED_CAPSTONE = createBlock("polished_capstone", () -> new Block(getCapstoneProperties()));
	public static final DeferredBlock<Block> POLISHED_CAPSTONE_SLAB = createBlock("polished_capstone_slab", () -> new SlabBlock(getCapstoneProperties()));
	public static final DeferredBlock<Block> POLISHED_CAPSTONE_STAIRS = createBlock("polished_capstone_stairs", () -> new StairBlock(() -> POLISHED_CAPSTONE.get().defaultBlockState(), getCapstoneProperties()));
	public static final DeferredBlock<Block> POLISHED_CAPSTONE_WALL = createBlock("polished_capstone_wall", () -> new WallBlock(getCapstoneProperties()));

	public static final DeferredBlock<Block> CAPSTONE_BRICKS = createBlock("capstone_bricks", () -> new Block(getCapstoneProperties()));
	public static final DeferredBlock<Block> CAPSTONE_BRICKS_SLAB = createBlock("capstone_bricks_slab", () -> new SlabBlock(getCapstoneProperties()));
	public static final DeferredBlock<Block> CAPSTONE_BRICKS_STAIRS = createBlock("capstone_bricks_stairs", () -> new StairBlock(() -> CAPSTONE_BRICKS.get().defaultBlockState(), getCapstoneProperties()));
	public static final DeferredBlock<Block> CAPSTONE_BRICKS_WALL = createBlock("capstone_bricks_wall", () -> new WallBlock(getCapstoneProperties()));

	//Sulfur
	public static final DeferredBlock<Block> SULFUR_CRYSTAL_BLOCK = createBlock("sulfur_crystal_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
			.strength(1.5F, 6.0F)
			.sound(SoundType.AMETHYST_CLUSTER)));

	public static final DeferredBlock<Block> SULFUR_ROCK_BLOCK = createBlock("sulfur_rock_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
			.strength(1.5F, 6.0F)
			.sound(SoundType.GILDED_BLACKSTONE)));

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
	public static final DeferredBlock<Block> SMOOTH_SHALE_STAIRS = createBlock("smooth_shale_stairs", () -> new StairBlock(() -> SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()));
	public static final DeferredBlock<Block> SMOOTH_SHALE_WALL = createBlock("smooth_shale_wall", () -> new WallBlock(getShaleProperties()));
	
	public static final DeferredBlock<Block> POLISHED_SHALE = createBlock("polished_shale", () -> new Block(getShaleProperties()));
	public static final DeferredBlock<Block> POLISHED_SHALE_SLAB = createBlock("polished_shale_slab", () -> new SlabBlock(getShaleProperties()));
	public static final DeferredBlock<Block> POLISHED_SHALE_STAIRS = createBlock("polished_shale_stairs", () -> new StairBlock(() -> SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()));
	public static final DeferredBlock<Block> POLISHED_SHALE_WALL = createBlock("polished_shale_wall", () -> new WallBlock(getShaleProperties()));
	
	public static final DeferredBlock<Block> SHALE_BRICKS = createBlock("shale_bricks", () -> new Block(getShaleProperties()));
	public static final DeferredBlock<Block> SHALE_BRICKS_SLAB = createBlock("shale_bricks_slab", () -> new SlabBlock(getShaleProperties()));
	public static final DeferredBlock<Block> SHALE_BRICKS_STAIRS = createBlock("shale_bricks_stairs", () -> new StairBlock(() -> SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()));
	public static final DeferredBlock<Block> SHALE_BRICKS_WALL = createBlock("shale_bricks_wall", () -> new WallBlock(getShaleProperties()));
	
	public static final DeferredBlock<Block> SMALL_SHALE_BRICKS = createBlock("small_shale_bricks", () -> new Block(getShaleProperties()));
	public static final DeferredBlock<Block> SMALL_SHALE_BRICKS_SLAB = createBlock("small_shale_bricks_slab", () -> new SlabBlock(getShaleProperties()));
	public static final DeferredBlock<Block> SMALL_SHALE_BRICKS_STAIRS = createBlock("small_shale_bricks_stairs", () -> new StairBlock(() -> SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()));
	public static final DeferredBlock<Block> SMALL_SHALE_BRICKS_FENCE = createBlock("small_shale_bricks_fence", () -> new FenceBlock(getShaleProperties()));
	
	
	//Ancient Bricks
	public static final DeferredBlock<Block> ANCIENT_BRICKS = createBlock("ancient_bricks", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).requiresCorrectToolForDrops().strength(25.0F, 1200.0F)));
	public static final DeferredBlock<Block> ANCIENT_BRICK_SLAB = createBlock("ancient_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(ANCIENT_BRICKS.get())));
	public static final DeferredBlock<Block> ANCIENT_BRICK_STAIRS = createBlock("ancient_brick_stairs", () -> new StairBlock(() -> ClinkerBlocks.ANCIENT_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(ANCIENT_BRICKS.get())));
	public static final DeferredBlock<Block> ANCIENT_SMOOTH_BRICK = createBlock("ancient_smooth_brick", () -> new Block(BlockBehaviour.Properties.ofFullCopy(ANCIENT_BRICKS.get())));
	public static final DeferredBlock<Block> ANCIENT_STONE = createBlock("ancient_stone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(ANCIENT_BRICKS.get())));
	public static final DeferredBlock<Block> ANCIENT_BRICK_FLAT = createBlock("ancient_brick_flat", AncientBrickFlatBlock::new);
	public static final DeferredBlock<Block> ANCIENT_RUNE = createBlock("ancient_rune", AncientBrickRunesBlock::new);

	
	//Metal Ores
	public static final DeferredBlock<Block> LEAD_ORE = createBlock("lead_ore", () -> new Block(getBrimstoneProperties()));
	
	
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
	public static final DeferredBlock<Block> LOCUST_STAIRS = createBlock("locust_stairs", () -> new StairBlock(() -> ClinkerBlocks.LOCUST_PLANKS.get().defaultBlockState(), getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
	public static final DeferredBlock<Block> LOCUST_SLAB = createBlock("locust_slab", () -> new SlabBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
	
	//public static final DeferredBlock<Block> LOCUST_FENCE = createBlock("locust_fence", () -> new FenceBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
	//public static final DeferredBlock<Block> LOCUST_FENCE_GATE = createBlock("locust_fence_gate", () -> new FenceGateBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
	//public static final DeferredBlock<Block> LOCUST_DOOR = createBlock("locust_door", () -> new DoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion(), BlockSetType.CRIMSON));
	//public static final DeferredBlock<Block> LOCUST_TRAPDOOR = createBlock("locust_trapdoor", () -> new TrapDoorBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
	//public static final DeferredBlock<Block> LOCUST_BUTTON = createBlock("locust_button", () -> new ButtonBlock(getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));
	//public static final DeferredBlock<Block> LOCUST_PRESSURE_PLATE = createBlock("locust_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, getOthershoreWoodProperties(MapColor.TERRACOTTA_GREEN)));

	public static final DeferredBlock<Block> SWAMP_ASPEN_LOG = createBlock("swamp_aspen_log", () -> new SwampAspenLogBlock(BlockBehaviour.Properties.of().mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor.WOOD : MapColor.COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.STEM).noOcclusion()));
	public static final DeferredBlock<Block> STRIPPED_SWAMP_ASPEN_LOG = createBlock("stripped_swamp_aspen_log", () -> new SwampAspenLogBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.STEM).noOcclusion()));

	//Plants
	public static final DeferredBlock<Block> TALL_MUD_REEDS = createBlock("tall_mud_reeds", () -> new DoubleMudReedsBlock(BlockBehaviour.Properties.of().noCollission().instabreak().noOcclusion().sound(SoundType.HANGING_ROOTS).offsetType(BlockBehaviour.OffsetType.XZ).replaceable()));
	public static final DeferredBlock<Block> SHORT_MUD_REEDS = createBlock("short_mud_reeds", () -> new MudReedsBlock(BlockBehaviour.Properties.of().noCollission().instabreak().noOcclusion().sound(SoundType.HANGING_ROOTS).offsetType(BlockBehaviour.OffsetType.XZ).replaceable()));
	public static final DeferredBlock<Block> MUD_REEDS = createBlock("mud_reeds", () -> new MudReedsBlock(BlockBehaviour.Properties.of().noCollission().instabreak().noOcclusion().sound(SoundType.HANGING_ROOTS).offsetType(BlockBehaviour.OffsetType.XZ).replaceable()));

	public static final DeferredBlock<Block> CAVE_FIG_STEM = createBlock("cave_fig_stem", () -> new HugeMushroomBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.CALCITE)));
	public static final DeferredBlock<Block> CAVE_FIG_ROOTS = createBlock("cave_fig_roots", () -> new CaveFigRootsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).noOcclusion().sound(SoundType.CALCITE)));

	public static final DeferredBlock<Block> FAIRY_FRUIT_BLOCK = createBlockNoItem("fairy_fruit_block", () -> new FairyFruitBlock(BlockBehaviour.Properties.of().noCollission().instabreak().noOcclusion().sound(SoundType.HANGING_ROOTS).offsetType(BlockBehaviour.OffsetType.XZ).lightLevel((state) -> 10)));

	public static final DeferredBlock<Block> DRIED_CLOVERS = createBlock("dried_clovers", () -> new DriedCloversBlock(BlockBehaviour.Properties.of().noOcclusion().mapColor(MapColor.COLOR_GREEN).strength(0.1F).sound(SoundType.HANGING_ROOTS).replaceable().pushReaction(PushReaction.DESTROY)));

	public static final DeferredBlock<Block> FULMINA_FLOWER = createBlock("fulmina_flower", () -> new FulminaFlowerBlock(BlockBehaviour.Properties.of().noOcclusion().mapColor(MapColor.COLOR_GRAY).strength(0.1F).sound(SoundType.HANGING_ROOTS).pushReaction(PushReaction.DESTROY).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape()));

	//Special

	public static DeferredBlock<Block> createBlock(String name, final Supplier<? extends Block> supplier) {
		DeferredBlock<Block> block = BLOCKS.register(name, supplier);
		BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
		return block;
	}

	public static DeferredBlock<Block> createBlockNoItem(String name, final Supplier<? extends Block> supplier) {
		DeferredBlock<Block> block = BLOCKS.register(name, supplier);
		return block;
	}
}
