package birsy.clinker.core.registry;

import birsy.clinker.client.render.entity.*;
import birsy.clinker.client.render.entity.model.*;
import birsy.clinker.common.world.entity.LumberingAspenEntity;
import birsy.clinker.common.world.entity.MudScarabEntity;
import birsy.clinker.common.world.entity.salamander.NewSalamanderEntity;
import birsy.clinker.common.world.entity.salamander.SalamanderBodyEntity;
import birsy.clinker.common.world.entity.salamander.SalamanderHeadEntity;
import birsy.clinker.common.world.entity.SeaHagEntity;
import birsy.clinker.common.world.entity.gnomad.GnomadAxemanEntity;
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
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Clinker.MOD_ID);

    public static final RegistryObject<EntityType<MudScarabEntity>> MUD_SCARAB = ENTITY_TYPES.register("mud_scarab", () ->
            EntityType.Builder.of(MudScarabEntity::new, MobCategory.MONSTER)
            .sized(2.0F, 1.9F)
            .build(new ResourceLocation(Clinker.MOD_ID, "mud_scarab").toString()));

    public static final RegistryObject<EntityType<SalamanderHeadEntity>> SALAMANDER_HEAD = ENTITY_TYPES.register("salamander_head", () ->
            EntityType.Builder.of(SalamanderHeadEntity::new, MobCategory.MONSTER).fireImmune()
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "salamander_head").toString()));
    public static final RegistryObject<EntityType<SalamanderBodyEntity>> SALAMANDER_BODY = ENTITY_TYPES.register("salamander_body", () ->
            EntityType.Builder.of(SalamanderBodyEntity::new, MobCategory.MONSTER).fireImmune()
                    .sized(1.0F, 1.0F)
                    .noSave().noSummon()
                    .build(new ResourceLocation(Clinker.MOD_ID, "salamander_body").toString()));

    public static final RegistryObject<EntityType<NewSalamanderEntity>> SALAMANDER = ENTITY_TYPES.register("salamander", () ->
            EntityType.Builder.of(NewSalamanderEntity::new, MobCategory.MONSTER).fireImmune()
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "salamander").toString()));

    public static final RegistryObject<EntityType<SeaHagEntity>> SEA_HAG = ENTITY_TYPES.register("sea_hag", () ->
            EntityType.Builder.of(SeaHagEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 2.5F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "sea_hag").toString()));

    public static final RegistryObject<EntityType<GnomadAxemanEntity>> GNOMAD_AXEMAN = ENTITY_TYPES.register("gnomad_axeman", () ->
            EntityType.Builder.of(GnomadAxemanEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.5f)
                    .build(new ResourceLocation(Clinker.MOD_ID, "gnomad_axeman").toString()));

    public static final RegistryObject<EntityType<LumberingAspenEntity>> LUMBERING_ASPEN = ENTITY_TYPES.register("lumbering_aspen", () ->
            EntityType.Builder.of(LumberingAspenEntity::new, MobCategory.AMBIENT)
                    .sized(1.0f, 1.0f)
                    .build(new ResourceLocation(Clinker.MOD_ID, "lumbering_aspen").toString()));

    @SubscribeEvent
    public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
        event.put(MUD_SCARAB.get(), MudScarabEntity.createAttributes().build());
        event.put(SALAMANDER_HEAD.get(), SalamanderHeadEntity.createAttributes().build());
        event.put(SALAMANDER_BODY.get(), SalamanderBodyEntity.createAttributes().build());
        event.put(SEA_HAG.get(), SeaHagEntity.createAttributes().build());
        event.put(GNOMAD_AXEMAN.get(), GnomadAxemanEntity.createAttributes().build());
        event.put(LUMBERING_ASPEN.get(), LumberingAspenEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ClinkerEntities.MUD_SCARAB.get(), MudScarabRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.SALAMANDER_HEAD.get(), SalamanderHeadRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.SALAMANDER_BODY.get(), SalamanderBodyRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.SALAMANDER.get(), NewSalamanderRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.SEA_HAG.get(), SeaHagRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.GNOMAD_AXEMAN.get(), GnomadAxemanRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.LUMBERING_ASPEN.get(), LumberingAspenRenderer::new);
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
