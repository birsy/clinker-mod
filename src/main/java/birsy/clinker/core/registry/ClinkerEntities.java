package birsy.clinker.core.registry;

import birsy.clinker.client.render.entity.MudScarabRenderer;
import birsy.clinker.client.render.entity.SeaHagRenderer;
import birsy.clinker.client.render.entity.model.MudScarabModel;
import birsy.clinker.client.render.entity.model.SeaHagModel;
import birsy.clinker.common.entity.MudScarabEntity;
import birsy.clinker.common.entity.SeaHagEntity;
import birsy.clinker.core.Clinker;
import com.ibm.icu.impl.Pair;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Clinker.MOD_ID);

    public static final RegistryObject<EntityType<MudScarabEntity>> MUD_SCARAB = ENTITIES.register("mud_scarab", () ->
            EntityType.Builder.of(MudScarabEntity::new, MobCategory.MONSTER)
            .sized(2.0F, 1.9F)
            .build(new ResourceLocation(Clinker.MOD_ID, "mud_scarab").toString()));

    public static final RegistryObject<EntityType<SeaHagEntity>> SEA_HAG = ENTITIES.register("sea_hag", () ->
            EntityType.Builder.of(SeaHagEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 6.4F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "sea_hag").toString()));

    @SubscribeEvent
    public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
        event.put(MUD_SCARAB.get(), MudScarabEntity.createAttributes().build());
        event.put(SEA_HAG.get(), SeaHagEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ClinkerEntities.MUD_SCARAB.get(), MudScarabRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.SEA_HAG.get(), SeaHagRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MudScarabModel.LAYER_LOCATION, MudScarabModel::createBodyLayer);
        event.registerLayerDefinition(SeaHagModel.LAYER_LOCATION, SeaHagModel::createBodyLayer);
    }
}
