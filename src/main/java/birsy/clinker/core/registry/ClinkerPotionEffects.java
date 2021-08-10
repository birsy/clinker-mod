package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerPotionEffects
{
	public static final DeferredRegister<Effect> POTION_EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, Clinker.MOD_ID);
	public static final DeferredRegister<Potion> POTION_TYPES = DeferredRegister.create(ForgeRegistries.POTION_TYPES, Clinker.MOD_ID);
	
	public static void init()
	{
		POTION_EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
		POTION_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<Effect> DESICCATION = POTION_EFFECTS.register("dessication", null);
	public static final RegistryObject<Potion> DESICCATION_POTION = POTION_TYPES.register("dessication", () -> new Potion(new EffectInstance(ClinkerPotionEffects.DESICCATION.get(), 3600)));
}
