package birsy.clinker.core.registry;

import birsy.clinker.common.block.*;
import birsy.clinker.common.block.aspen.SwampAspenLogBlock;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ClinkerBlocks
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Clinker.MOD_ID);
	public static final DeferredRegister<Item>  ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Clinker.MOD_ID);
	
	public static void init()
	{
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	// Blocks
	//Fluids


	//Material Blocks
	public static final RegistryObject<Block> LEAD_BLOCK = createBlock("lead_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).strength(5.0f, 6.0f).sound(SoundType.NETHERITE_BLOCK)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> RAW_LEAD_BLOCK = createBlock("raw_lead_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY).strength(2.5f, 3.0f).sound(SoundType.ANCIENT_DEBRIS)), Clinker.CLINKER_BLOCKS);
	
	//Soils
	public static final RegistryObject<Block> ASH = createBlock("ash", () -> new AshBlock(Block.Properties.of(Material.SNOW, MaterialColor.COLOR_GRAY).strength(0.5F).sound(SoundType.SNOW)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ASH_LAYER = createBlock("ash_layers", AshLayerBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ROOTED_ASH = createBlock("rooted_ash", () -> new RootedAshBlock(ASH.get()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> PACKED_ASH = createBlock("packed_ash", () -> new Block(Block.Properties.of(Material.CLAY, MaterialColor.COLOR_GRAY).strength(0.5F).sound(SoundType.GRAVEL)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ROCKY_PACKED_ASH = createBlock("rocky_packed_ash", () -> new Block(Block.Properties.of(Material.CLAY, MaterialColor.COLOR_GRAY).strength(0.5F).sound(SoundType.GRAVEL)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ROOTED_PACKED_ASH = createBlock("rooted_packed_ash", () -> new RootedAshBlock(PACKED_ASH.get()), Clinker.CLINKER_BLOCKS);
	
	//Brimstone
	public static BlockBehaviour.Properties getBrimstoneProperties() {
		return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_BROWN)
				  .strength(2.75F, 75.0F)
				  .sound(SoundType.DRIPSTONE_BLOCK);
	}
	
	public static final RegistryObject<Block> BRIMSTONE = createBlock("brimstone", () -> new Block(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_SLAB = createBlock("brimstone_slab", () -> new SlabBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_STAIRS = createBlock("brimstone_stairs", () -> new StairBlock(() -> BRIMSTONE.get().defaultBlockState(), getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_WALL = createBlock("brimstone_wall", () -> new WallBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> BRIMSTONE_PILLAR = createBlock("brimstone_pillar", () -> new RotatedPillarBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> COBBLED_BRIMSTONE = createBlock("cobbled_brimstone", () -> new Block(getBrimstoneProperties().sound(SoundType.GILDED_BLACKSTONE)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> COBBLED_BRIMSTONE_SLAB = createBlock("cobbled_brimstone_slab", () -> new SlabBlock(getBrimstoneProperties().sound(SoundType.GILDED_BLACKSTONE)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> COBBLED_BRIMSTONE_STAIRS = createBlock("cobbled_brimstone_stairs", () -> new StairBlock(() -> COBBLED_BRIMSTONE.get().defaultBlockState(), getBrimstoneProperties().sound(SoundType.GILDED_BLACKSTONE)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> COBBLED_BRIMSTONE_WALL = createBlock("cobbled_brimstone_wall", () -> new WallBlock(getBrimstoneProperties().sound(SoundType.GILDED_BLACKSTONE)), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> POLISHED_BRIMSTONE = createBlock("polished_brimstone", () -> new Block(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_BRIMSTONE_SLAB = createBlock("polished_brimstone_slab", () -> new SlabBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_BRIMSTONE_STAIRS = createBlock("polished_brimstone_stairs", () -> new StairBlock(() -> POLISHED_BRIMSTONE.get().defaultBlockState(), getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_BRIMSTONE_WALL = createBlock("polished_brimstone_wall", () -> new WallBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> BRIMSTONE_BRICKS = createBlock("brimstone_bricks", () -> new Block(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_BRICKS_SLAB = createBlock("brimstone_bricks_slab", () -> new SlabBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_BRICKS_STAIRS = createBlock("brimstone_bricks_stairs", () -> new StairBlock(() -> BRIMSTONE_BRICKS.get().defaultBlockState(), getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_BRICKS_WALL = createBlock("brimstone_bricks_wall", () -> new WallBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	//Capstone
	public static BlockBehaviour.Properties getCapstoneProperties() {
		return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_CYAN)
				.strength(1.5F, 6.0F)
				.sound(SoundType.DEEPSLATE_BRICKS);
	}

	public static final RegistryObject<Block> CAPSTONE = createBlock("capstone", () -> new Block(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CAPSTONE_SLAB = createBlock("capstone_slab", () -> new SlabBlock(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CAPSTONE_STAIRS = createBlock("capstone_stairs", () -> new StairBlock(() -> CAPSTONE.get().defaultBlockState(), getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CAPSTONE_WALL = createBlock("capstone_wall", () -> new WallBlock(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> CAPSTONE_SULFUR_ORE = createBlock("capstone_sulfur_ore", () -> new OreClinkerBlock(1.5F, 6.0F, 2, 2, SoundType.DEEPSLATE_BRICKS), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> POLISHED_CAPSTONE = createBlock("polished_capstone", () -> new Block(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_CAPSTONE_SLAB = createBlock("polished_capstone_slab", () -> new SlabBlock(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_CAPSTONE_STAIRS = createBlock("polished_capstone_stairs", () -> new StairBlock(() -> POLISHED_CAPSTONE.get().defaultBlockState(), getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_CAPSTONE_WALL = createBlock("polished_capstone_wall", () -> new WallBlock(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> CAPSTONE_BRICKS = createBlock("capstone_bricks", () -> new Block(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CAPSTONE_BRICKS_SLAB = createBlock("capstone_bricks_slab", () -> new SlabBlock(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CAPSTONE_BRICKS_STAIRS = createBlock("capstone_bricks_stairs", () -> new StairBlock(() -> CAPSTONE_BRICKS.get().defaultBlockState(), getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CAPSTONE_BRICKS_WALL = createBlock("capstone_bricks_wall", () -> new WallBlock(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);

	//Sulfur
	public static final RegistryObject<Block> SULFUR_CRYSTAL_BLOCK = createBlock("sulfur_crystal_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.GOLD)
			.strength(1.5F, 6.0F)
			.sound(SoundType.AMETHYST_CLUSTER)), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> SULFUR_ROCK_BLOCK = createBlock("sulfur_rock_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.GOLD)
			.strength(1.5F, 6.0F)
			.sound(SoundType.GILDED_BLACKSTONE)), Clinker.CLINKER_BLOCKS);


	//Scorstone
	public static BlockBehaviour.Properties getScorstoneProperties() {
		return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_LIGHT_GRAY)
				  .strength(1.5F, 6.0F)
				  .sound(SoundType.BASALT);
	}
	
	public static final RegistryObject<Block> SCORSTONE = createBlock("scorstone", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SCORSTONE_SLAB = createBlock("scorstone_slab", () -> new SlabBlock(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SCORSTONE_STAIRS = createBlock("scorstone_stairs", () -> new StairBlock(() -> SCORSTONE.get().defaultBlockState(), getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SCORSTONE_WALL = createBlock("scorstone_wall", () -> new WallBlock(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SCORSTONE_BRICKS = createBlock("scorstone_bricks", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SCORSTONE_BRICKS_WALL = createBlock("scorstone_bricks_wall", () -> new WallBlock(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> COBBLED_SCORSTONE = createBlock("cobbled_scorstone", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_SCORSTONE = createBlock("polished_scorstone", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CHISELED_SCORSTONE = createBlock("chiseled_scorstone", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SCORSTONE_PILLAR = createBlock("scorstone_pillar", () -> new RotatedPillarBlock(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	//Shale
	public static BlockBehaviour.Properties getShaleProperties() {
		return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_GRAY)
				  .strength(2.75F, 10.0F)
				  .sound(SoundType.ANCIENT_DEBRIS);
	}
	
	public static final RegistryObject<Block> SHALE = createBlock("shale", () -> new RotatedPillarBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SHALE_PILLAR = createBlock("shale_pillar", () -> new RotatedPillarBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> SMOOTH_SHALE = createBlock("smooth_shale", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMOOTH_SHALE_SLAB = createBlock("smooth_shale_slab", () -> new SlabBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMOOTH_SHALE_STAIRS = createBlock("smooth_shale_stairs", () -> new StairBlock(() -> SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMOOTH_SHALE_WALL = createBlock("smooth_shale_wall", () -> new WallBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> POLISHED_SHALE = createBlock("polished_shale", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_SHALE_SLAB = createBlock("polished_shale_slab", () -> new SlabBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_SHALE_STAIRS = createBlock("polished_shale_stairs", () -> new StairBlock(() -> SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_SHALE_WALL = createBlock("polished_shale_wall", () -> new WallBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SHALE_BRICKS = createBlock("shale_bricks", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SHALE_BRICKS_SLAB = createBlock("shale_bricks_slab", () -> new SlabBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SHALE_BRICKS_STAIRS = createBlock("shale_bricks_stairs", () -> new StairBlock(() -> SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SHALE_BRICKS_WALL = createBlock("shale_bricks_wall", () -> new WallBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SMALL_SHALE_BRICKS = createBlock("small_shale_bricks", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMALL_SHALE_BRICKS_SLAB = createBlock("small_shale_bricks_slab", () -> new SlabBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMALL_SHALE_BRICKS_STAIRS = createBlock("small_shale_bricks_stairs", () -> new StairBlock(() -> SMOOTH_SHALE.get().defaultBlockState(), getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMALL_SHALE_BRICKS_FENCE = createBlock("small_shale_bricks_fence", () -> new FenceBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	
	
	//Ancient Bricks
	public static final RegistryObject<Block> ANCIENT_BRICKS = createBlock("ancient_bricks", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND).requiresCorrectToolForDrops().strength(25.0F, 1200.0F)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_BRICK_SLAB = createBlock("ancient_brick_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(ANCIENT_BRICKS.get())), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_BRICK_STAIRS = createBlock("ancient_brick_stairs", () -> new StairBlock(() -> ClinkerBlocks.ANCIENT_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(ANCIENT_BRICKS.get())), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_SMOOTH_BRICK = createBlock("ancient_smooth_brick", () -> new Block(BlockBehaviour.Properties.copy(ANCIENT_BRICKS.get())), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_STONE = createBlock("ancient_stone", () -> new Block(BlockBehaviour.Properties.copy(ANCIENT_BRICKS.get())), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_BRICK_FLAT = createBlock("ancient_brick_flat", AncientBrickFlatBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_RUNE = createBlock("ancient_rune", AncientBrickRunesBlock::new, Clinker.CLINKER_BLOCKS);

	
	//Unsmeltable Ores
	public static final RegistryObject<Block> SULFUR_ORE = createBlock("sulfur_ore", () -> new OreClinkerBlock(3.0F, 75.0F, 2, 2, SoundType.DRIPSTONE_BLOCK), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> OVERWORLD_LEAD_ORE = createBlock("overworld_lead_ore", () -> new OreClinkerBlock(3.0F, 6.0F, 2, 1, SoundType.STONE), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> NETHER_LEAD_ORE = createBlock("nether_lead_ore", () -> new OreClinkerBlock(3.0F, 3.0F, 2, 2, SoundType.NETHER_ORE), Clinker.CLINKER_BLOCKS);
	
	
	//Metal Ores
	public static final RegistryObject<Block> LEAD_ORE = createBlock("lead_ore", () -> new Block(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	
	//Wood Types
	public static BlockBehaviour.Properties getOthershoreWoodProperties(MaterialColor colorIn) {
		return BlockBehaviour.Properties.of(Material.WOOD, colorIn)
				.strength(2.0F, 3.0F)
				.sound(SoundType.WOOD);
	}
	
	public static final RegistryObject<Block> LOCUST_LOG = createBlock("locust_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, (state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.TERRACOTTA_GREEN : MaterialColor.STONE).strength(2.0F).sound(SoundType.STEM)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> TRIMMED_LOCUST_LOG = createBlock("trimmed_locust_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, (state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.TERRACOTTA_GREEN : MaterialColor.STONE).strength(2.0F).sound(SoundType.STEM)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> STRIPPED_LOCUST_LOG = createBlock("stripped_locust_log", 
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, (state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.TERRACOTTA_GREEN : MaterialColor.STONE).strength(2.0F).sound(SoundType.WOOD)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_LEAVES = createBlock("locust_leaves", 
			() -> new LocustFlowersBlock(BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.TERRACOTTA_ORANGE).strength(0.2F).randomTicks().sound(SoundType.AZALEA_LEAVES).noOcclusion()), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> LOCUST_PLANKS = createBlock("locust_planks", () -> new Block(getOthershoreWoodProperties(MaterialColor.TERRACOTTA_GREEN)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_STAIRS = createBlock("locust_stairs", () -> new StairBlock(() -> ClinkerBlocks.LOCUST_PLANKS.get().defaultBlockState(), getOthershoreWoodProperties(MaterialColor.TERRACOTTA_GREEN)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_SLAB = createBlock("locust_slab", () -> new SlabBlock(getOthershoreWoodProperties(MaterialColor.TERRACOTTA_GREEN)), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> LOCUST_FENCE = createBlock("locust_fence", () -> new FenceBlock(getOthershoreWoodProperties(MaterialColor.TERRACOTTA_GREEN)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_FENCE_GATE = createBlock("locust_fence_gate", () -> new FenceGateBlock(getOthershoreWoodProperties(MaterialColor.TERRACOTTA_GREEN)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_DOOR = createBlock("locust_door", () -> new DoorBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_TRAPDOOR = createBlock("locust_trapdoor", () -> new TrapDoorBlock(getOthershoreWoodProperties(MaterialColor.TERRACOTTA_GREEN)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_BUTTON = createBlock("locust_button", () -> new WoodButtonBlock(getOthershoreWoodProperties(MaterialColor.TERRACOTTA_GREEN)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_PRESSURE_PLATE = createBlock("locust_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, getOthershoreWoodProperties(MaterialColor.TERRACOTTA_GREEN)), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> SWAMP_ASPEN_LOG = createBlock("swamp_aspen_log", () -> new SwampAspenLogBlock(BlockBehaviour.Properties.of(Material.WOOD, (state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.WOOD : MaterialColor.COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.STEM).noOcclusion()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> STRIPPED_SWAMP_ASPEN_LOG = createBlock("stripped_swamp_aspen_log", () -> new SwampAspenLogBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sound(SoundType.STEM).noOcclusion()), Clinker.CLINKER_BLOCKS);

	//Plants

	//Riek Plant

	//Special

	public static RegistryObject<Block> createBlock(String name, final Supplier<? extends Block> supplier, @Nullable CreativeModeTab group) {
		RegistryObject<Block> block = BLOCKS.register(name, supplier);
		if (group != null) {
			ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(group)));
		}
		return block;
	}
}
