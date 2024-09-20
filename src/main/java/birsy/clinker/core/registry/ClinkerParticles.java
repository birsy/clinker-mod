package birsy.clinker.core.registry;

import birsy.clinker.client.render.ExplosionLightParticle;
import birsy.clinker.client.particle.*;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Clinker.MOD_ID);

    public static void init() {
        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final Supplier<SimpleParticleType> LIGHTNING = createSimpleParticle("lightning");
    public static final Supplier<SimpleParticleType> RED_LIGHTNING = createSimpleParticle("red_lightning");
    public static final Supplier<SimpleParticleType> SNOOZE = createSimpleParticle("snooze");
    public static final Supplier<SimpleParticleType> MOTH = createSimpleParticle("moth");
    public static final Supplier<SimpleParticleType> FIREFLY = createSimpleParticle("firefly");
    public static final Supplier<SimpleParticleType> FIRE_SPEW = createSimpleParticle("fire_spew");

    public static final Supplier<ParticleType<DustColorTransitionOptions>> ORDNANCE_TRAIL = createParticle("ordnance_trail", DustColorTransitionOptions.DESERIALIZER, DustColorTransitionOptions.CODEC);
    public static final Supplier<ParticleType<DustColorTransitionOptions>> ORDNANCE_EXPLOSION = createParticle("ordnance_explosion", DustColorTransitionOptions.DESERIALIZER, DustColorTransitionOptions.CODEC);
    public static final Supplier<SimpleParticleType> EXPLOSION_LIGHT = createSimpleParticle("explosion_light");

    public static final Supplier<ParticleType<ChainLightningParticle.ChainLightningParticleOptions>> CHAIN_LIGHTNING = createParticle("chain_lightning", ChainLightningParticle.ChainLightningParticleOptions.DESERIALIZER, ChainLightningParticle.ChainLightningParticleOptions.CODEC);


    public static Supplier<SimpleParticleType> createSimpleParticle(String name) {
        Supplier<SimpleParticleType> particle = PARTICLES.register(name, () -> new SimpleParticleType(false));
        return particle;
    }

    public static <I extends ParticleOptions> Supplier<ParticleType<I>> createParticle(String name, ParticleOptions.Deserializer<I> pDeserializer, Codec<I> codec) {
        Supplier<ParticleType<I>> particle = PARTICLES.register(name, () -> new ParticleType<I>(false, pDeserializer) {
            @Override
            public Codec<I> codec() {
                return codec;
            }
        });
        return particle;
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(LIGHTNING.get(), LightningParticle.Provider::new);
        event.registerSpriteSet(RED_LIGHTNING.get(), LightningParticle.Provider::new);
        event.registerSpriteSet(SNOOZE.get(), SnoozeParticle.Provider::new);
        event.registerSpriteSet(MOTH.get(), MothParticle.Provider::new);
        event.registerSpriteSet(FIREFLY.get(), FireflyParticle.Provider::new);
        event.registerSpriteSet(ORDNANCE_TRAIL.get(), OrdnanceExplosionParticle.Provider::new);
        event.registerSpriteSet(ORDNANCE_EXPLOSION.get(), OrdnanceExplosionParticle.Provider::new);
        event.registerSpriteSet(EXPLOSION_LIGHT.get(), ExplosionLightParticle.Provider::new);
        event.registerSpriteSet(CHAIN_LIGHTNING.get(), ChainLightningParticle.Provider::new);
        event.registerSpriteSet(FIRE_SPEW.get(), FireSpewParticle.Provider::new);

    }
}
