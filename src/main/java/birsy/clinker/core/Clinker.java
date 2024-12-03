package birsy.clinker.core;

import birsy.clinker.client.render.GUIRenderer;
import birsy.clinker.client.gui.AlchemyBundleGUIRenderer;
import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.core.registry.*;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import birsy.clinker.core.registry.world.ClinkerFeatures;
import birsy.clinker.core.registry.world.ClinkerWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.RenderType;

@Mod(Clinker.MOD_ID)
public class Clinker {
    public static final String MOD_ID = "clinker";
    public static boolean devmode = true;
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());

    public Clinker(IEventBus modEventBus) throws InterruptedException {
        ClinkerSounds.SOUNDS.register(modEventBus);
        ClinkerItems.ITEMS.register(modEventBus);
        ClinkerBlocks.BLOCKS.register(modEventBus);
        ClinkerBlocks.BLOCK_ITEMS.register(modEventBus);
        ClinkerWorld.CHUNK_GENERATORS.register(modEventBus);
        ClinkerBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);

        ClinkerMemoryModules.MEMORY_MODULE_TYPES.register(modEventBus);
        ClinkerSensors.SENSOR_TYPES.register(modEventBus);
        ClinkerEntities.ENTITY_TYPES.register(modEventBus);

        ClinkerFeatures.FEATURES.register(modEventBus);
        ClinkerParticles.PARTICLES.register(modEventBus);
        ClinkerDataAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ClinkerCreativeModeTabs.TABS.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);
    }

    public static ResourceLocation resource(String resource) {
        return ResourceLocation.fromNamespaceAndPath(Clinker.MOD_ID, resource);
    }

    private void setup(final FMLCommonSetupEvent event) {
//        DispenserBlock.registerBehavior(ClinkerItems.ORDNANCE.get(), new ProjectileDispenseBehavior(ClinkerItems.ORDNANCE.get()));
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ClinkerDebugRenderers.initialize();
        ClinkerBlockEntities.registerTileEntityRenderers();

        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.SHORT_MUD_REEDS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.MUD_REEDS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.TALL_MUD_REEDS.get(), RenderType.cutout());

        GUIRenderer.alchemyBundleGUIRenderer = new AlchemyBundleGUIRenderer(Minecraft.getInstance());
    }
}
