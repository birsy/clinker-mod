package birsy.clinker.core.registry;

import birsy.clinker.common.tileentity.SiltscarTileEntity;
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
}
