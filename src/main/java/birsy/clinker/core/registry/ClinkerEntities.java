package birsy.clinker.core.registry;

import birsy.clinker.client.render.entity.*;
import birsy.clinker.client.render.entity.model.*;
import birsy.clinker.client.render.world.item.AlchemyBookRenderer;
import birsy.clinker.common.entity.*;
import birsy.clinker.common.entity.Salamander.SalamanderBodyEntity;
import birsy.clinker.common.entity.Salamander.SalamanderHeadEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Clinker.MOD_ID);

    public static final RegistryObject<EntityType<MudScarabEntity>> MUD_SCARAB = ENTITIES.register("mud_scarab", () ->
            EntityType.Builder.of(MudScarabEntity::new, MobCategory.MONSTER)
            .sized(2.0F, 1.9F)
            .build(new ResourceLocation(Clinker.MOD_ID, "mud_scarab").toString()));

    public static final RegistryObject<EntityType<SalamanderHeadEntity>> SALAMANDER_HEAD = ENTITIES.register("salamander_head", () ->
            EntityType.Builder.of(SalamanderHeadEntity::new, MobCategory.MONSTER).fireImmune()
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "salamander_head").toString()));
    public static final RegistryObject<EntityType<SalamanderBodyEntity>> SALAMANDER_BODY = ENTITIES.register("salamander_body", () ->
            EntityType.Builder.of(SalamanderBodyEntity::new, MobCategory.MONSTER).fireImmune()
                    .sized(1.0F, 1.0F)
                    .noSave().noSummon()
                    .build(new ResourceLocation(Clinker.MOD_ID, "salamander_body").toString()));

    public static final RegistryObject<EntityType<SeaHagEntity>> SEA_HAG = ENTITIES.register("sea_hag", () ->
            EntityType.Builder.of(SeaHagEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 6.4F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "sea_hag").toString()));

    public static final RegistryObject<EntityType<GnomadAxemanEntity>> GNOMAD_AXEMAN = ENTITIES.register("gnomad_axeman", () ->
            EntityType.Builder.of(GnomadAxemanEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.5f)
                    .build(new ResourceLocation(Clinker.MOD_ID, "gnomad_axeman").toString()));

    @SubscribeEvent
    public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
        event.put(MUD_SCARAB.get(), MudScarabEntity.createAttributes().build());
        event.put(SALAMANDER_HEAD.get(), SalamanderHeadEntity.createAttributes().build());
        event.put(SALAMANDER_BODY.get(), SalamanderBodyEntity.createAttributes().build());
        event.put(SEA_HAG.get(), SeaHagEntity.createAttributes().build());
        event.put(GNOMAD_AXEMAN.get(), SeaHagEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ClinkerEntities.MUD_SCARAB.get(), MudScarabRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.SALAMANDER_HEAD.get(), SalamanderHeadRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.SALAMANDER_BODY.get(), SalamanderBodyRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.SEA_HAG.get(), SeaHagRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.GNOMAD_AXEMAN.get(), GnomadAxemanRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SalamanderHeadModel.LAYER_LOCATION, SalamanderHeadModel::createBodyLayer);
        event.registerLayerDefinition(SalamanderBodyModel.LAYER_LOCATION, SalamanderBodyModel::createBodyLayer);
        event.registerLayerDefinition(SalamanderTailModel.LAYER_LOCATION, SalamanderTailModel::createBodyLayer);

        event.registerLayerDefinition(MudScarabModel.LAYER_LOCATION, MudScarabModel::createBodyLayer);
        event.registerLayerDefinition(SeaHagModel.LAYER_LOCATION, SeaHagModel::createBodyLayer);
        event.registerLayerDefinition(GnomadAxemanModel.LAYER_LOCATION, GnomadAxemanModel::createBodyLayer);
    }
}
