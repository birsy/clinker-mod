package birsy.clinker.core.registry;

import birsy.clinker.client.render.particle.BugParticle;
import birsy.clinker.client.render.particle.LightningParticle;
import birsy.clinker.client.render.particle.SnoozeParticle;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
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

	public static final RegistryObject<SimpleParticleType> LIGHTNING = createParticle("lightning");
	public static final RegistryObject<SimpleParticleType> RED_LIGHTNING = createParticle("red_lightning");
	public static final RegistryObject<SimpleParticleType> BUG = createParticle("bug");
	public static final RegistryObject<SimpleParticleType> SNOOZE = createParticle("snooze");

	public static RegistryObject<SimpleParticleType> createParticle(String name) {
		RegistryObject<SimpleParticleType> particle = PARTICLES.register(name, () -> new SimpleParticleType(false));
		return particle;
	}

	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		ParticleEngine engine = Minecraft.getInstance().particleEngine;

		engine.register(LIGHTNING.get(), LightningParticle.Provider::new);
		engine.register(RED_LIGHTNING.get(), LightningParticle.Provider::new);
		engine.register(BUG.get(), BugParticle.Provider::new);
		engine.register(SNOOZE.get(), SnoozeParticle.Provider::new);
	}
}
