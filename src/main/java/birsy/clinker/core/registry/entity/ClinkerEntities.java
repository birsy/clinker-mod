package birsy.clinker.core.registry.entity;

import birsy.clinker.client.entity.*;
import birsy.clinker.client.entity.mogul.MogulRenderer;
import birsy.clinker.common.world.entity.*;
import birsy.clinker.common.world.entity.gnomad.*;
import birsy.clinker.common.world.entity.mold.MoldEntity;
import birsy.clinker.common.world.entity.projectile.FlechetteEntity;
import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.common.world.entity.projectile.RerollFlaskEntity;
import birsy.clinker.common.world.entity.projectile.WarhookEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.minecraft.world.entity.monster.Zombie;
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

    public static final Supplier<EntityType<GnomadSoldierEntity>> GNOMAD_SOLDIER = ENTITY_TYPES.register("gnomad_soldier", () ->
            EntityType.Builder.of(GnomadSoldierEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.5f)
                    .build(new ResourceLocation(Clinker.MOD_ID, "gnomad").toString()));

    public static final Supplier<EntityType<GnomadMogulEntity>> GNOMAD_MOGUL = ENTITY_TYPES.register("gnomad_mogul", () ->
            EntityType.Builder.of(GnomadMogulEntity::new, MobCategory.MONSTER)
                    .sized(1.5f, 3.5f)
                    .build(new ResourceLocation(Clinker.MOD_ID, "gnomad_mogul").toString()));

    public static final Supplier<EntityType<OrdnanceEntity>> ORDNANCE = ENTITY_TYPES.register("ordnance", () ->
            EntityType.Builder.of(OrdnanceEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "ordnance").toString()));

    public static final Supplier<EntityType<FlechetteEntity>> FLECHETTE = ENTITY_TYPES.register("flechette", () ->
            EntityType.Builder.of(FlechetteEntity::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "flechette").toString()));

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

    public static final Supplier<EntityType<ColliderEntity<?>>> COLLIDER = ENTITY_TYPES.register("collider", () ->
            EntityType.Builder.<ColliderEntity<?>>of(ColliderEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).noSave().noSummon()
                    .build(new ResourceLocation(Clinker.MOD_ID, "collider").toString()));

    public static final Supplier<EntityType<TestRopeEntity>> TEST_ROPE = ENTITY_TYPES.register("test_rope", () ->
            EntityType.Builder.of(TestRopeEntity::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.5F)
                    .build(new ResourceLocation(Clinker.MOD_ID, "test_rope").toString()));

    @SubscribeEvent
    public static void registerEntityAttribute(EntityAttributeCreationEvent event) {
        event.put(GNOMAD_SOLDIER.get(), Zombie.createAttributes().build());
        event.put(GNOMAD_MOGUL.get(), Zombie.createAttributes().build());
        event.put(TEST_ROPE.get(), Zombie.createAttributes().build());

        event.put(MOLD.get(), MoldEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ClinkerEntities.ORDNANCE.get(), OrdnanceRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.FLECHETTE.get(), FlechetteRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.WARHOOK.get(), WarhookRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.REROLL_FLASK.get(), ThrownItemRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.GNOMAD_SOLDIER.get(), DebugEntityRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.GNOMAD_MOGUL.get(), MogulRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.MOLD.get(), MoldRenderer::new);

        event.registerEntityRenderer(ClinkerEntities.COLLIDER.get(), NoopRenderer::new);
        event.registerEntityRenderer(ClinkerEntities.TEST_ROPE.get(), DebugRopeEntityRenderer::new);
    }
}
