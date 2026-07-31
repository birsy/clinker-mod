package birsy.clinker.core.registry.entity;

import birsy.clinker.client.entity.*;
import birsy.clinker.client.entity.gnomad.basic.GnomadRenderer;
import birsy.clinker.client.entity.gnomad.mogul.GnomadMogulRenderer;
import birsy.clinker.client.entity.gnomad.runt.GnomadRuntRenderer;
import birsy.clinker.client.entity.item.LeadArmorRenderer;
import birsy.clinker.client.entity.leggy.LeggyRenderer;
import birsy.clinker.client.entity.slabcrab.SlabCrabRenderer;
import birsy.clinker.common.entity.*;
import birsy.clinker.common.entity.projectile.FlechetteEntity;
import birsy.clinker.common.entity.projectile.OrdnanceEntity;
import birsy.clinker.common.entity.projectile.RerollFlaskEntity;
import birsy.clinker.common.entity.projectile.WarhookEntity;
import birsy.clinker.common.entity.gnomad.mogul.GnomadMogulEntity;
import birsy.clinker.common.entity.gnomad.GnomadRuntEntity;
import birsy.clinker.common.entity.gnomad.GnomadEntity;
import birsy.clinker.common.entity.homunculoids.SpitterHomunculoid;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class ClinkerEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Clinker.MOD_ID);

    public static final Supplier<EntityType<OrdnanceEntity>> ORDNANCE = ENTITY_TYPES.register("ordnance", () ->
            EntityType.Builder.of(OrdnanceEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(Clinker.resource("ordnance").toString()));

    public static final Supplier<EntityType<FlechetteEntity>> FLECHETTE = ENTITY_TYPES.register("flechette", () ->
            EntityType.Builder.of(FlechetteEntity::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F)
                    .build(Clinker.resource("flechette").toString()));

    public static final Supplier<EntityType<WarhookEntity>> WARHOOK = ENTITY_TYPES.register("warhook", () ->
            EntityType.Builder.of(WarhookEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(Clinker.resource("warhook").toString()));

    public static final Supplier<EntityType<RerollFlaskEntity>> REROLL_FLASK = ENTITY_TYPES.register("transmogrifying_flask", () ->
            EntityType.Builder.of(RerollFlaskEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build(Clinker.resource("transmogrifying_flask").toString()));

    public static final Supplier<EntityType<RedLightningBoltEntity>> RED_LIGHTNING_BOLT = ENTITY_TYPES.register("red_lightning_bolt", () ->
            EntityType.Builder.of(RedLightningBoltEntity::new, MobCategory.MISC)
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(16)
                    .updateInterval(Integer.MAX_VALUE)
                    .noSave()
                    .build(Clinker.resource("red_lightning_bolt").toString()));

    public static final Supplier<EntityType<ColliderEntity<?>>> COLLIDER = ENTITY_TYPES.register("collider", () ->
            EntityType.Builder.<ColliderEntity<?>>of(ColliderEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).noSave().noSummon()
                    .build(Clinker.resource("collider").toString()));

    public static final Supplier<EntityType<FallingLayerEntity>> FALLING_LAYER = ENTITY_TYPES.register("falling_layer", () ->
            EntityType.Builder.of(FallingLayerEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .build(Clinker.resource("falling_layer").toString()));

    public static final Supplier<EntityType<SpitterHomunculoid>> SPITTER_HOMUNCULOID = ENTITY_TYPES.register("spitter_homunculoid", () ->
            EntityType.Builder.of(SpitterHomunculoid::new, MobCategory.MISC)
                    .sized(0.3F, 0.38F)
                    .build(Clinker.resource("spitter_homunculoid").toString()));

    public static final Supplier<EntityType<AiTestEntity>> AI_TEST = ENTITY_TYPES.register("ai_test", () ->
            EntityType.Builder.of(AiTestEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.8F)
                    .build(Clinker.resource("ai_test").toString()));

    public static final Supplier<EntityType<SlabCrabEntity>> SLAB_CRAB = ENTITY_TYPES.register("slab_crab", () ->
            EntityType.Builder.of(SlabCrabEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 0.5F)
                    .build(Clinker.resource("slab_crab").toString()));

    public static final Supplier<EntityType<GiantLeggyCritterEntity>> GIANT_LEGGY_CRITTER = ENTITY_TYPES.register("leggy", () ->
            EntityType.Builder.of(GiantLeggyCritterEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.0F)
                    .build(Clinker.resource("leggy").toString()));

    public static final Supplier<EntityType<GnomadMogulEntity>> GNOMAD_MOGUL = ENTITY_TYPES.register("gnomad_mogul", () ->
            EntityType.Builder.of(GnomadMogulEntity::new, MobCategory.MONSTER)
                    .sized(2.25f, 3.5f)
                    .passengerAttachments(2.96875F)
                    .build(Clinker.resource("gnomad_mogul").toString()));
    public static final Supplier<EntityType<GnomadEntity>> GNOMAD = ENTITY_TYPES.register("gnomad", () ->
            EntityType.Builder.of(GnomadEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.7f)
                    .build(Clinker.resource("gnomad").toString()));
    public static final Supplier<EntityType<GnomadRuntEntity>> GNOMAD_RUNT = ENTITY_TYPES.register("gnomad_runt", () ->
            EntityType.Builder.of(GnomadRuntEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 0.8F)
                    .build(Clinker.resource("gnomad_runt").toString()));

    @SubscribeEvent
    public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
        event.put(SPITTER_HOMUNCULOID.get(), SpitterHomunculoid.createAttributes().build());

        event.put(AI_TEST.get(), Zombie.createAttributes().build());
        event.put(GIANT_LEGGY_CRITTER.get(), Zombie.createAttributes().build());

        event.put(SLAB_CRAB.get(), SlabCrabEntity.createAttributes().build());

        event.put(GNOMAD_MOGUL.get(), GnomadMogulEntity.createAttributes().build());
        event.put(GNOMAD.get(), Zombie.createAttributes().build());
        event.put(GNOMAD_RUNT.get(), GnomadRuntEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ORDNANCE.get(), OrdnanceRenderer::new);
        event.registerEntityRenderer(FLECHETTE.get(), FlechetteRenderer::new);

        event.registerEntityRenderer(WARHOOK.get(), WarhookRenderer::new);
        event.registerEntityRenderer(REROLL_FLASK.get(), ThrownItemRenderer::new);

        event.registerEntityRenderer(RED_LIGHTNING_BOLT.get(), RedLightningBoltRenderer::new);

        event.registerEntityRenderer(FALLING_LAYER.get(), FallingBlockRenderer::new);
        event.registerEntityRenderer(COLLIDER.get(), NoopRenderer::new);

        event.registerEntityRenderer(SPITTER_HOMUNCULOID.get(), DebugEntityRenderer::new);

        event.registerEntityRenderer(AI_TEST.get(), DebugEntityRenderer::new);
        event.registerEntityRenderer(GIANT_LEGGY_CRITTER.get(), LeggyRenderer::new);

        event.registerEntityRenderer(SLAB_CRAB.get(), SlabCrabRenderer::new);

        event.registerEntityRenderer(GNOMAD_MOGUL.get(), GnomadMogulRenderer::new);
        event.registerEntityRenderer(GNOMAD.get(), GnomadRenderer::new);
        event.registerEntityRenderer(GNOMAD_RUNT.get(), GnomadRuntRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LeadArmorRenderer.Model.LAYER_LOCATION, LeadArmorRenderer.Model::createMesh);
    }
}
