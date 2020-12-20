package birsy.clinker.core.registry;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import birsy.clinker.common.block.AncientBrickFlatBlock;
import birsy.clinker.common.block.AncientBrickRunesBlock;
import birsy.clinker.common.block.AshBlock;
import birsy.clinker.common.block.AshLayerBlock;
import birsy.clinker.common.block.DustStalagmiteBlock;
import birsy.clinker.common.block.FoulAirBlock;
import birsy.clinker.common.block.MaterialBlock;
import birsy.clinker.common.block.MetalMaterialBlock;
import birsy.clinker.common.block.OreClinkerBlock;
import birsy.clinker.common.block.PoisonLogBlock;
import birsy.clinker.common.block.RootGrassBlock;
import birsy.clinker.common.block.RootedAshBlock;
import birsy.clinker.common.block.ThornLogConnectedBlock;
import birsy.clinker.common.block.belwithstone.BelwithPillarBlock;
import birsy.clinker.common.block.belwithstone.CyclopeanBelwithBricksBlock;
import birsy.clinker.common.block.belwithstone.MonolithicBelwithBricksBlock;
import birsy.clinker.common.block.bramble.BrambleBlock;
import birsy.clinker.common.block.bramble.BrambleRootsBlock;
import birsy.clinker.common.block.bramble.BrambleRootsBottomBlock;
import birsy.clinker.common.block.bramble.BrambleVinesBlock;
import birsy.clinker.common.block.bramble.BrambleVinesTopBlock;
import birsy.clinker.common.block.driedsoulsand.ChiseledDrySoulsandBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandFenceBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandPillarBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandSlabBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandStairsBlock;
import birsy.clinker.common.block.riekplant.RiekPlantBlock;
import birsy.clinker.common.block.riekplant.RiekTubeBlock;
import birsy.clinker.common.block.riekplant.RiekVinesBlock;
import birsy.clinker.common.block.trees.LocustTree;
import birsy.clinker.core.Clinker;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
	
	//Material Blocks
	public static final RegistryObject<Block> LEAD_BLOCK = createBlock("lead_block", () -> new MetalMaterialBlock(), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SULFUR_BLOCK = createBlock("sulfur_block", () -> new MaterialBlock(), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> IRON_PYRITE_BLOCK = createBlock("iron_pyrite_block", () -> new MetalMaterialBlock(), Clinker.CLINKER_BLOCKS);
	
	//Soils
	public static final RegistryObject<Block> ASH = createBlock("ash", AshBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ASH_LAYER = createBlock("ash_layers", AshLayerBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ROOTED_ASH = createBlock("rooted_ash", () -> new RootedAshBlock(ASH.get()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> PACKED_ASH = createBlock("packed_ash", () -> new Block(Block.Properties.create(Material.CLAY, MaterialColor.GRAY).hardnessAndResistance(0.5F).sound(SoundType.GROUND).harvestTool(ToolType.SHOVEL)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ROCKY_PACKED_ASH = createBlock("rocky_packed_ash", () -> new Block(Block.Properties.create(Material.CLAY, MaterialColor.GRAY).hardnessAndResistance(0.5F).sound(SoundType.GROUND).harvestTool(ToolType.SHOVEL)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ROOTED_PACKED_ASH = createBlock("rooted_packed_ash", () -> new RootedAshBlock(PACKED_ASH.get()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SALT_BLOCK = createBlock("salt_block", () -> new SandBlock(14406560, AbstractBlock.Properties.create(Material.SAND, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.5F).sound(SoundType.GROUND).harvestTool(ToolType.SHOVEL)), Clinker.CLINKER_BLOCKS);
	
	
	//Brimstone
	public static AbstractBlock.Properties getBrimstoneProperties() {
		return AbstractBlock.Properties.create(Material.ROCK, MaterialColor.BROWN_TERRACOTTA)
				  .hardnessAndResistance(2.75F, 75.0F)
				  .sound(SoundType.STONE)
				  .harvestLevel(1)
				  .harvestTool(ToolType.PICKAXE);
	}
	
	public static final RegistryObject<Block> BRIMSTONE = createBlock("brimstone", () -> new Block(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_SLAB = createBlock("brimstone_slab", () -> new SlabBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_STAIRS = createBlock("brimstone_stairs", () -> new StairsBlock(() -> BRIMSTONE.get().getDefaultState(), getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_WALL = createBlock("brimstone_wall", () -> new WallBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> BRIMSTONE_PILLAR = createBlock("brimstone_pillar", () -> new RotatedPillarBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> COBBLED_BRIMSTONE = createBlock("cobbled_brimstone", () -> new Block(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> COBBLED_BRIMSTONE_SLAB = createBlock("cobbled_brimstone_slab", () -> new SlabBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> COBBLED_BRIMSTONE_STAIRS = createBlock("cobbled_brimstone_stairs", () -> new StairsBlock(() -> COBBLED_BRIMSTONE.get().getDefaultState(), getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> COBBLED_BRIMSTONE_WALL = createBlock("cobbled_brimstone_wall", () -> new WallBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> POLISHED_BRIMSTONE = createBlock("polished_brimstone", () -> new Block(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_BRIMSTONE_SLAB = createBlock("polished_brimstone_slab", () -> new SlabBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_BRIMSTONE_STAIRS = createBlock("polished_brimstone_stairs", () -> new StairsBlock(() -> POLISHED_BRIMSTONE.get().getDefaultState(), getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_BRIMSTONE_WALL = createBlock("polished_brimstone_wall", () -> new WallBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> BRIMSTONE_BRICKS = createBlock("brimstone_bricks", () -> new Block(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_BRICKS_SLAB = createBlock("brimstone_bricks_slab", () -> new SlabBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_BRICKS_STAIRS = createBlock("brimstone_bricks_stairs", () -> new StairsBlock(() -> BRIMSTONE_BRICKS.get().getDefaultState(), getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRIMSTONE_BRICKS_WALL = createBlock("brimstone_bricks_wall", () -> new WallBlock(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	
	//Scorstone
	public static AbstractBlock.Properties getScorstoneProperties() {
		return AbstractBlock.Properties.create(Material.ROCK, MaterialColor.WHITE_TERRACOTTA)
				  .hardnessAndResistance(1.5F, 6.0F)
				  .sound(SoundType.BASALT)
				  .harvestLevel(1)
				  .harvestTool(ToolType.PICKAXE);
	}
	
	public static final RegistryObject<Block> SCORSTONE = createBlock("scorstone", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SCORSTONE_SLAB = createBlock("scorstone_slab", () -> new SlabBlock(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SCORSTONE_STAIRS = createBlock("scorstone_stairs", () -> new StairsBlock(() -> SCORSTONE.get().getDefaultState(), getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SCORSTONE_WALL = createBlock("scorstone_wall", () -> new WallBlock(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SCORSTONE_BRICKS = createBlock("scorstone_bricks", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SCORSTONE_BRICKS_WALL = createBlock("scorstone_bricks_wall", () -> new WallBlock(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> COBBLED_SCORSTONE = createBlock("cobbled_scorstone", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_SCORSTONE = createBlock("polished_scorstone", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CHISELED_SCORSTONE = createBlock("chiseled_scorstone", () -> new Block(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SCORSTONE_PILLAR = createBlock("scorstone_pillar", () -> new RotatedPillarBlock(getScorstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	
	//Dried Soulsand
	public static final RegistryObject<Block> DRIED_SOULSAND = createBlock("dried_soulsand", DriedSoulsandBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> DRIED_SOULSAND_BRICKS = createBlock("dried_soulsand_bricks", DriedSoulsandBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> DRIED_SOULSAND_ROOF = createBlock("dried_soulsand_roof", DriedSoulsandBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> COBBLED_DRY_SOULSAND = createBlock("cobbled_dry_soulsand", DriedSoulsandBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_DRY_SOULSAND = createBlock("polished_dry_soulsand", DriedSoulsandBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CHISELED_DRY_SOULSAND = createBlock("chiseled_dry_soulsand", ChiseledDrySoulsandBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> DRIED_SOULSAND_PILLAR = createBlock("dried_soulsand_pillar", DriedSoulsandPillarBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> DRIED_SOULSAND_SLAB = createBlock("dried_soulsand_slab", DriedSoulsandSlabBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> DRIED_SOULSAND_STAIRS = createBlock("dried_soulsand_stairs", DriedSoulsandStairsBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> DRIED_SOULSAND_FENCE = createBlock("dried_soulsand_fence", DriedSoulsandFenceBlock::new, Clinker.CLINKER_BLOCKS);
	
	
	//Shale
	public static AbstractBlock.Properties getShaleProperties() {
		return AbstractBlock.Properties.create(Material.ROCK, MaterialColor.LIGHT_BLUE_TERRACOTTA)
				  .hardnessAndResistance(2.75F, 10.0F)
				  .sound(SoundType.ANCIENT_DEBRIS)
				  .harvestLevel(1)
				  .harvestTool(ToolType.PICKAXE);
	}
	
	public static final RegistryObject<Block> SHALE = createBlock("shale", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SHALE_PILLAR = createBlock("shale_pillar", () -> new RotatedPillarBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> SMOOTH_SHALE = createBlock("smooth_shale", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMOOTH_SHALE_SLAB = createBlock("smooth_shale_slab", () -> new SlabBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMOOTH_SHALE_STAIRS = createBlock("smooth_shale_stairs", () -> new StairsBlock(() -> SMOOTH_SHALE.get().getDefaultState(), getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMOOTH_SHALE_WALL = createBlock("smooth_shale_wall", () -> new WallBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> POLISHED_SHALE = createBlock("polished_shale", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_SHALE_SLAB = createBlock("polished_shale_slab", () -> new SlabBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_SHALE_STAIRS = createBlock("polished_shale_stairs", () -> new StairsBlock(() -> SMOOTH_SHALE.get().getDefaultState(), getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> POLISHED_SHALE_WALL = createBlock("polished_shale_wall", () -> new WallBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SHALE_BRICKS = createBlock("shale_bricks", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SHALE_BRICKS_SLAB = createBlock("shale_bricks_slab", () -> new SlabBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SHALE_BRICKS_STAIRS = createBlock("shale_bricks_stairs", () -> new StairsBlock(() -> SMOOTH_SHALE.get().getDefaultState(), getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SHALE_BRICKS_WALL = createBlock("shale_bricks_wall", () -> new WallBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SMALL_SHALE_BRICKS = createBlock("small_shale_bricks", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMALL_SHALE_BRICKS_SLAB = createBlock("small_shale_bricks_slab", () -> new SlabBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMALL_SHALE_BRICKS_STAIRS = createBlock("small_shale_bricks_stairs", () -> new StairsBlock(() -> SMOOTH_SHALE.get().getDefaultState(), getShaleProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SMALL_SHALE_BRICKS_FENCE = createBlock("small_shale_bricks_fence", () -> new FenceBlock(getShaleProperties()), Clinker.CLINKER_BLOCKS);
	
	
	//Ancient Bricks
	public static final RegistryObject<Block> ANCIENT_BRICKS = createBlock("ancient_bricks", () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(25.0F, 1200.0F)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_BRICK_SLAB = createBlock("ancient_brick_slab", () -> new SlabBlock(AbstractBlock.Properties.from(ANCIENT_BRICKS.get())), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_BRICK_STAIRS = createBlock("ancient_brick_stairs", () -> new StairsBlock(() -> ClinkerBlocks.ANCIENT_BRICKS.get().getDefaultState(), AbstractBlock.Properties.from(ANCIENT_BRICKS.get())), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_SMOOTH_BRICK = createBlock("ancient_smooth_brick", () -> new Block(AbstractBlock.Properties.from(ANCIENT_BRICKS.get())), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_STONE = createBlock("ancient_stone", () -> new Block(AbstractBlock.Properties.from(ANCIENT_BRICKS.get())), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_BRICK_FLAT = createBlock("ancient_brick_flat", AncientBrickFlatBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ANCIENT_RUNE = createBlock("ancient_rune", AncientBrickRunesBlock::new, Clinker.CLINKER_BLOCKS);
	
	
	//Belwith Stone
	public static final RegistryObject<Block> BELWITH_PILLAR = createBlock("belwith_pillar", BelwithPillarBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CYCLOPEAN_BELWITH_BRICKS = createBlock("cyclopean_belwith_bricks", CyclopeanBelwithBricksBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CRACKED_CYCLOPEAN_BELWITH_BRICKS = createBlock("cracked_cyclopean_belwith_bricks", CyclopeanBelwithBricksBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> MONOLITHIC_BELWITH_BRICKS = createBlock("monolithic_belwith_bricks", MonolithicBelwithBricksBlock::new, Clinker.CLINKER_BLOCKS);
	
	
	//Unsmeltable Ores
	public static final RegistryObject<Block> SULFUR_ORE = createBlock("sulfur_ore", () -> new OreClinkerBlock(3.0F, 75.0F, 2, 2, 3, SoundType.STONE), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> RUBY_ORE = createBlock("ruby_ore", () -> new OreClinkerBlock(1.5F, 6.0F, 2, 0, 0, SoundType.STONE), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> OVERWORLD_LEAD_ORE = createBlock("overworld_lead_ore", () -> new OreClinkerBlock(3.0F, 6.0F, 2, 1, 2, SoundType.STONE), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> NETHER_LEAD_ORE = createBlock("nether_lead_ore", () -> new OreClinkerBlock(3.0F, 3.0F, 2, 2, 3, SoundType.NETHER_ORE), Clinker.CLINKER_BLOCKS);
	
	
	//Metal Ores
	public static final RegistryObject<Block> LEAD_ORE = createBlock("lead_ore", () -> new Block(getBrimstoneProperties()), Clinker.CLINKER_BLOCKS);
	
	
	//Wood Types
	public static AbstractBlock.Properties getLocustWoodProperties() {
		return AbstractBlock.Properties.create(Material.WOOD, MaterialColor.WOOD)
				.hardnessAndResistance(2.0F, 3.0F)
				.sound(SoundType.WOOD);
	}
	
	public static final RegistryObject<Block> LOCUST_LOG = createBlock("locust_log", PoisonLogBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> TRIMMED_LOCUST_LOG = createBlock("trimmed_locust_log", PoisonLogBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> STRIPPED_LOCUST_LOG = createBlock("stripped_locust_log", 
			() -> new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, (state) -> {return state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.FOLIAGE : MaterialColor.STONE;}).hardnessAndResistance(2.0F).sound(SoundType.WOOD)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_LEAVES = createBlock("locust_leaves", 
			() -> new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_SAPLING = createBlock("locust_sapling", 
			() -> new SaplingBlock(new LocustTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT)), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> LOCUST_PLANKS = createBlock("locust_planks", () -> new Block(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_STAIRS = createBlock("locust_stairs", () -> new StairsBlock(() -> ClinkerBlocks.LOCUST_PLANKS.get().getDefaultState(), getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_SLAB = createBlock("locust_slab", () -> new SlabBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> LOCUST_FENCE = createBlock("locust_fence", () -> new FenceBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_FENCE_GATE = createBlock("locust_fence_gate", () -> new FenceGateBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_DOOR = createBlock("locust_door", () -> new DoorBlock(AbstractBlock.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD).notSolid()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_TRAPDOOR = createBlock("locust_trapdoor", () -> new TrapDoorBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_BUTTON = createBlock("locust_button", () -> new WoodButtonBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_PRESSURE_PLATE = createBlock("locust_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	
	
	//Plants
	public static final RegistryObject<Block> BRAMBLE = createBlock("bramble", BrambleBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRAMBLE_VINES = createBlock("bramble_vines", BrambleVinesBlock::new, null);
	public static final RegistryObject<Block> BRAMBLE_VINES_TOP = createBlock("bramble_vines_top", BrambleVinesTopBlock::new, null);
	public static final RegistryObject<Block> BRAMBLE_ROOTS = createBlock("bramble_roots", BrambleRootsBlock::new, null);
	public static final RegistryObject<Block> BRAMBLE_ROOTS_BOTTOM = createBlock("bramble_roots_bottom", BrambleRootsBottomBlock::new, null);
	
	public static final RegistryObject<Block> THORN_LOG = createBlock("thorn_log", ThornLogConnectedBlock::new, Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> STRIPPED_THORN_LOG = createBlock("stripped_thorn_log", ThornLogConnectedBlock::new, Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> ROOT_GRASS = createBlock("root_grass", RootGrassBlock::new, Clinker.CLINKER_BLOCKS);
	
	
	//Riek Plant
	public static final RegistryObject<Block> RIEK_PLANT = createBlock("riek_plant", RiekPlantBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> RIEK_TUBE = createBlock("riek_tube", RiekTubeBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> FERTILE_RIEK_PLANT = createBlock("fertile_riek_plant", RiekPlantBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> RIEK_VINES = createBlock("riek_vines", RiekVinesBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> FERTILE_RIEK_VINES = createBlock("fertile_riek_vines", RiekVinesBlock::new, Clinker.CLINKER_BLOCKS);
	
	
	//Special
	public static final RegistryObject<Block> FOUL_AIR = createBlock("foul_air", FoulAirBlock::new, null);
	public static final RegistryObject<Block> DUST_STALAGMITE = createBlock("dust_stalagmite", DustStalagmiteBlock::new, null);
	
	
	
	public static <B extends Block> RegistryObject<B> createBlock(String name, final Supplier<? extends B> supplier, @Nullable ItemGroup group) {
		RegistryObject<B> block = BLOCKS.register(name, supplier);
		if (group != null) {
			ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(group)));
		}
		return block;
	}
}
