package birsy.clinker.core.registry;

import birsy.clinker.common.tileentity.HeatedIronCauldronTileEntity;
import birsy.clinker.common.tileentity.MitesoilDiffuserTileEntity;
import birsy.clinker.common.tileentity.SiltscarTileEntity;
import birsy.clinker.common.tileentity.SoulWellTileEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerTileEntities
{
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Clinker.MOD_ID);
	
	public static void init()
	{
		TILE_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	//public static final RegistryObject<TileEntityType<SiltscarTileEntity>> SILTSCAR_MOUTH = TILE_ENTITY_TYPES.register("siltscar_mouth", () -> TileEntityType.Builder.create(SiltscarTileEntity::new, ClinkerBlocks.SILTSCAR_VINE_MOUTH.get()).build(null));
	public static final RegistryObject<TileEntityType<HeatedIronCauldronTileEntity>> HEATED_IRON_CAULDRON = TILE_ENTITY_TYPES.register("heated_iron_cauldron", () -> TileEntityType.Builder.create(HeatedIronCauldronTileEntity::new, ClinkerBlocks.HEATED_IRON_CAULDRON.get()).build(null));
	public static final RegistryObject<TileEntityType<MitesoilDiffuserTileEntity>> MITESOIL_DIFFUSER = TILE_ENTITY_TYPES.register("mitesoil_diffuser", () -> TileEntityType.Builder.create(MitesoilDiffuserTileEntity::new, ClinkerBlocks.MITESOIL_DIFFUSER.get()).build(null));
	public static final RegistryObject<TileEntityType<SoulWellTileEntity>> SOUL_WELL = TILE_ENTITY_TYPES.register("soul_well", () -> TileEntityType.Builder.create(SoulWellTileEntity::new, ClinkerBlocks.SOUL_WELL.get()).build(null));
}
