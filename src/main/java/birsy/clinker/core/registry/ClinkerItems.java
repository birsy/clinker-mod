package birsy.clinker.core.registry;

import birsy.clinker.common.item.AlchemicalItem;
import birsy.clinker.common.item.AlchemyBookItem;
import birsy.clinker.common.item.LadleItem;
import birsy.clinker.common.item.food.GnomeatItem;
import birsy.clinker.common.item.food.GnomeatJerkyItem;
import birsy.clinker.common.item.materials.enums.ClinkerItemTier;
import birsy.clinker.core.Clinker;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ClinkerItems
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Clinker.MOD_ID);
	
	public static void init()
	{
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public static final RegistryObject<Item> ALCHEMY_BOOK = ITEMS.register("alchemy_book", () -> new AlchemyBookItem(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON).tab(Clinker.CLINKER_MISC)));


	/**
	 * MISC ITEMS
	 */

	public static final RegistryObject<Item> RAW_LEAD = ITEMS.register("raw_lead", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> LEAD_INGOT = ITEMS.register("lead_ingot", () -> new AlchemicalItem(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> LEAD_NUGGET = ITEMS.register("lead_nugget", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> SULFUR = ITEMS.register("sulfur", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> BISMUTH = ITEMS.register("bismuth", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> PYRITE = ITEMS.register("pyrite", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> IRON_PYRITE = ITEMS.register("iron_pyrite", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> PHOSPHOR = ITEMS.register("phosphor", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> IMPURE_RUBY = ITEMS.register("impure_ruby", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> RUBY = ITEMS.register("ruby", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> CENTIPEDE_SHELL = ITEMS.register("centipede_shell", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> SALT = ITEMS.register("salt", () -> new Item(new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> TWIZZLING_VINE_ITEM = ITEMS.register("twizzling_vine_item", () -> new ItemNameBlockItem(ClinkerBlocks.TWIZZLING_VINE.get(), new Item.Properties().tab(Clinker.CLINKER_MISC)));
	public static final RegistryObject<Item> FAIRY_FRUIT = ITEMS.register("fairy_fruit", () -> new ItemNameBlockItem(ClinkerBlocks.FAIRY_FRUIT_BLOCK.get(), new Item.Properties().tab(Clinker.CLINKER_MISC)));

	//Buckets
	//Projectiles

	//Spawn Eggs
	
	/**
	 * FOOD
	 */
	public static final RegistryObject<Item> GNOMEAT_JERKY = ITEMS.register("gnomeat_jerky", GnomeatJerkyItem::new);
	public static final RegistryObject<Item> GNOMEAT = ITEMS.register("gnomeat", GnomeatItem::new);
	
	/**
	 * TOOLS
	 */
	//Lead Tools
	public static final RegistryObject<SwordItem> LEAD_SWORD = ITEMS.register("lead_sword", () ->
	new SwordItem(ClinkerItemTier.LEAD, 4, -2.4F, new Item.Properties().tab(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<AxeItem> LEAD_AXE = ITEMS.register("lead_axe", () ->
	new AxeItem(ClinkerItemTier.LEAD, 5, -3.4F, new Item.Properties().tab(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<PickaxeItem> LEAD_PICKAXE = ITEMS.register("lead_pickaxe", () ->
	new PickaxeItem(ClinkerItemTier.LEAD, 0, -2.8F, new Item.Properties().tab(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<ShovelItem> LEAD_SHOVEL = ITEMS.register("lead_shovel", () ->
	new ShovelItem(ClinkerItemTier.LEAD, 0, -2.8F, new Item.Properties().tab(Clinker.CLINKER_TOOLS)));
	
	public static final RegistryObject<HoeItem> LEAD_HOE = ITEMS.register("lead_hoe", () ->
	new HoeItem(ClinkerItemTier.LEAD, 0, -2.8F, new Item.Properties().tab(Clinker.CLINKER_TOOLS)));

	public static final RegistryObject<Item> LADLE = ITEMS.register("ladle", () -> new LadleItem(new Item.Properties().tab(Clinker.CLINKER_TOOLS)));

	/**
	 * ARMOR
	 */
}
