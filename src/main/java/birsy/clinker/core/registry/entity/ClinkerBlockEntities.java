package birsy.clinker.core.registry.entity;

import birsy.clinker.client.render.world.blockentity.*;
import birsy.clinker.common.block.blockentity.*;
import birsy.clinker.common.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class ClinkerBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Clinker.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SarcophagusBlockEntity>> SARCOPHAGUS_INNARDS = BLOCK_ENTITY_TYPES.register("sarcophagus_innards",
            () -> BlockEntityType.Builder.of(SarcophagusBlockEntity::new, ClinkerBlocks.BLANK_SARCOPHAGUS.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL = BLOCK_ENTITY_TYPES.register("fermentation_barrel",
            () -> BlockEntityType.Builder.of(FermentationBarrelBlockEntity::new, ClinkerBlocks.FERMENTATION_BARREL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StoveBlockEntity>> STOVE = BLOCK_ENTITY_TYPES.register("stove",
            () -> BlockEntityType.Builder.of(StoveBlockEntity::new, ClinkerBlocks.STOVE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FairyFruitBlockEntity>> FAIRY_FRUIT = BLOCK_ENTITY_TYPES.register("fairy_fruit",
            () -> BlockEntityType.Builder.of(FairyFruitBlockEntity::new, ClinkerBlocks.FAIRY_FRUIT_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FulminaFlowerBlockEntity>> FULMINA_FLOWER = BLOCK_ENTITY_TYPES.register("fulmina_flower",
            () -> BlockEntityType.Builder.of(FulminaFlowerBlockEntity::new, ClinkerBlocks.FULMINA_FLOWER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MortarBlockEntity>> MORTAR = BLOCK_ENTITY_TYPES.register("mortar",
            () -> BlockEntityType.Builder.of(MortarBlockEntity::new, ClinkerBlocks.MORTAR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CounterBlockEntity>> COUNTER = BLOCK_ENTITY_TYPES.register("counter",
            () -> BlockEntityType.Builder.of(CounterBlockEntity::new, ClinkerBlocks.COUNTER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PressureCookerBlockEntity>> PRESSURE_COOKER = BLOCK_ENTITY_TYPES.register("pressure_cooker",
            () -> BlockEntityType.Builder.of(PressureCookerBlockEntity::new, ClinkerBlocks.PRESSURE_COOKER.get()).build(null));

    public static void registerTileEntityRenderers() {
        BlockEntityRenderers.register(ClinkerBlockEntities.FERMENTATION_BARREL.get(), FermentationBarrelRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.SARCOPHAGUS_INNARDS.get(), SarcophagusInnardsRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.STOVE.get(), StoveRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.FAIRY_FRUIT.get(), FairyFruitRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.MORTAR.get(), MortarRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.COUNTER.get(), CounterRenderer::new);
        BlockEntityRenderers.register(ClinkerBlockEntities.PRESSURE_COOKER.get(), PressureCookerRenderer::new);
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
