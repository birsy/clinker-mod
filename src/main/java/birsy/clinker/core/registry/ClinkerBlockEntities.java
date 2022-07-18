package birsy.clinker.core.registry;

import birsy.clinker.client.render.world.blockentity.FermentationBarrelRenderer;
import birsy.clinker.common.blockentity.FermentationBarrelBlockEntity;
import birsy.clinker.core.Clinker;
import com.ibm.icu.impl.Pair;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
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
    private static List<Pair<EntityType<?>, EntityRenderer<?>>> entityTypes = new ArrayList<>();
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Clinker.MOD_ID);

    public static final RegistryObject<BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL = BLOCK_ENTITIES.register("fermentation_barrel", () -> BlockEntityType.Builder.of(FermentationBarrelBlockEntity::new, ClinkerBlocks.FERMENTATION_BARREL.get()).build(null));

    public static void registerTileEntityRenderers() {
        BlockEntityRenderers.register(ClinkerBlockEntities.FERMENTATION_BARREL.get(), FermentationBarrelRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FermentationBarrelRenderer.LAYER_LOCATION, FermentationBarrelRenderer::createBodyLayer);
    }
}
