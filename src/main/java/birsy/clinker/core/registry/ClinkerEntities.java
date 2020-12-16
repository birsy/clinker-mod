package birsy.clinker.core.registry;

import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.entity.monster.GnomadHelicopterEntity;
import birsy.clinker.common.entity.monster.GnomadAxemanEntity;
import birsy.clinker.common.entity.monster.GnomadShamanEntity;
import birsy.clinker.common.entity.monster.HyenaEntity;
import birsy.clinker.common.entity.monster.WitchBrickEntity;
import birsy.clinker.common.entity.monster.WitherRevenantEntity;
import birsy.clinker.common.entity.passive.SnailEntity;
import birsy.clinker.common.entity.passive.TorAntEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("deprecation")
public class ClinkerEntities
{
	public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Clinker.MOD_ID);
	
	public static void init()
	{
		ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static void setup()
	{
    	DeferredWorkQueue.runLater(() -> {
    		GlobalEntityTypeAttributes.put(ClinkerEntities.SNAIL.get(), SnailEntity.setCustomAttributes().create());
    		
    		GlobalEntityTypeAttributes.put(ClinkerEntities.GNOME.get(), GnomeEntity.setCustomAttributes().create());
    		GlobalEntityTypeAttributes.put(ClinkerEntities.GNOME_BRAT.get(), GnomeBratEntity.setCustomAttributes().create());
    		
    		GlobalEntityTypeAttributes.put(ClinkerEntities.GNOMAD_AXEMAN.get(), GnomadAxemanEntity.setCustomAttributes().create());
    		GlobalEntityTypeAttributes.put(ClinkerEntities.GNOMAD_SHAMAN.get(), GnomadShamanEntity.setCustomAttributes().create());
    		GlobalEntityTypeAttributes.put(ClinkerEntities.COPTER_GNOMAD.get(), GnomadHelicopterEntity.setCustomAttributes().create());
    		
    		GlobalEntityTypeAttributes.put(ClinkerEntities.HYENA.get(), HyenaEntity.setCustomAttributes().create());
    		GlobalEntityTypeAttributes.put(ClinkerEntities.TOR_ANT.get(), TorAntEntity.setCustomAttributes().create());
    		GlobalEntityTypeAttributes.put(ClinkerEntities.WITHER_REVENANT.get(), WitherRevenantEntity.setCustomAttributes().create());
    		
    		GlobalEntityTypeAttributes.put(ClinkerEntities.WITCH_BRICK.get(), WitchBrickEntity.setCustomAttributes().create());
    	});
	}
	
	//Entity Types
	
	//Animals
	public static final RegistryObject<EntityType<SnailEntity>> SNAIL = ENTITY_TYPES.register("snail",
			() -> EntityType.Builder.create(SnailEntity::new, EntityClassification.CREATURE)
			.size(1.0f, 1.0f)
			.build(new ResourceLocation(Clinker.MOD_ID, "snail").toString()));
	
	//Gnomes
	public static final RegistryObject<EntityType<GnomeEntity>> GNOME = ENTITY_TYPES.register("gnome",
			() -> EntityType.Builder.create(GnomeEntity::new, EntityClassification.CREATURE)
			.size(1.0f, 2.0f)
			.build(new ResourceLocation(Clinker.MOD_ID, "gnome").toString()));
	
	public static final RegistryObject<EntityType<GnomeBratEntity>> GNOME_BRAT = ENTITY_TYPES.register("gnome_brat",
			() -> EntityType.Builder.create(GnomeBratEntity::new, EntityClassification.CREATURE)
			.size(1.0f, 1.0f)
			.build(new ResourceLocation(Clinker.MOD_ID, "gnome_brat").toString()));
	
	
	//Gnomads
	public static final RegistryObject<EntityType<GnomadAxemanEntity>> GNOMAD_AXEMAN = ENTITY_TYPES.register("gnomad_axeman",
			() -> EntityType.Builder.create(GnomadAxemanEntity::new, EntityClassification.MONSTER)
			.size(1.0f, 2.0f)
			.build(new ResourceLocation(Clinker.MOD_ID, "gnomad_axeman").toString()));
	
	public static final RegistryObject<EntityType<GnomadShamanEntity>> GNOMAD_SHAMAN = ENTITY_TYPES.register("gnomad_shaman",
			() -> EntityType.Builder.create(GnomadShamanEntity::new, EntityClassification.MONSTER)
			.size(1.0f, 2.0f)
			.build(new ResourceLocation(Clinker.MOD_ID, "gnomad_shaman").toString()));
	
	public static final RegistryObject<EntityType<GnomadHelicopterEntity>> COPTER_GNOMAD = ENTITY_TYPES.register("copter_gnomad",
			() -> EntityType.Builder.create(GnomadHelicopterEntity::new, EntityClassification.MONSTER)
			.size(1.0f, 2.5f)
			.build(new ResourceLocation(Clinker.MOD_ID, "copter_gnomad").toString()));
	
	//Monsters
	public static final RegistryObject<EntityType<HyenaEntity>> HYENA = ENTITY_TYPES.register("hyena",
			() -> EntityType.Builder.create(HyenaEntity::new, EntityClassification.MONSTER)
			.size(1.8f, 1.5f)
			.build(new ResourceLocation(Clinker.MOD_ID, "hyena").toString()));
	
	public static final RegistryObject<EntityType<TorAntEntity>> TOR_ANT = ENTITY_TYPES.register("tor_ant",
			() -> EntityType.Builder.create(TorAntEntity::new, EntityClassification.MONSTER)
			.size(3.0f, 2.5f)
			.build(new ResourceLocation(Clinker.MOD_ID, "tor_ant").toString()));
	
	public static final RegistryObject<EntityType<WitherRevenantEntity>> WITHER_REVENANT = ENTITY_TYPES.register("wither_revenant",
			() -> EntityType.Builder.create(WitherRevenantEntity::new, EntityClassification.MONSTER)
			.size(1.3f, 2.6f)
			.build(new ResourceLocation(Clinker.MOD_ID, "wither_revenant").toString()));
	
	//Witches
	public static final RegistryObject<EntityType<WitchBrickEntity>> WITCH_BRICK = ENTITY_TYPES.register("witch_brick",
			() -> EntityType.Builder.create(WitchBrickEntity::new, EntityClassification.MONSTER)
			.size(1.2f, 2.2f)
			.build(new ResourceLocation(Clinker.MOD_ID, "witch_brick").toString()));
	
	//SoundTypes
	
//	public static final RegistryObject<EntityType<PlayerSoundCaster>> PLAYER_SOUND = ENTITY_TYPES.register
//			("player_sound", () -> EntityType.Builder.create(PlayerSoundCaster::new, EntityClassification.MISC)
//			.size(1.0f, 1.0f)
//			.build(new ResourceLocation(Clinker.MOD_ID, "player_sound").toString()));
	
	//Projectiles
	//public static final RegistryObject<EntityType<PhospherBallEntity>> PHOSPHER_BALL = ENTITY_TYPES.register("phospher_ball",
			//() -> EntityType.Builder.create(PhospherBallEntity::new, EntityClassification.MISC)
			//.size(1.0f, 1.0f)
			//.build(new ResourceLocation(Clinker.MOD_ID, "phospher_ball").toString()));
}
