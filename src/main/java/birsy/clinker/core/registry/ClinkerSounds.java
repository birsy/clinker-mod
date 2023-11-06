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
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "block.stove.loop")));

	//Entity Sounds
	public static final RegistryObject<SoundEvent> ENTITY_GNOME_AMBIENT = SOUNDS.register("entity.gnome.ambient",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "entity.gnome.ambient")));
	
	public static final RegistryObject<SoundEvent> ENTITY_GNOME_HURT = SOUNDS.register("entity.gnome.hurt",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "entity.gnome.hurt")));
	
	public static final RegistryObject<SoundEvent> ENTITY_GNOME_CHAT = SOUNDS.register("entity.gnome.chat",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "entity.gnome.chat")));
	
	public static final RegistryObject<SoundEvent> ENTITY_GNOME_DEATH = SOUNDS.register("entity.gnome.death",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "entity.gnome.death")));


	public static final RegistryObject<SoundEvent> ORDNANCE_BOUNCE = SOUNDS.register("entity.ordnance.bounce",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "entity.ordnance.bounce")));

	public static final RegistryObject<SoundEvent> ORDNANCE_EXPLODE = SOUNDS.register("entity.ordnance.explode",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "entity.ordnance.explode")));

	public static final RegistryObject<SoundEvent> ORDNANCE_SPARKLE_LOOP = SOUNDS.register("entity.ordnance.sparkle",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "entity.ordnance.sparkle")));

	//Ambient Sounds
	public static final RegistryObject<SoundEvent> AMBIENT_ASH_PLAINS_ADDITIONS = SOUNDS.register("ambient.ash_plains.additions",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "ambient.ash_plains.additions")));

	public static final RegistryObject<SoundEvent> AMBIENT_ASH_PLAINS_LOOP = SOUNDS.register("ambient.ash_plains.loop",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Clinker.MOD_ID, "ambient.ash_plains.loop")));




}
