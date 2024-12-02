package birsy.clinker.core.registry;

import birsy.clinker.client.particle.ExplosionLightParticle;
import birsy.clinker.client.particle.*;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClinkerParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Clinker.MOD_ID);
    
    public static final Supplier<SimpleParticleType> LIGHTNING = register("lightning");
    public static final Supplier<SimpleParticleType> RED_LIGHTNING = register("red_lightning");
    public static final Supplier<SimpleParticleType> SNOOZE = register("snooze");
    public static final Supplier<SimpleParticleType> MOTH = register("moth");
    public static final Supplier<SimpleParticleType> FIREFLY = register("firefly");
    public static final Supplier<SimpleParticleType> FIRE_SPEW = register("fire_spew");

    public static final Supplier<ParticleType<DustColorTransitionOptions>> ORDNANCE_TRAIL = register("ordnance_trail",
            false,
            type -> DustColorTransitionOptions.CODEC,
            type -> DustColorTransitionOptions.STREAM_CODEC);
    public static final Supplier<ParticleType<DustColorTransitionOptions>> ORDNANCE_EXPLOSION = register("ordnance_explosion",
            false,
            type -> DustColorTransitionOptions.CODEC,
            type -> DustColorTransitionOptions.STREAM_CODEC);
    public static final Supplier<SimpleParticleType> EXPLOSION_LIGHT = register("explosion_light");

    public static final Supplier<ParticleType<ChainLightningParticle.ChainLightningParticleOptions>> CHAIN_LIGHTNING = register("chain_lightning",
            true,
            type -> ChainLightningParticle.ChainLightningParticleOptions.CODEC,
            type -> ChainLightningParticle.ChainLightningParticleOptions.STREAM_CODEC);

    public static Supplier<SimpleParticleType> register(String name) {
        Supplier<SimpleParticleType> particle = PARTICLES.register(name, () -> new SimpleParticleType(false));
        return particle;
    }

    private static <T extends ParticleOptions> Supplier<ParticleType<T>> register(
            String name,
            boolean overrideLimitter,
            final Function<ParticleType<T>, MapCodec<T>> codecGetter,
            final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
    ) {
        return PARTICLES.register(name, () -> new ParticleType<T>(overrideLimitter) {
            @Override
            public MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        });
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(LIGHTNING.get(), LightningParticle.Provider::new);
        event.registerSpriteSet(RED_LIGHTNING.get(), LightningParticle.Provider::new);
        event.registerSpriteSet(SNOOZE.get(), SnoozeParticle.Provider::new);
        event.registerSpriteSet(MOTH.get(), MothParticle.Provider::new);
        event.registerSpriteSet(FIREFLY.get(), FireflyParticle.Provider::new);
        event.registerSpriteSet(ORDNANCE_TRAIL.get(), OrdnanceTrailParticle.Provider::new);
        event.registerSpriteSet(ORDNANCE_EXPLOSION.get(), OrdnanceExplosionParticle.Provider::new);
        event.registerSpriteSet(EXPLOSION_LIGHT.get(), ExplosionLightParticle.Provider::new);
        event.registerSpriteSet(CHAIN_LIGHTNING.get(), ChainLightningParticle.Provider::new);
        event.registerSpriteSet(FIRE_SPEW.get(), FireSpewParticle.Provider::new);
    }
}
