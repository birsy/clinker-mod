package birsy.clinker.core;

import birsy.clinker.client.render.GUIRenderer;
import birsy.clinker.client.gui.AlchemyBundleGUIRenderer;
import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.world.level.chunk.gen.OthershoreChunkGenerator;
import birsy.clinker.common.world.level.chunk.gen.TestChunkGenerator;
import birsy.clinker.core.registry.*;
import birsy.clinker.core.registry.world.ClinkerBiomeTest;
import birsy.clinker.core.registry.world.ClinkerFeatures;
import birsy.clinker.core.registry.world.ClinkerWorld;
import birsy.clinker.core.util.ClinkerFontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Clinker.MOD_ID)
public class Clinker
{
	public static final String MOD_ID = "clinker";
	public static boolean devmode = true;
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());
    public static ClinkerFontManager fontManager;

	public Clinker() throws InterruptedException {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ClinkerPacketHandler.register();
        ClinkerSounds.SOUNDS.register(modEventBus);
        ClinkerItems.ITEMS.register(modEventBus);
        ClinkerBlocks.BLOCKS.register(modEventBus);
        ClinkerBlocks.BLOCK_ITEMS.register(modEventBus);
        ClinkerWorld.CHUNK_GENERATORS.register(modEventBus);
        ClinkerBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ClinkerEntities.ENTITY_TYPES.register(modEventBus);
        ClinkerFeatures.FEATURES.register(modEventBus);
        //ClinkerElements.ELEMENTS.register(modEventBus);
        ClinkerParticles.PARTICLES.register(modEventBus);

        //TODO : STRUCTURES. LOOK INTO WAVE FUNCTION COLLAPSE?

        ClinkerBiomeTest.BIOMES.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);

    }
	
	private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            OthershoreChunkGenerator.register();
            TestChunkGenerator.register();
            //AxeItem.STRIPPABLES.put(ClinkerBlocks.LOCUST_LOG.get(), ClinkerBlocks.STRIPPED_LOCUST_LOG.get());
            //AxeItem.STRIPPABLES.put(ClinkerBlocks.SWAMP_ASPEN_LOG.get(), ClinkerBlocks.STRIPPED_SWAMP_ASPEN_LOG.get());
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ClinkerBlockEntities.registerTileEntityRenderers();

        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.LOCUST_LOG.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.LOCUST_LEAVES.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.LOCUST_DOOR.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.SHORT_MUD_REEDS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.MUD_REEDS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.TALL_MUD_REEDS.get(), RenderType.cutout());

        fontManager = new ClinkerFontManager(Minecraft.getInstance());
        GUIRenderer.alchemyBundleGUIRenderer = new AlchemyBundleGUIRenderer(Minecraft.getInstance());
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

    /*

       the, elder, scrolls,
       klaatu, berata, niktu, xyzzy,
       bless, curse,
       light, darkness,
       fire, air, earth, water,
       hot, dry, cold, wet,
       ignite, snuff,
       embiggen, twist, shorten, stretch,
       fiddle, destroy,
       imbue,
       galvanize,
       enchant,
       free, limited,
       range, of,
       towards, inside,
       sphere, cube, self, other, ball,
       mental, physical,
       grow, shrink,
       demon, elemental, spirit, animal, creature, beast, humanoid, undead,
       fresh, stale,
       phnglui, mglwnafh,
       cthulhu, rlyeh,
       wgahnagl, fhtagn,
       baguette

     */
}
