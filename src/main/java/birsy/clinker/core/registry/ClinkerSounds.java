package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ClinkerSounds
{
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Clinker.MOD_ID);
	
	public static void init()
	{
		SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	//Block Sounds
	public static final RegistryObject<SoundEvent> BLOCK_STOVE_LOOP = SOUNDS.register("block.stove.loop",
			() -> new SoundEvent(new ResourceLocation(Clinker.MOD_ID, "block.stove.loop")));

	//Entity Sounds
	public static final RegistryObject<SoundEvent> ENTITY_GNOME_AMBIENT = SOUNDS.register("entity.gnome.ambient",
			() -> new SoundEvent(new ResourceLocation(Clinker.MOD_ID, "entity.gnome.ambient")));
	
	public static final RegistryObject<SoundEvent> ENTITY_GNOME_HURT = SOUNDS.register("entity.gnome.hurt",
			() -> new SoundEvent(new ResourceLocation(Clinker.MOD_ID, "entity.gnome.hurt")));
	
	public static final RegistryObject<SoundEvent> ENTITY_GNOME_CHAT = SOUNDS.register("entity.gnome.chat",
			() -> new SoundEvent(new ResourceLocation(Clinker.MOD_ID, "entity.gnome.chat")));
	
	public static final RegistryObject<SoundEvent> ENTITY_GNOME_DEATH = SOUNDS.register("entity.gnome.death",
			() -> new SoundEvent(new ResourceLocation(Clinker.MOD_ID, "entity.gnome.death")));
	
	//Ambient Sounds
	public static final RegistryObject<SoundEvent> AMBIENT_ASH_PLAINS_ADDITIONS = SOUNDS.register("ambient.ash_plains.additions",
			() -> new SoundEvent(new ResourceLocation(Clinker.MOD_ID, "ambient.ash_plains.additions")));

	public static final RegistryObject<SoundEvent> AMBIENT_ASH_PLAINS_LOOP = SOUNDS.register("ambient.ash_plains.loop",
			() -> new SoundEvent(new ResourceLocation(Clinker.MOD_ID, "ambient.ash_plains.loop")));


}
