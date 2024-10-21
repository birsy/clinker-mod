package birsy.clinker.core.registry.entity;

import birsy.clinker.client.necromancer.render.NecromancerEntityRenderer;
import birsy.clinker.client.render.entity.*;
import birsy.clinker.client.render.entity.model.*;
import birsy.clinker.common.world.entity.*;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.GnomadSoldierEntity;
import birsy.clinker.common.world.entity.mold.MoldEntity;
import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.common.world.entity.projectile.RerollFlaskEntity;
import birsy.clinker.common.world.entity.projectile.WarhookEntity;
import birsy.clinker.common.world.entity.salamander.NewSalamanderEntity;
import birsy.clinker.common.world.entity.salamanderOLD.SalamanderBodyEntity;
import birsy.clinker.common.world.entity.salamanderOLD.SalamanderHeadEntity;
import birsy.clinker.common.world.entity.gnomad.OldGnomadAxemanEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Clinker.MOD_ID);

    public static final Supplier<EntityType<MudScarabEntity>> MUD_SCARAB = ENTITY_TYPES.register("mud_scarab", () ->
            EntityType.Builder.of(MudScarabEntity::new, MobCategory.MONSTER)
            .sized(2.0F, 1.9F)
            .build(new ResourceLocation(Clinker.MOD_ID, "mud_scarab").toString()));

    public static final Supplier<EntityType<SalamanderHeadEntity>> SALAMANDER_HEAD = ENTITY_TYPES.register("salamander_head", () ->
            EntityType.Builder.of(SalamanderHeadEntity::new, MobCategory.MONSTER).fireImmune()
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "salamander_head").toString()));
    public static final Supplier<EntityType<SalamanderBodyEntity>> SALAMANDER_BODY = ENTITY_TYPES.register("salamander_body", () ->
            EntityType.Builder.of(SalamanderBodyEntity::new, MobCategory.MONSTER).fireImmune()
                    .sized(1.0F, 1.0F)
                    .noSave().noSummon()
                    .build(new ResourceLocation(Clinker.MOD_ID, "salamander_body").toString()));

    public static final Supplier<EntityType<NewSalamanderEntity>> SALAMANDER = ENTITY_TYPES.register("salamander", () ->
            EntityType.Builder.of(NewSalamanderEntity::new, MobCategory.MONSTER).fireImmune()
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "salamander").toString()));

    public static final Supplier<EntityType<SeaHagEntity>> SEA_HAG = ENTITY_TYPES.register("sea_hag", () ->
            EntityType.Builder.of(SeaHagEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 2.5F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "sea_hag").toString()));

    public static final Supplier<EntityType<OldGnomadAxemanEntity>> GNOMAD_AXEMAN = ENTITY_TYPES.register("gnomad_axeman", () ->
            EntityType.Builder.of(OldGnomadAxemanEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.5f)
                    .build(new ResourceLocation(Clinker.MOD_ID, "gnomad_axeman").toString()));

    public static final Supplier<EntityType<GnomadSoldierEntity>> GNOMAD_SOLDIER = ENTITY_TYPES.register("gnomad_soldier", () ->
            EntityType.Builder.of(GnomadSoldierEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.5f)
                    .build(new ResourceLocation(Clinker.MOD_ID, "gnomad").toString()));

    public static final Supplier<EntityType<LumberingAspenEntity>> LUMBERING_ASPEN = ENTITY_TYPES.register("lumbering_aspen", () ->
            EntityType.Builder.of(LumberingAspenEntity::new, MobCategory.AMBIENT)
                    .sized(1.0f, 1.0f)
                    .build(new ResourceLocation(Clinker.MOD_ID, "lumbering_aspen").toString()));

    public static final Supplier<EntityType<UrnEntity>> FAE_URN = ENTITY_TYPES.register("fae_urn", () ->
            EntityType.Builder.of(UrnEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.0f)
                    .build(new ResourceLocation(Clinker.MOD_ID, "fae_urn").toString()));

    public static final Supplier<EntityType<OrdnanceEntity>> ORDNANCE = ENTITY_TYPES.register("ordnance", () ->
            EntityType.Builder.of(OrdnanceEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "ordnance").toString()));
    public static final Supplier<EntityType<WarhookEntity>> WARHOOK = ENTITY_TYPES.register("warhook", () ->
            EntityType.Builder.of(WarhookEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "warhook").toString()));
    public static final Supplier<EntityType<RerollFlaskEntity>> REROLL_FLASK = ENTITY_TYPES.register("transmogrifying_flask", () ->
            EntityType.Builder.of(RerollFlaskEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "transmogrifying_flask").toString()));

    public static final Supplier<EntityType<MoldEntity>> MOLD = ENTITY_TYPES.register("mold", () ->
            EntityType.Builder.of(MoldEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "mold").toString()));

    public static final Supplier<EntityType<InverseKinematicsLegEntity>> LEGS = ENTITY_TYPES.register("legs", () ->
            EntityType.Builder.of(InverseKinematicsLegEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "legs").toString()));

    public static final Supplier<EntityType<FrogNoMoreEntity>> FROG_NO_MORE = ENTITY_TYPES.register("frog_no_more", () ->
            EntityType.Builder.of(FrogNoMoreEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 1.0F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "frog_no_more").toString()));

    public static final Supplier<EntityType<ColliderEntity<?>>> COLLIDER = ENTITY_TYPES.register("collider", () ->
            EntityType.Builder.<ColliderEntity<?>>of(ColliderEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).noSave().noSummon()
                    .build(new ResourceLocation(Clinker.MOD_ID, "collider").toString()));

    public static final Supplier<EntityType<TestRopeEntity>> TEST_ROPE = ENTITY_TYPES.register("test_rope", () ->
            EntityType.Builder.of(TestRopeEntity::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "test_rope").toString()));


    // TESTING
    public static final Supplier<EntityType<FrogNoMoreEntity>> NECROMANCER_RENDER_TEST = ENTITY_TYPES.register("necromancer_render_test", () ->
            EntityType.Builder.of(FrogNoMoreEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "necromancer_render_test").toString()));

    @SubscribeEvent
    public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
        event.put(MUD_SCARAB.get(), MudScarabEntity.createAttributes().build());
        event.put(SALAMANDER_HEAD.get(), SalamanderHeadEntity.createAttributes().build());
        event.put(SALAMANDER_BODY.get(), SalamanderBodyEntity.createAttributes().build());
        event.put(SALAMANDER.get(), NewSalamanderEntity.createAttributes().build());
        event.put(SEA_HAG.get(), SeaHagEntity.createAttributes().build());
        event.put(GNOMAD_AXEMAN.get(), OldGnomadAxemanEntity.createAttributes().build());

        event.put(GNOMAD_SOLDIER.get(), GnomadEntity.createAttributes().build());

        event.put(LUMBERING_ASPEN.get(), LumberingAspenEntity.createAttributes().build());
        event.put(FAE_URN.get(), UrnEntity.createAttributes().build());
        event.put(MOLD.get(), MoldEntity.createAttributes().build());

        event.put(LEGS.get(), MoldEntity.createAttributes().build());
        event.put(FROG_NO_MORE.get(), FrogNoMoreEntity.createAttributes().build());

        //event.put(TEST.get(), FrogNoMoreEntity.createAttributes().build());
        event.put(TEST_ROPE.get(), FrogNoMoreEntity.createAttributes().build());

        event.put(NECROMANCER_RENDER_TEST.get(), FrogNoMoreEntity.createAttributes().build());

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
        event.registerEntityRenderer(ClinkerEntities.FAE_URN.get(), UrnEntityRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.ORDNANCE.get(), OrdnanceRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.WARHOOK.get(), WarhookRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.REROLL_FLASK.get(), ThrownItemRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.GNOMAD_SOLDIER.get(), NewGnomadRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.MOLD.get(), MoldRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.LEGS.get(), InverseKinematicsEntityRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.FROG_NO_MORE.get(), FrogNoMoreRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.COLLIDER.get(), NoopRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.TEST_ROPE.get(), DebugRopeEntityRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.NECROMANCER_RENDER_TEST.get(), NecromancerEntityRenderer::new);

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
