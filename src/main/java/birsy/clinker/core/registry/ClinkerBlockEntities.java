package birsy.clinker.core.registry;

import birsy.clinker.client.render.world.blockentity.CounterRenderer;
import birsy.clinker.client.render.world.blockentity.FermentationBarrelRenderer;
import birsy.clinker.client.render.world.blockentity.SarcophagusInnardsRenderer;
import birsy.clinker.common.blockentity.CounterBlockEntity;
import birsy.clinker.common.blockentity.FermentationBarrelBlockEntity;
import birsy.clinker.common.blockentity.SarcophagusBlockEntity;
import birsy.clinker.core.Clinker;
import com.ibm.icu.impl.Pair;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Clinker.MOD_ID);

    public static final RegistryObject<BlockEntityType<SarcophagusBlockEntity>> SARCOPHAGUS_INNARDS = BLOCK_ENTITY_TYPES.register("sarcophagus_innards",
            () -> BlockEntityType.Builder.of(SarcophagusBlockEntity::new, ClinkerBlocks.BLANK_SARCOPHAGUS.get()).build(null));

    public static final RegistryObject<BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL = BLOCK_ENTITY_TYPES.register("fermentation_barrel",
            () -> BlockEntityType.Builder.of(FermentationBarrelBlockEntity::new, ClinkerBlocks.FERMENTATION_BARREL.get()).build(null));

    public static final RegistryObject<BlockEntityType<CounterBlockEntity>> COUNTER = BLOCK_ENTITY_TYPES.register("counter",
            () -> BlockEntityType.Builder.of(CounterBlockEntity::new, ClinkerBlocks.COUNTER.get()).build(null));

    public static void registerTileEntityRenderers() {
        BlockEntityRenderers.register(ClinkerBlockEntities.FERMENTATION_BARREL.get(), FermentationBarrelRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.SARCOPHAGUS_INNARDS.get(), SarcophagusInnardsRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.COUNTER.get(), CounterRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FermentationBarrelRenderer.LAYER_LOCATION, FermentationBarrelRenderer::createBodyLayer);
    }
}
