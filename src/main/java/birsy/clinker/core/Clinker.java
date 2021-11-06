package birsy.clinker.core;


<<<<<<< Updated upstream
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.ClinkerSounds;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
=======
<<<<<<< Updated upstream
import birsy.clinker.client.render.tileentity.*;
import birsy.clinker.client.render.world.OthershoreDimensionRenderInfo;
import birsy.clinker.core.registry.*;
import birsy.clinker.core.registry.world.*;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

=======
import birsy.clinker.common.alchemy.chemicals.Element;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.ClinkerSounds;
import birsy.clinker.core.registry.alchemy.ClinkerElements;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
>>>>>>> Stashed changes
>>>>>>> Stashed changes
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
<<<<<<< Updated upstream
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
=======
<<<<<<< Updated upstream

import java.util.Map;
=======
import net.minecraftforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
>>>>>>> Stashed changes
>>>>>>> Stashed changes

@Mod(Clinker.MOD_ID)
public class Clinker
{
	public static final String MOD_ID = "clinker";
	public static boolean devmode = true;

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());
	
	public Clinker() throws InterruptedException {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ClinkerSounds.SOUNDS.register(modEventBus);
        ClinkerItems.ITEMS.register(modEventBus);
        ClinkerBlocks.BLOCKS.register(modEventBus);
        ClinkerBlocks.ITEMS.register(modEventBus);
<<<<<<< Updated upstream
=======
<<<<<<< Updated upstream
        ClinkerEntities.ENTITY_TYPES.register(modEventBus);
        ClinkerTileEntities.TILE_ENTITY_TYPES.register(modEventBus);
=======
        ClinkerElements.ELEMENTS.register(modEventBus);
>>>>>>> Stashed changes
>>>>>>> Stashed changes

		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::doClientStuff);
        
        MinecraftForge.EVENT_BUS.register(this);
    }
	
	private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            AxeItem.STRIPPABLES.put(ClinkerBlocks.LOCUST_LOG.get(), ClinkerBlocks.STRIPPED_LOCUST_LOG.get());
            AxeItem.STRIPPABLES.put(ClinkerBlocks.SWAMP_ASPEN_LOG.get(), ClinkerBlocks.STRIPPED_SWAMP_ASPEN_LOG.get());
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.LOCUST_LOG.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.LOCUST_LEAVES.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.LOCUST_DOOR.get(), RenderType.cutout());
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = Clinker.MOD_ID)
    public static class RegistryEvents {
    }

    public static final CreativeModeTab CLINKER_MISC = new CreativeModeTab("clinkerItems")
    {
        @Override
        public ItemStack makeIcon()
        {
        	return new ItemStack(ClinkerItems.SULFUR.get());
        }
    };
    
    public static final CreativeModeTab CLINKER_BLOCKS = new CreativeModeTab("clinkerBlocks")
    {
        @Override
        public ItemStack makeIcon()
        {
        	return new ItemStack(ClinkerBlocks.BRIMSTONE_BRICKS.get().asItem());
        }
    };
    
    public static final CreativeModeTab CLINKER_TOOLS = new CreativeModeTab("clinkerTools")
    {
        @Override
        public ItemStack makeIcon()
        {
        	return new ItemStack(ClinkerItems.LEAD_SWORD.get());
        }
    };
    
    public static final CreativeModeTab CLINKER_FOOD = new CreativeModeTab("clinkerFood")
    {
        @Override
        public ItemStack makeIcon()
        {
        	return new ItemStack(ClinkerItems.GNOMEAT_JERKY.get());
        }
    };
}
