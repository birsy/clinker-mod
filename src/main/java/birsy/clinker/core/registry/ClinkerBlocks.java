package birsy.clinker.core.registry;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import birsy.clinker.common.block.*;
import birsy.clinker.common.block.belwithstone.BelwithPillarBlock;
import birsy.clinker.common.block.belwithstone.CyclopeanBelwithBricksBlock;
import birsy.clinker.common.block.belwithstone.MonolithicBelwithBricksBlock;
import birsy.clinker.common.block.bramble.*;
import birsy.clinker.common.block.driedsoulsand.ChiseledDrySoulsandBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandFenceBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandPillarBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandSlabBlock;
import birsy.clinker.common.block.driedsoulsand.DriedSoulsandStairsBlock;
import birsy.clinker.common.block.HeaterBlock;
import birsy.clinker.common.block.mitesoil.MitesoilDiffuserBlock;
import birsy.clinker.common.block.riekplant.RiekPlantBlock;
import birsy.clinker.common.block.riekplant.RiekTubeBlock;
import birsy.clinker.common.block.riekplant.RiekVinesBlock;
import birsy.clinker.common.block.silt.WittlebulbBlock;
import birsy.clinker.common.block.trees.LocustTree;
import birsy.clinker.core.Clinker;
import net.minecraft.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.Direction;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

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
	public static final RegistryObject<LiquidBlock> BRINE = createBlock("brine", () -> new LiquidBlock(
			ClinkerFluids.BRINE_SOURCE, BlockBehaviour.Properties.of(Material.WATER)), null);


	//Material Blocks
	public static final RegistryObject<Block> LEAD_BLOCK = createBlock("lead_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).strength(5.0f, 6.0f).sound(SoundType.NETHERITE_BLOCK).harvestLevel(1).harvestTool(ToolType.PICKAXE)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> RAW_LEAD_BLOCK = createBlock("raw_lead_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).strength(2.5f, 3.0f).sound(SoundType.ANCIENT_DEBRIS).harvestLevel(1).harvestTool(ToolType.PICKAXE)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> SULFUR_BLOCK = createBlock("sulfur_block", MaterialBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> IRON_PYRITE_BLOCK = createBlock("iron_pyrite_block", MetalMaterialBlock::new, Clinker.CLINKER_BLOCKS);
	
	//Soils
	public static final RegistryObject<Block> ASH = createBlock("ash", () -> new AshBlock(Block.Properties.of(Material.SNOW, MaterialColor.COLOR_GRAY).strength(0.5F).sound(SoundType.SNOW).harvestTool(ToolType.SHOVEL).randomTicks()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ASH_LAYER = createBlock("ash_layers", AshLayerBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ROOTED_ASH = createBlock("rooted_ash", () -> new RootedAshBlock(ASH.get()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> PACKED_ASH = createBlock("packed_ash", () -> new Block(Block.Properties.of(Material.CLAY, MaterialColor.COLOR_GRAY).strength(0.5F).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ROCKY_PACKED_ASH = createBlock("rocky_packed_ash", () -> new Block(Block.Properties.of(Material.CLAY, MaterialColor.COLOR_GRAY).strength(0.5F).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> ROOTED_PACKED_ASH = createBlock("rooted_packed_ash", () -> new RootedAshBlock(PACKED_ASH.get()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> SALT_BLOCK = createBlock("salt_block", () -> new SandBlock(14406560, BlockBehaviour.Properties.of(Material.SAND, MaterialColor.TERRACOTTA_LIGHT_GRAY).strength(0.5F).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL)), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> ROOTSTALK = createBlock("rootstalk", () -> new Block(BlockBehaviour.Properties.of(Material.GRASS, MaterialColor.TERRACOTTA_ORANGE).harvestTool(ToolType.HOE).strength(0.1F).sound(SoundType.WART_BLOCK)), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> SILT_BLOCK = createBlock("silt_block", () -> new Block(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.TERRACOTTA_GREEN).harvestTool(ToolType.SHOVEL).strength(0.1F).sound(SoundType.WET_GRASS)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> WITTLEBULB =          createBlock("wittlebulb",          () -> new WittlebulbBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.TERRACOTTA_GREEN).instabreak().noOcclusion().noCollission().sound(SoundType.WET_GRASS)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BLOOMING_WITTLEBULB = createBlock("blooming_wittlebulb", () -> new WittlebulbBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.TERRACOTTA_GREEN).instabreak().noOcclusion().noCollission().sound(SoundType.WET_GRASS).lightLevel((state) -> 8)), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> MITESOIL_DIFFUSER = createBlock("mitesoil_diffuser", MitesoilDiffuserBlock::new, Clinker.CLINKER_BLOCKS);

	//public static final RegistryObject<Block> SILTSCAR_VINE = createBlock("siltscar_vine", () -> new SiltscarVineBlock(AbstractBlock.Properties.create(Material.PLANTS, MaterialColor.GREEN_TERRACOTTA).hardnessAndResistance(0.3F).notSolid().doesNotBlockMovement().sound(SoundType.WET_GRASS)), Clinker.CLINKER_BLOCKS);
	//public static final RegistryObject<Block> SILTSCAR_VINE_MOUTH = createBlock("siltscar_vine_mouth", () -> new SiltscarVineMouthBlock(AbstractBlock.Properties.create(Material.PLANTS, MaterialColor.GREEN_TERRACOTTA).hardnessAndResistance(0.3F).notSolid().doesNotBlockMovement().sound(SoundType.WET_GRASS)), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> HEATED_IRON_CAULDRON = createBlock("heated_iron_cauldron", HeatedIronCauldronBlock::new, null);
	public static final RegistryObject<Block> HEATER = createBlock("heater", HeaterBlock::new, Clinker.CLINKER_BLOCKS);

	//Brimstone
	public static BlockBehaviour.Properties getBrimstoneProperties() {
		return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_BROWN)
				  .strength(2.75F, 75.0F)
				  .sound(SoundType.BASALT)
				  .harvestLevel(1)
				  .harvestTool(ToolType.PICKAXE);
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
				.sound(SoundType.NETHER_BRICKS)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE);
	}

	public static final RegistryObject<Block> CAPSTONE = createBlock("capstone", () -> new Block(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CAPSTONE_SLAB = createBlock("capstone_slab", () -> new SlabBlock(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CAPSTONE_STAIRS = createBlock("capstone_stairs", () -> new StairBlock(() -> CAPSTONE.get().defaultBlockState(), getCapstoneProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> CAPSTONE_WALL = createBlock("capstone_wall", () -> new WallBlock(getCapstoneProperties()), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> CAPSTONE_SULFUR__ORE = createBlock("capstone_sulfur_ore", () -> new OreClinkerBlock(1.5F, 6.0F, 2, 2, 3, SoundType.NETHER_BRICKS), Clinker.CLINKER_BLOCKS);

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
			.sound(SoundType.GLASS)
			.harvestLevel(1)
			.harvestTool(ToolType.PICKAXE)), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> SULFUR_ROCK_BLOCK = createBlock("sulfur_rock_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.GOLD)
			.strength(1.5F, 6.0F)
			.sound(SoundType.GILDED_BLACKSTONE)
			.harvestLevel(1)
			.harvestTool(ToolType.PICKAXE)), Clinker.CLINKER_BLOCKS);


	//Scorstone
	public static BlockBehaviour.Properties getScorstoneProperties() {
		return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_LIGHT_GRAY)
				  .strength(1.5F, 6.0F)
				  .sound(SoundType.BASALT)
				  .harvestLevel(1)
				  .harvestTool(ToolType.PICKAXE);
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
	public static BlockBehaviour.Properties getShaleProperties() {
		return BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_LIGHT_BLUE)
				  .strength(2.75F, 10.0F)
				  .sound(SoundType.ANCIENT_DEBRIS)
				  .harvestLevel(1)
				  .harvestTool(ToolType.PICKAXE);
	}
	
	public static final RegistryObject<Block> SHALE = createBlock("shale", () -> new Block(getShaleProperties()), Clinker.CLINKER_BLOCKS);
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
	public static BlockBehaviour.Properties getLocustWoodProperties() {
		return BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
				.strength(2.0F, 3.0F)
				.sound(SoundType.WOOD);
	}
	
	public static final RegistryObject<Block> LOCUST_LOG = createBlock("locust_log", PoisonLogBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> TRIMMED_LOCUST_LOG = createBlock("trimmed_locust_log", PoisonLogBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> STRIPPED_LOCUST_LOG = createBlock("stripped_locust_log", 
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, (state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.PLANT : MaterialColor.STONE).strength(2.0F).sound(SoundType.WOOD)), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_LEAVES = createBlock("locust_leaves", 
			() -> new LeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.TERRACOTTA_ORANGE).strength(0.2F).randomTicks().sound(SoundType.ROOTS).noOcclusion()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_SAPLING = createBlock("locust_sapling",
			() -> new SaplingBlock(new LocustTree(), BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)), Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> LOCUST_PLANKS = createBlock("locust_planks", () -> new Block(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_STAIRS = createBlock("locust_stairs", () -> new StairBlock(() -> ClinkerBlocks.LOCUST_PLANKS.get().defaultBlockState(), getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_SLAB = createBlock("locust_slab", () -> new SlabBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> LOCUST_FENCE = createBlock("locust_fence", () -> new FenceBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_FENCE_GATE = createBlock("locust_fence_gate", () -> new FenceGateBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_DOOR = createBlock("locust_door", () -> new DoorBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_TRAPDOOR = createBlock("locust_trapdoor", () -> new TrapDoorBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_BUTTON = createBlock("locust_button", () -> new WoodButtonBlock(getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> LOCUST_PRESSURE_PLATE = createBlock("locust_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.MOBS, getLocustWoodProperties()), Clinker.CLINKER_BLOCKS);
	
	
	//Plants
	public static final RegistryObject<Block> BRAMBLE = createBlock("bramble", BrambleBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRAMBLE_VINES = createBlock("bramble_vines", BrambleVinesBlock::new, null);
	public static final RegistryObject<Block> BRAMBLE_VINES_TOP = createBlock("bramble_vines_top", BrambleVinesTopBlock::new, null);
	public static final RegistryObject<Block> BRAMBLE_ROOTS = createBlock("bramble_roots", BrambleRootsBlock::new, null);
	public static final RegistryObject<Block> BRAMBLE_ROOTS_BOTTOM = createBlock("bramble_roots_bottom", BrambleRootsBottomBlock::new, null);

	public static final RegistryObject<Block> BRAMBOO_LOG = createBlock("bramboo_log", BrambooLogBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> BRAMBOO_STALK = createBlock("bramboo_stalk", BrambooStalkBlock::new, Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> THORN_LOG = createBlock("thorn_log", ThornLogConnectedBlock::new, Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> STRIPPED_THORN_LOG = createBlock("stripped_thorn_log", ThornLogConnectedBlock::new, Clinker.CLINKER_BLOCKS);
	
	public static final RegistryObject<Block> ROOT_GRASS = createBlock("root_grass", RootGrassBlock::new, Clinker.CLINKER_BLOCKS);

	public static final RegistryObject<Block> CAVE_MOSS = createBlock("cave_moss", CaveMossBlock::new, Clinker.CLINKER_BLOCKS);


	//Riek Plant
	public static final RegistryObject<Block> RIEK_PLANT = createBlock("riek_plant", RiekPlantBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> RIEK_TUBE = createBlock("riek_tube", RiekTubeBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> FERTILE_RIEK_PLANT = createBlock("fertile_riek_plant", RiekPlantBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> RIEK_VINES = createBlock("riek_vines", RiekVinesBlock::new, Clinker.CLINKER_BLOCKS);
	public static final RegistryObject<Block> FERTILE_RIEK_VINES = createBlock("fertile_riek_vines", RiekVinesBlock::new, Clinker.CLINKER_BLOCKS);
	
	
	//Special
	public static final RegistryObject<Block> FOUL_AIR = createBlock("foul_air", FoulAirBlock::new, null);
	public static final RegistryObject<Block> DUST_STALAGMITE = createBlock("dust_stalagmite", DustStalagmiteBlock::new, null);

	public static final RegistryObject<Block> SOUL_WELL = createBlock("soul_well", SoulWellBlock::new, Clinker.CLINKER_BLOCKS);

	public static <B extends Block> RegistryObject<B> createBlock(String name, final Supplier<? extends B> supplier, @Nullable CreativeModeTab group) {
		RegistryObject<B> block = BLOCKS.register(name, supplier);
		if (group != null) {
			ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(group)));
		}
		return block;
	}
}
