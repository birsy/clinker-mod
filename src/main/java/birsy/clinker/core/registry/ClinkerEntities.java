package birsy.clinker.core.registry;

import birsy.clinker.client.render.entity.*;
import birsy.clinker.common.entity.merchant.GnomeBratEntity;
import birsy.clinker.common.entity.merchant.GnomeEntity;
import birsy.clinker.common.entity.monster.*;
import birsy.clinker.common.entity.monster.beetle.BoxBeetleEntity;
import birsy.clinker.common.entity.monster.gnomad.GnomadAxemanEntity;
import birsy.clinker.common.entity.monster.gnomad.GnomadHelicopterEntity;
import birsy.clinker.common.entity.monster.gnomad.GnomadShamanEntity;
import birsy.clinker.common.entity.passive.SnailEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class ClinkerEntities
{
	public static List<EntityType<? extends LivingEntity>> livingEntities;
	public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Clinker.MOD_ID);
	
	public static void init()
	{
		ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	//Entity Types
	public static final RegistryObject<EntityType<ShoggothHeadEntity>> SHOGGOTH_HEAD = createLivingEntity("shoggoth_head",
			() -> EntityType.Builder.create(ShoggothHeadEntity::new, EntityClassification.MONSTER)
					.size(2.0f, 3.0f)
					.build(makeEntityPath("shoggoth_head")));
	public static final RegistryObject<EntityType<ShoggothBodyEntity>> SHOGGOTH_BODY = createLivingEntity("shoggoth_body",
			() -> EntityType.Builder.create(ShoggothBodyEntity::new, EntityClassification.MONSTER)
					.size(1.5f, 1.25f)
					.build(makeEntityPath("shoggoth_body")));

	//Animals
	public static final RegistryObject<EntityType<SnailEntity>> SNAIL = createLivingEntity("snail",
			() -> EntityType.Builder.create(SnailEntity::new, EntityClassification.CREATURE)
			.size(1.0f, 1.0f)
			.build(makeEntityPath("snail")));
	
	//Gnomes
	public static final RegistryObject<EntityType<GnomeEntity>> GNOME = createLivingEntity("gnome",
			() -> EntityType.Builder.create(GnomeEntity::new, EntityClassification.CREATURE)
			.size(1.0f, 1.5f)
			.build(makeEntityPath("gnome")));
	
	public static final RegistryObject<EntityType<GnomeBratEntity>> GNOME_BRAT = createLivingEntity("gnome_brat",
			() -> EntityType.Builder.create(GnomeBratEntity::new, EntityClassification.CREATURE)
			.size(1.0f, 1.0f)
			.build(makeEntityPath("gnome_brat")));
	
	
	//Gnomads
	public static final RegistryObject<EntityType<GnomadAxemanEntity>> GNOMAD_AXEMAN = createLivingEntity("gnomad_axeman",
			() -> EntityType.Builder.create(GnomadAxemanEntity::new, EntityClassification.MONSTER)
			.size(1.0f, 1.5f)
			.build(makeEntityPath("gnomad_axeman")));
	
	public static final RegistryObject<EntityType<GnomadShamanEntity>> GNOMAD_SHAMAN = createLivingEntity("gnomad_shaman",
			() -> EntityType.Builder.create(GnomadShamanEntity::new, EntityClassification.MONSTER)
			.size(1.0f, 1.5f)
			.build(makeEntityPath("gnomad_shaman")));
	
	public static final RegistryObject<EntityType<GnomadHelicopterEntity>> COPTER_GNOMAD = createLivingEntity("copter_gnomad",
			() -> EntityType.Builder.create(GnomadHelicopterEntity::new, EntityClassification.MONSTER)
			.size(1.0f, 2.5f)
			.build(makeEntityPath("copter_gnomad")));


	//Monsters
	public static final RegistryObject<EntityType<HyenaEntity>> HYENA = createLivingEntity("hyena",
			() -> EntityType.Builder.create(HyenaEntity::new, EntityClassification.MONSTER)
			.size(1.8f, 1.5f)
			.build(makeEntityPath("hyena")));
	
	public static final RegistryObject<EntityType<WitherRevenantEntity>> WITHER_REVENANT = createLivingEntity("wither_revenant",
			() -> EntityType.Builder.create(WitherRevenantEntity::new, EntityClassification.MONSTER)
			.size(1.3f, 2.6f)
			.build(makeEntityPath("wither_revenant")));

	//Beetles
	public static final RegistryObject<EntityType<BoxBeetleEntity>> BOX_BEETLE = createLivingEntity("box_beetle",
			() -> EntityType.Builder.create(BoxBeetleEntity::new, EntityClassification.MONSTER)
					.size(1.3f, 1.2f)
					.build(makeEntityPath("box_beetle")));

	//Witches
	public static final RegistryObject<EntityType<WitchBrickEntity>> WITCH_BRICK = createLivingEntity("witch_brick",
			() -> EntityType.Builder.create(WitchBrickEntity::new, EntityClassification.MONSTER)
			.size(1.2f, 2.2f)
			.build(makeEntityPath("witch_brick")));



	public static void setup()
	{
		GlobalEntityTypeAttributes.put(ClinkerEntities.SHOGGOTH_HEAD.get(), ShoggothHeadEntity.setCustomAttributes().create());
		GlobalEntityTypeAttributes.put(ClinkerEntities.SHOGGOTH_BODY.get(), ShoggothBodyEntity.setCustomAttributes().create());

		GlobalEntityTypeAttributes.put(ClinkerEntities.SNAIL.get(), SnailEntity.setCustomAttributes().create());

		GlobalEntityTypeAttributes.put(ClinkerEntities.GNOME.get(), GnomeEntity.setCustomAttributes().create());
		GlobalEntityTypeAttributes.put(ClinkerEntities.GNOME_BRAT.get(), GnomeBratEntity.setCustomAttributes().create());

		GlobalEntityTypeAttributes.put(ClinkerEntities.GNOMAD_AXEMAN.get(), GnomadAxemanEntity.setCustomAttributes().create());
		GlobalEntityTypeAttributes.put(ClinkerEntities.GNOMAD_SHAMAN.get(), GnomadShamanEntity.setCustomAttributes().create());
		GlobalEntityTypeAttributes.put(ClinkerEntities.COPTER_GNOMAD.get(), GnomadHelicopterEntity.setCustomAttributes().create());

		GlobalEntityTypeAttributes.put(ClinkerEntities.HYENA.get(), HyenaEntity.setCustomAttributes().create());
		GlobalEntityTypeAttributes.put(ClinkerEntities.WITHER_REVENANT.get(), WitherRevenantEntity.setCustomAttributes().create());

		GlobalEntityTypeAttributes.put(ClinkerEntities.BOX_BEETLE.get(), BoxBeetleEntity.setCustomAttributes().create());

		GlobalEntityTypeAttributes.put(ClinkerEntities.WITCH_BRICK.get(), WitchBrickEntity.setCustomAttributes().create());
	}

	public static void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.SHOGGOTH_BODY.get(), ShoggothBodyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.SHOGGOTH_HEAD.get(), ShoggothHeadRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.SNAIL.get(), SnailRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.GNOME.get(), GnomeRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.GNOME_BRAT.get(), GnomeBratRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.GNOMAD_AXEMAN.get(), GnomadAxemanRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.GNOMAD_SHAMAN.get(), GnomadShamanRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.COPTER_GNOMAD.get(), CopterGnomadRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.HYENA.get(), HyenaRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.WITHER_REVENANT.get(), WitherRevenantRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.BOX_BEETLE.get(), BoxBeetleRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(ClinkerEntities.WITCH_BRICK.get(), WitchBrickRenderer::new);
	}



	public static <E extends EntityType<? extends LivingEntity>> RegistryObject<E> createLivingEntity(String name, final Supplier<? extends E> supplier) {
		RegistryObject<E> entity = ENTITY_TYPES.register(name, supplier);
		//livingEntities.add(entity.get());
		return entity;
	}

	public static String makeEntityPath(String name) {
		return new ResourceLocation(Clinker.MOD_ID, name).toString();
	}
}
