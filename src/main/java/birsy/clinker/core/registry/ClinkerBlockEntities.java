package birsy.clinker.core.registry;

import birsy.clinker.client.render.world.blockentity.*;
import birsy.clinker.common.world.block.blockentity.CounterBlockEntity;
import birsy.clinker.common.world.block.blockentity.StoveBlockEntity;
import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.common.world.block.blockentity.FermentationBarrelBlockEntity;
import birsy.clinker.common.world.block.blockentity.SarcophagusBlockEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Clinker.MOD_ID);

    public static final Supplier<BlockEntityType<SarcophagusBlockEntity>> SARCOPHAGUS_INNARDS = BLOCK_ENTITY_TYPES.register("sarcophagus_innards",
            () -> BlockEntityType.Builder.of(SarcophagusBlockEntity::new, ClinkerBlocks.BLANK_SARCOPHAGUS.get()).build(null));

    public static final Supplier<BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL = BLOCK_ENTITY_TYPES.register("fermentation_barrel",
            () -> BlockEntityType.Builder.of(FermentationBarrelBlockEntity::new, ClinkerBlocks.FERMENTATION_BARREL.get()).build(null));

    public static final Supplier<BlockEntityType<CounterBlockEntity>> COUNTER = BLOCK_ENTITY_TYPES.register("counter",
            () -> BlockEntityType.Builder.of(CounterBlockEntity::new, ClinkerBlocks.COUNTER.get()).build(null));

    public static final Supplier<BlockEntityType<StoveBlockEntity>> STOVE = BLOCK_ENTITY_TYPES.register("stove",
            () -> BlockEntityType.Builder.of(StoveBlockEntity::new, ClinkerBlocks.STOVE.get()).build(null));

    public static final Supplier<BlockEntityType<FairyFruitBlockEntity>> FAIRY_FRUIT = BLOCK_ENTITY_TYPES.register("fairy_fruit",
            () -> BlockEntityType.Builder.of(FairyFruitBlockEntity::new, ClinkerBlocks.FAIRY_FRUIT_BLOCK.get()).build(null));

    public static void registerTileEntityRenderers() {
        BlockEntityRenderers.register(ClinkerBlockEntities.FERMENTATION_BARREL.get(), FermentationBarrelRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.SARCOPHAGUS_INNARDS.get(), SarcophagusInnardsRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.COUNTER.get(), CounterRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.STOVE.get(), StoveRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.FAIRY_FRUIT.get(), FairyFruitRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FermentationBarrelRenderer.LAYER_LOCATION, FermentationBarrelRenderer::createBodyLayer);

        event.registerLayerDefinition(StoveRenderer.StoveModel.LAYER_LOCATION, StoveRenderer.StoveModel::createBodyLayer);
        event.registerLayerDefinition(StoveRenderer.StoveChimneyModel.LAYER_LOCATION, StoveRenderer.StoveChimneyModel::createBodyLayer);
        event.registerLayerDefinition(StoveRenderer.DoubleStoveModel.LAYER_LOCATION, StoveRenderer.DoubleStoveModel::createBodyLayer);
        event.registerLayerDefinition(StoveRenderer.DoubleStoveChimneyModel.LAYER_LOCATION, StoveRenderer.DoubleStoveChimneyModel::createBodyLayer);

    }
}
