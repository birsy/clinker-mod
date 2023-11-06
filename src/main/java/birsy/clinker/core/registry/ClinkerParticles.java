package birsy.clinker.core.registry;

import birsy.clinker.client.render.particle.*;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerParticles
{
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Clinker.MOD_ID);

	public static void init() {
		PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public static final RegistryObject<SimpleParticleType> LIGHTNING = createSimpleParticle("lightning");
	public static final RegistryObject<SimpleParticleType> RED_LIGHTNING = createSimpleParticle("red_lightning");
	public static final RegistryObject<SimpleParticleType> SNOOZE = createSimpleParticle("snooze");
	public static final RegistryObject<SimpleParticleType> MOTH = createSimpleParticle("moth");

	public static final RegistryObject<ParticleType<DustColorTransitionOptions>> ORDNANCE_TRAIL = createParticle("ordnance_trail", DustColorTransitionOptions.DESERIALIZER, DustColorTransitionOptions.CODEC);
	public static final RegistryObject<ParticleType<DustColorTransitionOptions>> ORDNANCE_EXPLOSION = createParticle("ordnance_explosion", DustColorTransitionOptions.DESERIALIZER, DustColorTransitionOptions.CODEC);

	public static RegistryObject<SimpleParticleType> createSimpleParticle(String name) {
		RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, () -> new SimpleParticleType(false));
		return particle;
	}

	public static <I extends ParticleOptions> RegistryObject<ParticleType<I>> createParticle(String name, ParticleOptions.Deserializer<I> pDeserializer, Codec<I> codec) {
		RegistryObject<ParticleType<I>> particle = PARTICLES.register(name, () -> new ParticleType<I>(false, pDeserializer) {
			@Override
			public Codec<I> codec() {
				return codec;
			}
		});
		return particle;
	}

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(LIGHTNING.get(), LightningParticle.Provider::new);
		event.registerSpriteSet(RED_LIGHTNING.get(), LightningParticle.Provider::new);
		event.registerSpriteSet(SNOOZE.get(), SnoozeParticle.Provider::new);
		event.registerSpriteSet(MOTH.get(), MothParticle.Provider::new);
		event.registerSpriteSet(ORDNANCE_TRAIL.get(), OrdnanceTrailParticle.Provider::new);
		event.registerSpriteSet(ORDNANCE_EXPLOSION.get(), OrdnanceExplosionParticle.Provider::new);
	}
}
