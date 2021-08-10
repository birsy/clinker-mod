package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerPotionEffects
{
	public static final DeferredRegister<MobEffect> POTION_EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, Clinker.MOD_ID);
	public static final DeferredRegister<Potion> POTION_TYPES = DeferredRegister.create(ForgeRegistries.POTION_TYPES, Clinker.MOD_ID);
	
	public static void init()
	{
		POTION_EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
		POTION_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<MobEffect> DESICCATION = POTION_EFFECTS.register("dessication", null);
	public static final RegistryObject<Potion> DESICCATION_POTION = POTION_TYPES.register("dessication", () -> new Potion(new MobEffectInstance(ClinkerPotionEffects.DESICCATION.get(), 3600)));
}
