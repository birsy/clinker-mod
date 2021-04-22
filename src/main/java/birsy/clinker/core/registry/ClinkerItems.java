package birsy.clinker.core.registry;

import birsy.clinker.common.item.ClinkerSpawnEggItem;
import birsy.clinker.common.item.PhospherBallItem;
import birsy.clinker.common.item.food.GnomeatItem;
import birsy.clinker.common.item.food.GnomeatJerkyItem;
import birsy.clinker.common.item.materials.LeadArmorItem;
import birsy.clinker.common.item.materials.enums.ClinkerArmorMaterial;
import birsy.clinker.common.item.materials.enums.ClinkerItemTier;
import birsy.clinker.core.Clinker;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClinkerItems
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Clinker.MOD_ID);
	
	public static void init()
	{
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	/**
	 * MISC ITEMS
	 */
	
	public static final RegistryObject<Item> LEAD_INGOT = ITEMS.register("lead_ingot", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> LEAD_NUGGET = ITEMS.register("lead_nugget", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> SULFUR = ITEMS.register("sulfur", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> BISMUTH = ITEMS.register("bismuth", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> PYRITE = ITEMS.register("pyrite", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> IRON_PYRITE = ITEMS.register("iron_pyrite", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> PHOSPHOR = ITEMS.register("phosphor", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> IMPURE_RUBY = ITEMS.register("impure_ruby", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> RUBY = ITEMS.register("ruby", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> CENTIPEDE_SHELL = ITEMS.register("centipede_shell", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> SALT = ITEMS.register("salt", () -> new Item(new Item.Properties().group(Clinker.CLINKER_MISC)));
	
	//Buckets
	public static final RegistryObject<Item> BRINE_BUCKET = ITEMS.register("brine_bucket", () -> new BucketItem(
			ClinkerFluids.BRINE_SOURCE, (new Item.Properties()).group(Clinker.CLINKER_MISC).maxStackSize(1)));
	//Projectiles
	public static final RegistryObject<Item> PHOSPHER_BALL_ITEM = ITEMS.register("phospher_ball_item", PhospherBallItem::new);
	
	//Spawn Eggs
	public static final RegistryObject<ClinkerSpawnEggItem> SHOGGOTH_SPAWN_EGG = ITEMS.register("shoggoth_spawn_egg",
			() -> new ClinkerSpawnEggItem(ClinkerEntities.SHOGGOTH_HEAD, 0x85494C, 0x6E7058, new Item.Properties().group(Clinker.CLINKER_MISC)));

	public static final RegistryObject<ClinkerSpawnEggItem> SNAIL_SPAWN_EGG = ITEMS.register("snail_spawn_egg",
	() -> new ClinkerSpawnEggItem(ClinkerEntities.SNAIL, 0x85494C, 0x6E7058, new Item.Properties().group(Clinker.CLINKER_MISC)));
	
	public static final RegistryObject<ClinkerSpawnEggItem> GNOME_SPAWN_EGG = ITEMS.register("gnome_spawn_egg",
	() -> new ClinkerSpawnEggItem(ClinkerEntities.GNOME, 0x735d57, 0x763a3e, new Item.Properties().group(Clinker.CLINKER_MISC)));
	
	public static final RegistryObject<ClinkerSpawnEggItem> GNOMAD_AXEMAN_SPAWN_EGG = ITEMS.register("gnomad_axeman_spawn_egg",
	() -> new ClinkerSpawnEggItem(ClinkerEntities.GNOMAD_AXEMAN, 0x36322c, 0x897b75, new Item.Properties().group(Clinker.CLINKER_MISC)));
	
	public static final RegistryObject<ClinkerSpawnEggItem> GNOMAD_SHAMAN_SPAWN_EGG = ITEMS.register("gnomad_shaman_spawn_egg",
	() -> new ClinkerSpawnEggItem(ClinkerEntities.GNOMAD_SHAMAN, 0x544950, 0x897b75, new Item.Properties().group(Clinker.CLINKER_MISC)));
	
	public static final RegistryObject<ClinkerSpawnEggItem> COPTER_GNOMAD_SPAWN_EGG = ITEMS.register("copter_gnomad_spawn_egg",
	() -> new ClinkerSpawnEggItem(ClinkerEntities.COPTER_GNOMAD, 0x36322c, 0x897b75, new Item.Properties().group(Clinker.CLINKER_MISC)));	
	
	public static final RegistryObject<ClinkerSpawnEggItem> HYENA_SPAWN_EGG = ITEMS.register("hyena_spawn_egg",
	() -> new ClinkerSpawnEggItem(ClinkerEntities.HYENA, 0x735d57, 0x763a3e, new Item.Properties().group(Clinker.CLINKER_MISC)));
	
	public static final RegistryObject<ClinkerSpawnEggItem> TOR_ANT_SPAWN_EGG = ITEMS.register("tor_ant_spawn_egg",
	() -> new ClinkerSpawnEggItem(ClinkerEntities.TOR_ANT, 0xb9bf94, 0x3d413d, new Item.Properties().group(Clinker.CLINKER_MISC)));

	public static final RegistryObject<ClinkerSpawnEggItem> BOX_BEETLE_SPAWN_EGG = ITEMS.register("box_beetle_spawn_egg",
			() -> new ClinkerSpawnEggItem(ClinkerEntities.BOX_BEETLE, 0x33323a, 0x5f6352,  new Item.Properties().group(Clinker.CLINKER_MISC)));
	
	public static final RegistryObject<ClinkerSpawnEggItem> WITCH_BRICK_SPAWN_EGG = ITEMS.register("witch_brick_spawn_egg",
	() -> new ClinkerSpawnEggItem(ClinkerEntities.WITCH_BRICK, 0x9aa8af, 0x593025, new Item.Properties().group(Clinker.CLINKER_MISC)));
	
	/**
	 * FOOD
	 */
	public static final RegistryObject<GnomeatJerkyItem> GNOMEAT_JERKY = ITEMS.register("gnomeat_jerky", GnomeatJerkyItem::new);
	public static final RegistryObject<GnomeatItem> GNOMEAT = ITEMS.register("gnomeat", GnomeatItem::new);
	
	/**
	 * TOOLS
	 */
	//Lead Tools
	public static final RegistryObject<SwordItem> LEAD_SWORD = ITEMS.register("lead_sword", () ->
	new SwordItem(ClinkerItemTier.LEAD, 4, -2.4F, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<AxeItem> LEAD_AXE = ITEMS.register("lead_axe", () ->
	new AxeItem(ClinkerItemTier.LEAD, 5, -3.4F, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<PickaxeItem> LEAD_PICKAXE = ITEMS.register("lead_pickaxe", () ->
	new PickaxeItem(ClinkerItemTier.LEAD, 0, -2.8F, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<ShovelItem> LEAD_SHOVEL = ITEMS.register("lead_shovel", () ->
	new ShovelItem(ClinkerItemTier.LEAD, 0, -2.8F, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<HoeItem> LEAD_HOE = ITEMS.register("lead_hoe", () ->
	new HoeItem(ClinkerItemTier.LEAD, 0, -2.8F, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	/**
	 * ARMOR
	 */
	//Lead Armor
	public static final RegistryObject<ArmorItem> LEAD_HELMET = ITEMS.register("lead_helmet", () ->
	new LeadArmorItem(EquipmentSlotType.HEAD));
	
	public static final RegistryObject<ArmorItem> LEAD_CHESTPLATE = ITEMS.register("lead_chestplate", () ->
	new LeadArmorItem(EquipmentSlotType.CHEST));
	
	public static final RegistryObject<ArmorItem> LEAD_LEGGINGS = ITEMS.register("lead_leggings", () ->
	new ArmorItem(ClinkerArmorMaterial.LEAD, EquipmentSlotType.LEGS, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<ArmorItem> LEAD_BOOTS = ITEMS.register("lead_boots", () ->
	new ArmorItem(ClinkerArmorMaterial.LEAD, EquipmentSlotType.FEET, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	//Shell Armor
	public static final RegistryObject<ArmorItem> SHELL_HELMET = ITEMS.register("shell_helmet", () ->
	new ArmorItem(ClinkerArmorMaterial.SHELL, EquipmentSlotType.HEAD, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<ArmorItem> SHELL_CHESTPLATE = ITEMS.register("shell_chestplate", () ->
	new ArmorItem(ClinkerArmorMaterial.SHELL, EquipmentSlotType.CHEST, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<ArmorItem> SHELL_LEGGINGS = ITEMS.register("shell_leggings", () ->
	new ArmorItem(ClinkerArmorMaterial.SHELL, EquipmentSlotType.LEGS, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<ArmorItem> SHELL_BOOTS = ITEMS.register("shell_boots", () ->
	new ArmorItem(ClinkerArmorMaterial.SHELL, EquipmentSlotType.FEET, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<ShieldItem> SHELL_SHIELD = ITEMS.register("shell_shield", () ->
	new ShieldItem(new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	//Traditional Armor
	public static final RegistryObject<ArmorItem> TRADITIONAL_ROBES = ITEMS.register("traditional_robes", () ->
	new ArmorItem(ClinkerArmorMaterial.TRADITIONAL, EquipmentSlotType.CHEST, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<ArmorItem> TRADITIONAL_PANTS = ITEMS.register("traditional_pants", () ->
	new ArmorItem(ClinkerArmorMaterial.TRADITIONAL, EquipmentSlotType.LEGS, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<ArmorItem> TRADITIONAL_SHOES = ITEMS.register("traditional_shoes", () ->
	new ArmorItem(ClinkerArmorMaterial.TRADITIONAL, EquipmentSlotType.FEET, new Item.Properties().group(Clinker.CLINKER_TOOLS)));
}
