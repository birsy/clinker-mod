package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.tileentity.TileEntityType;
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
	
	//public static final RegistryObject<TileEntityType<GemstoneTileEntity>> GEMSTONE = TILE_ENTITY_TYPES.register("gemstone", () -> TileEntityType.Builder.create(GemstoneTileEntity::new, ClinkerBlocks.GEMSTONE.get()).build(null));
}
