package birsy.clinker.core;

import birsy.clinker.client.render.GUIRenderer;
import birsy.clinker.client.gui.AlchemyBundleGUIRenderer;
import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.world.entity.OrdnanceEntity;
import birsy.clinker.common.world.level.chunk.gen.OthershoreChunkGenerator;
import birsy.clinker.common.world.level.chunk.gen.TestChunkGenerator;
import birsy.clinker.core.registry.*;
import birsy.clinker.core.registry.world.ClinkerFeatures;
import birsy.clinker.core.registry.world.ClinkerWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

@Mod(Clinker.MOD_ID)
public class Clinker {
	public static final String MOD_ID = "clinker";
	public static boolean devmode = true;
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());

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
        ClinkerParticles.PARTICLES.register(modEventBus);
        ClinkerDataAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);

        NeoForge.EVENT_BUS.register(this);
    }
	
	private void setup(final FMLCommonSetupEvent event) {
        DispenserBlock.registerBehavior(ClinkerItems.ORDNANCE.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack item) {
                return OrdnanceEntity.create(level, position.x(), position.y(), position.z());
            }

            @Override
            protected void playSound(BlockSource pSource) {
                pSource.level().playSound(null, pSource.pos().getX(), pSource.pos().getY(), pSource.pos().getZ(), SoundEvents.TRIDENT_THROW, SoundSource.BLOCKS, 0.5F, 0.4F / (pSource.level().getRandom().nextFloat() * 0.4F + 0.8F));
            }
        });

        event.enqueueWork(() -> {
            OthershoreChunkGenerator.register();
            TestChunkGenerator.register();
            //AxeItem.STRIPPABLES.put(ClinkerBlocks.LOCUST_LOG.get(), ClinkerBlocks.STRIPPED_LOCUST_LOG.get());
            //AxeItem.STRIPPABLES.put(ClinkerBlocks.SWAMP_ASPEN_LOG.get(), ClinkerBlocks.STRIPPED_SWAMP_ASPEN_LOG.get());
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ClinkerBlockEntities.registerTileEntityRenderers();

        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.LOCUST_LOG.get(), RenderType.cutout());
        //ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.LOCUST_DOOR.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.SHORT_MUD_REEDS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.MUD_REEDS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ClinkerBlocks.TALL_MUD_REEDS.get(), RenderType.cutout());

        GUIRenderer.alchemyBundleGUIRenderer = new AlchemyBundleGUIRenderer(Minecraft.getInstance());
    }

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
