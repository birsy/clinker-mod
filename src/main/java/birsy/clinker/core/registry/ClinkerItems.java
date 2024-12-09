package birsy.clinker.core.registry;

import birsy.clinker.common.world.item.*;
import birsy.clinker.common.world.item.components.FuseTimer;
import birsy.clinker.common.world.item.components.LoadedItemStack;
import birsy.clinker.common.world.item.components.OrdnanceEffects;
import birsy.clinker.core.Clinker;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClinkerItems
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Clinker.MOD_ID);
    
    public static final DeferredItem<Item> ALCHEMY_BOOK = ITEMS.register("alchemy_book", () -> new AlchemyBookItem(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));
    
    public static final DeferredItem<Item> RAW_LEAD = ITEMS.registerSimpleItem("raw_lead", new Item.Properties());
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerSimpleItem("lead_ingot", new Item.Properties());
    public static final DeferredItem<Item> LEAD_NUGGET = ITEMS.registerSimpleItem("lead_nugget", new Item.Properties());
    public static final DeferredItem<Item> SULFUR = ITEMS.registerSimpleItem("sulfur", new Item.Properties());
    public static final DeferredItem<Item> BISMUTH = ITEMS.registerSimpleItem("bismuth",new Item.Properties());
    public static final DeferredItem<Item> PYRITE = ITEMS.registerSimpleItem("pyrite", new Item.Properties());
    public static final DeferredItem<Item> IRON_PYRITE = ITEMS.registerSimpleItem("iron_pyrite", new Item.Properties());
    public static final DeferredItem<Item> PHOSPHOR = ITEMS.registerSimpleItem("phosphor", new Item.Properties());
    public static final DeferredItem<Item> IMPURE_RUBY = ITEMS.registerSimpleItem("impure_ruby", new Item.Properties());
    public static final DeferredItem<Item> RUBY = ITEMS.registerSimpleItem("ruby", new Item.Properties());
    public static final DeferredItem<Item> CENTIPEDE_SHELL = ITEMS.registerSimpleItem("centipede_shell", new Item.Properties());
    public static final DeferredItem<Item> SALT = ITEMS.registerSimpleItem("salt", new Item.Properties());
    
    public static final DeferredItem<Item> FAIRY_FRUIT = ITEMS.register("fairy_fruit", () -> new ItemNameBlockItem(ClinkerBlocks.FAIRY_FRUIT_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<OrdnanceItem> ORDNANCE = ITEMS.register("ordnance", () ->
            new OrdnanceItem(new Item.Properties()
                    .stacksTo(24)
                    .component(ClinkerDataComponents.ORDNANCE_EFFECTS.get(), OrdnanceEffects.DEFAULT)
                    .component(ClinkerDataComponents.FUSE_TIMER.get(), FuseTimer.EMPTY)
            )
    );
    public static final DeferredItem<AlchemistsCrossbowItem> ALCHEMISTS_CROSSBOW = ITEMS.register("alchemists_crossbow", () ->
            new AlchemistsCrossbowItem(new Item.Properties()
                    .stacksTo(1).durability(384)
                    .component(ClinkerDataComponents.LOADED_ITEM_STACK.get(), LoadedItemStack.EMPTY)
                    .component(ClinkerDataComponents.FUSE_TIMER.get(), FuseTimer.EMPTY)
                    .component(ClinkerDataComponents.TICK_DELAY.get(), 0)
            )
    );
    public static final DeferredItem<RerollFlaskItem> REROLL_FLASK = ITEMS.register("transmogrifying_flask", () ->
            new RerollFlaskItem(new Item.Properties()
                    .component(DataComponents.RARITY, Rarity.RARE)
                    .stacksTo(16)
            )
    );
    
    
    public static final DeferredItem<SwordItem> LEAD_SWORD = ITEMS.register("lead_sword", () ->
    new SwordItem(Tiers.IRON, new Item.Properties()));
    
    public static final DeferredItem<AxeItem> LEAD_AXE = ITEMS.register("lead_axe", () ->
    new AxeItem(Tiers.IRON, new Item.Properties()));
    
    public static final DeferredItem<PickaxeItem> LEAD_PICKAXE = ITEMS.register("lead_pickaxe", () ->
    new PickaxeItem(Tiers.IRON, new Item.Properties()));
    
    public static final DeferredItem<ShovelItem> LEAD_SHOVEL = ITEMS.register("lead_shovel", () ->
    new ShovelItem(Tiers.IRON, new Item.Properties()));
    
    public static final DeferredItem<HoeItem> LEAD_HOE = ITEMS.register("lead_hoe", () ->
    new HoeItem(Tiers.IRON, new Item.Properties()));

    public static final DeferredItem<Item> LADLE = ITEMS.register("ladle", () -> new LadleItem(new Item.Properties()));

    public static final DeferredItem<Item> MOGUL_WARHOOK = ITEMS.register("mogul_warhook", () -> 
            new MogulWarhookItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .durability(10430)
                            .rarity(Rarity.UNCOMMON)
            )
    );
}
