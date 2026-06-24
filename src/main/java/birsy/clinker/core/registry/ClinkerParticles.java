package birsy.clinker.core.registry;

import birsy.clinker.client.particle.ExplosionLightParticle;
import birsy.clinker.client.particle.*;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class ClinkerParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Clinker.MOD_ID);
    
    public static final Supplier<SimpleParticleType> LIGHTNING = register("lightning");
    public static final Supplier<SimpleParticleType> RED_LIGHTNING = register("red_lightning");
    public static final Supplier<SimpleParticleType> SNOOZE = register("snooze");
    public static final Supplier<SimpleParticleType> MOTH = register("moth");
    public static final Supplier<SimpleParticleType> FIREFLY = register("firefly");
    public static final Supplier<SimpleParticleType> FLY = register("fly");
    public static final Supplier<SimpleParticleType> FIRE_SPEW = register("fire_spew");
    public static final Supplier<SimpleParticleType> WRITHING_MAGGOT = register("writhing_maggot");

    public static final Supplier<ParticleType<OrdnanceTrailParticle.Options>> ORDNANCE_TRAIL = register("ordnance_trail",
            false,
            type -> OrdnanceTrailParticle.Options.CODEC,
            type -> OrdnanceTrailParticle.Options.STREAM_CODEC);
    public static final Supplier<ParticleType<DustColorTransitionOptions>> ORDNANCE_EXPLOSION = register("ordnance_explosion",
            false,
            type -> DustColorTransitionOptions.CODEC,
            type -> DustColorTransitionOptions.STREAM_CODEC);
    public static final Supplier<SimpleParticleType> EXPLOSION_LIGHT = register("explosion_light");
    public static final Supplier<ParticleType<ChainLightningBoltParticle.Options>> CHAIN_LIGHTNING_BOLT = register("chain_lightning_bolt",
            true,
            type -> ChainLightningBoltParticle.Options.CODEC,
            type -> ChainLightningBoltParticle.Options.STREAM_CODEC);
    public static final Supplier<ParticleType<BlossomBugParticle.BlossomBugParticleOptions>> BLOSSOM_BUG = register("blossom_bug",
            true,
            type -> BlossomBugParticle.BlossomBugParticleOptions.CODEC,
            type -> BlossomBugParticle.BlossomBugParticleOptions.STREAM_CODEC);

    public static final Supplier<SimpleParticleType> DRIPPING_SALTPETRE = register("dripping_saltpetre");
    public static final Supplier<SimpleParticleType> FALLING_SALTPETRE = register("falling_saltpetre");
    public static final Supplier<SimpleParticleType> LANDING_SALTPETRE = register("landing_saltpetre");
    public static final Supplier<SimpleParticleType> SALTPETRE_LEACH = register("saltpetre_leach");


    public static Supplier<SimpleParticleType> register(String name) {
        return PARTICLES.register(name, () -> new SimpleParticleType(false));
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

    @OnlyIn(Dist.CLIENT)
    @EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
    public static class ClientClinkerParticles {
        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(LIGHTNING.get(), LightningParticle.Provider::new);
            event.registerSpriteSet(RED_LIGHTNING.get(), LightningParticle.Provider::new);
            event.registerSpriteSet(SNOOZE.get(), SnoozeParticle.Provider::new);
            event.registerSpriteSet(MOTH.get(), MothParticle.Provider::new);
            event.registerSpriteSet(FIREFLY.get(), FireflyParticle.Provider::new);
            event.registerSpriteSet(FLY.get(), FlyParticle.Provider::new);
            event.registerSpriteSet(ORDNANCE_TRAIL.get(), OrdnanceTrailParticle.Provider::new);
            event.registerSpriteSet(ORDNANCE_EXPLOSION.get(), OrdnanceExplosionParticle.Provider::new);
            event.registerSpriteSet(EXPLOSION_LIGHT.get(), ExplosionLightParticle.Provider::new);
            event.registerSpriteSet(FIRE_SPEW.get(), FireSpewParticle.Provider::new);
            event.registerSpriteSet(BLOSSOM_BUG.get(), BlossomBugParticle.Provider::new);
            event.registerSpriteSet(WRITHING_MAGGOT.get(), WrithingMaggotParticle.Provider::new);
            event.registerSpriteSet(SALTPETRE_LEACH.get(), SaltpetreLeachParticle.Provider::new);

            event.registerSpecial(CHAIN_LIGHTNING_BOLT.get(), new ChainLightningBoltParticle.Provider());

            registerDripParticle(event, DRIPPING_SALTPETRE.get(), ClinkerDripParticles::createSaltPetreDripHangParticle);
            registerDripParticle(event, FALLING_SALTPETRE.get(), ClinkerDripParticles::createSaltPetreDripFallParticle);
            registerDripParticle(event, LANDING_SALTPETRE.get(), ClinkerDripParticles::createSaltPetreDripLandParticle);
        }

        private static <T extends ParticleOptions> void registerDripParticle(RegisterParticleProvidersEvent event, ParticleType<T> particleType, ParticleProvider.Sprite<T> sprite) {
            event.registerSpriteSet(particleType, (sprites) -> (options, level, x, y, z, xSpeed, ySpeed, zSpeed) -> {
                TextureSheetParticle texturesheetparticle = sprite.createParticle(options, level, x, y, z, xSpeed, ySpeed, zSpeed);
                if (texturesheetparticle != null) texturesheetparticle.pickSprite(sprites);
                return texturesheetparticle;
            });
        }
    }
}
