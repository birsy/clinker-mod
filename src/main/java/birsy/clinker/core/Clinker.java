package birsy.clinker.core;


import birsy.clinker.core.registry.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerEntities;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("deprecation")
@Mod(Clinker.MOD_ID)
public class Clinker
{
	public static final String MOD_ID = "clinker";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());
	
	public Clinker() throws InterruptedException {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ClinkerSounds.SOUNDS.register(modEventBus);
        ClinkerItems.ITEMS.register(modEventBus);
        ClinkerBlocks.BLOCKS.register(modEventBus);
        ClinkerBlocks.ITEMS.register(modEventBus);
        ClinkerEntities.ENTITY_TYPES.register(modEventBus);
        
        ClinkerWorldCarvers.WORLD_CARVERS.register(modEventBus);
        ClinkerSurfaceBuilders.SURFACE_BUILDERS.register(modEventBus);
        ClinkerFeatures.FEATURES.register(modEventBus);
        ClinkerBiomes.BIOMES.register(modEventBus);
        
        
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::doClientStuff);
        
        MinecraftForge.EVENT_BUS.register(this);
    }
	
	private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            ClinkerEntities.setup();
            ClinkerConfiguredFeatures.registerConfiguredFeatures();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.THORN_LOG.get(), RenderType.getCutout());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.STRIPPED_THORN_LOG.get(), RenderType.getCutout());

    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.ROOTSTALK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ClinkerBlocks.ROOT_GRASS.get(), RenderType.getCutoutMipped());

        RenderTypeLookup.setRenderLayer(ClinkerBlocks.LOCUST_LOG.get(), RenderType.getCutout());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.LOCUST_LEAVES.get(), RenderType.getCutoutMipped());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.LOCUST_DOOR.get(), RenderType.getCutout());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.LOCUST_SAPLING.get(), RenderType.getCutout());
    	
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.BRAMBLE.get(), RenderType.getCutoutMipped());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.BRAMBLE_VINES.get(), RenderType.getCutoutMipped());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.BRAMBLE_VINES_TOP.get(), RenderType.getCutoutMipped());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.BRAMBLE_ROOTS.get(), RenderType.getCutoutMipped());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.BRAMBLE_ROOTS_BOTTOM.get(), RenderType.getCutoutMipped());
    	
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.RIEK_TUBE.get(), RenderType.getCutout());
    	//RenderTypeLookup.setRenderLayer(ClinkerBlocks.RIEK_FRUIT.get(), RenderType.getCutout());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.RIEK_VINES.get(), RenderType.getCutout());
    	RenderTypeLookup.setRenderLayer(ClinkerBlocks.FERTILE_RIEK_VINES.get(), RenderType.getCutout());
    	
    	//mensionRenderInfo.field_239208_a_.put(ClinkerDimensions.OTHERSHORE, new OthershoreDimensionRenderInfo());
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = Clinker.MOD_ID)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onRegisterBiomes(final RegistryEvent.Register<Biome> event) {
            ClinkerBiomes.registerBiomes();
        }
    }

    public static final ItemGroup CLINKER_MISC = new ItemGroup("clinkerItems")
    {
        @Override
        public ItemStack createIcon()
        {
        	return new ItemStack(ClinkerItems.SULFUR.get());
        }
    };
    
    public static final ItemGroup CLINKER_BLOCKS = new ItemGroup("clinkerBlocks")
    {
        @Override
        public ItemStack createIcon()
        {
        	return new ItemStack(ClinkerBlocks.BRIMSTONE_BRICKS.get().asItem());
        }
    };
    
    public static final ItemGroup CLINKER_TOOLS = new ItemGroup("clinkerTools")
    {
        @Override
        public ItemStack createIcon()
        {
        	return new ItemStack(ClinkerItems.LEAD_SWORD.get());
        }
    };
    
    public static final ItemGroup CLINKER_FOOD = new ItemGroup("clinkerFood")
    {
        @Override
        public ItemStack createIcon()
        {
        	return new ItemStack(ClinkerItems.GNOMEAT_JERKY.get());
        }
    };
}
