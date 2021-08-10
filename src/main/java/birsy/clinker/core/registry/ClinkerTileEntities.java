package birsy.clinker.core.registry;

import birsy.clinker.common.tileentity.*;
import birsy.clinker.core.Clinker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerTileEntities
{
	public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Clinker.MOD_ID);
	
	public static void init()
	{
		TILE_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	//public static final RegistryObject<TileEntityType<SiltscarTileEntity>> SILTSCAR_MOUTH = TILE_ENTITY_TYPES.register("siltscar_mouth", () -> TileEntityType.Builder.create(SiltscarTileEntity::new, ClinkerBlocks.SILTSCAR_VINE_MOUTH.get()).build(null));
	public static final RegistryObject<BlockEntityType<HeatedIronCauldronTileEntity>> HEATED_IRON_CAULDRON = TILE_ENTITY_TYPES.register("heated_iron_cauldron", () -> BlockEntityType.Builder.of(HeatedIronCauldronTileEntity::new, ClinkerBlocks.HEATED_IRON_CAULDRON.get()).build(null));
	public static final RegistryObject<BlockEntityType<HeaterTileEntity>> HEATER = TILE_ENTITY_TYPES.register("heater", () -> BlockEntityType.Builder.of(HeaterTileEntity::new, ClinkerBlocks.HEATER.get()).build(null));

	public static final RegistryObject<BlockEntityType<MitesoilDiffuserTileEntity>> MITESOIL_DIFFUSER = TILE_ENTITY_TYPES.register("mitesoil_diffuser", () -> BlockEntityType.Builder.of(MitesoilDiffuserTileEntity::new, ClinkerBlocks.MITESOIL_DIFFUSER.get()).build(null));
	public static final RegistryObject<BlockEntityType<SoulWellTileEntity>> SOUL_WELL = TILE_ENTITY_TYPES.register("soul_well", () -> BlockEntityType.Builder.of(SoulWellTileEntity::new, ClinkerBlocks.SOUL_WELL.get()).build(null));
}
