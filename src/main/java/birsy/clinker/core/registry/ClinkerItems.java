package birsy.clinker.core.registry;

import birsy.clinker.client.entity.item.AlchemistsCrossbowRenderer;
import birsy.clinker.client.entity.item.LeadArmorRenderer;
import birsy.clinker.client.render.ClinkerFonts;
import birsy.clinker.common.world.item.*;
import birsy.clinker.common.world.item.components.LoadedItemStack;
import birsy.clinker.common.world.item.components.PageContents;
import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.core.Clinker;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;

import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

@EventBusSubscriber(modid = Clinker.MOD_ID, value = Dist.CLIENT)
public class ClinkerItems
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Clinker.MOD_ID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, Clinker.MOD_ID);

    public static final DeferredItem<Item> ALCHEMY_BOOK = ITEMS.register("alchemy_book", () -> new AlchemyBookItem(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> PECULIAR_MIRROR = ITEMS.registerSimpleItem("peculiar_mirror", new Item.Properties().fireResistant().rarity(Rarity.RARE));

    public static final DeferredItem<Item> RAW_LEAD = ITEMS.registerSimpleItem("raw_lead", new Item.Properties());
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerSimpleItem("lead_ingot", new Item.Properties());
    public static final DeferredItem<Item> LEAD_NUGGET = ITEMS.registerSimpleItem("lead_nugget", new Item.Properties());

    public static final DeferredItem<Item> SULFUR = ITEMS.registerSimpleItem("sulfur", new Item.Properties());
    public static final DeferredItem<Item> SALTPETRE = ITEMS.registerSimpleItem("saltpetre", new Item.Properties());
    public static final DeferredItem<Item> HERBAL_AMALGAM = ITEMS.registerSimpleItem("herbal_amalgam", new Item.Properties());
    public static final DeferredItem<Item> HERBAL_SPIRIT = ITEMS.registerSimpleItem("herbal_spirit", new Item.Properties().stacksTo(16));

    public static final DeferredItem<Item> FISTFUL_OF_MAGGOTS = ITEMS.register("fistful_of_maggots",
            () -> new FistfulOfMaggotsItem(new Item.Properties().stacksTo(16))
    );

    public static final DeferredItem<Item> MUSIC_DISC_CODA = ITEMS.register("music_disc_coda",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, Clinker.resource("coda"))))
    );

    public static final DeferredItem<Item> LEECH = ITEMS.registerSimpleItem("leech", new Item.Properties());

    public static final DeferredItem<Item> FAIRY_FRUIT = ITEMS.register("fairy_fruit", () -> new ItemNameBlockItem(ClinkerBlocks.FAIRY_FRUIT_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<OrdnanceItem> ORDNANCE = ITEMS.register("ordnance", () ->
            new OrdnanceItem(new Item.Properties()
                    .stacksTo(24)
                    .component(ClinkerDataComponents.ORDNANCE_MODIFIERS.get(), OrdnanceModifierSet.NONE)
            )
    );
    public static final DeferredItem<AlchemistsCrossbowItem> ALCHEMISTS_CROSSBOW = ITEMS.register("alchemists_crossbow", () ->
            new AlchemistsCrossbowItem(new Item.Properties()
                    .stacksTo(1).durability(384)
                    .component(ClinkerDataComponents.LOADED_ITEM_STACK.get(), LoadedItemStack.EMPTY)
                    .component(ClinkerDataComponents.TICK_DELAY.get(), 0)
            )
    );
    public static final DeferredItem<Item> CROSSBOW_REPEATER_ATTACHMENT = ITEMS.registerSimpleItem("crossbow_repeater_attachment",
            new Item.Properties()
                    .component(DataComponents.RARITY, Rarity.RARE)
                    .component(DataComponents.LORE, new ItemLore(
                            List.of(Component.translatable("item.clinker.crossbow_repeater_attachment.instructions").withStyle(
                                            Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)
                                                       .withFont(ClinkerFonts.SERIF)))
                    ))
    );


    public static final DeferredItem<RerollFlaskItem> REROLL_FLASK = ITEMS.register("transmogrifying_flask", () ->
            new RerollFlaskItem(new Item.Properties()
                    .component(DataComponents.RARITY, Rarity.UNCOMMON)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .stacksTo(16)
            )
    );

    public static final DeferredItem<PageItem> PAGE = ITEMS.register("page", () ->
            new PageItem(new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.RARITY, Rarity.UNCOMMON)
                    .component(ClinkerDataComponents.PAGE.get(), PageContents.DEFAULT)
            )
    );

    public static final DeferredItem<PestleItem> PESTLE = ITEMS.register("pestle", () -> new PestleItem(new Item.Properties().stacksTo(1)));

    public static final Tier TOOL_TIER_LEAD = new ToolTier(
            Tiers.IRON.getIncorrectBlocksForDrops(), 6144, Tiers.IRON.getSpeed(), Tiers.IRON.getAttackDamageBonus(), 0,
            () -> Ingredient.of(LEAD_INGOT.get())
    );
    public static final DeferredItem<SwordItem> LEAD_SWORD = ITEMS.register("lead_sword", () ->
        new SwordItem(TOOL_TIER_LEAD, new Item.Properties().attributes(SwordItem.createAttributes(TOOL_TIER_LEAD, 3, -2.4F))));
    public static final DeferredItem<AxeItem> LEAD_AXE = ITEMS.register("lead_axe", () ->
        new AxeItem(TOOL_TIER_LEAD, new Item.Properties().attributes(ShovelItem.createAttributes(TOOL_TIER_LEAD, 1.5F, -3.0F))));
    public static final DeferredItem<PickaxeItem> LEAD_PICKAXE = ITEMS.register("lead_pickaxe", () ->
        new PickaxeItem(TOOL_TIER_LEAD, new Item.Properties().attributes(PickaxeItem.createAttributes(TOOL_TIER_LEAD, 1.0F, -2.8F))));
    public static final DeferredItem<ShovelItem> LEAD_SHOVEL = ITEMS.register("lead_shovel", () ->
        new ShovelItem(TOOL_TIER_LEAD, new Item.Properties().attributes(AxeItem.createAttributes(TOOL_TIER_LEAD, 6.0F, -3.1F))));
    public static final DeferredItem<HoeItem> LEAD_HOE = ITEMS.register("lead_hoe", () ->
        new HoeItem(TOOL_TIER_LEAD, new Item.Properties().attributes(HoeItem.createAttributes(TOOL_TIER_LEAD, -2.0F, -1.0F))));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ARMOR_TIER_LEAD = ARMOR_MATERIALS.register(
            "lead",
            () -> ArmorTier.create("lead",
                    2, 5, 6, 2, 5,
                    0, 1.5F, 0.0F,
                    () -> Ingredient.of(LEAD_INGOT.get()),
                    SoundEvents.ARMOR_EQUIP_IRON
            )
    );
    public static final DeferredItem<LeadArmorItem> LEAD_HELMET = ITEMS.register("lead_helmet", () ->
            new LeadArmorItem(ARMOR_TIER_LEAD, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(45))));
    public static final DeferredItem<LeadArmorItem> LEAD_CHESTPLATE = ITEMS.register("lead_chestplate", () ->
            new LeadArmorItem(ARMOR_TIER_LEAD, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(45))));
    public static final DeferredItem<LeadArmorItem> LEAD_LEGGINGS = ITEMS.register("lead_leggings", () ->
            new LeadArmorItem(ARMOR_TIER_LEAD, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(45))));
    public static final DeferredItem<LeadArmorItem> LEAD_BOOTS = ITEMS.register("lead_boots", () ->
            new LeadArmorItem(ARMOR_TIER_LEAD, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(45))));


    public static final DeferredItem<Item> LADLE = ITEMS.register("ladle", () -> new LadleItem(new Item.Properties()));

    public static final DeferredItem<Item> MOGUL_WARHOOK = ITEMS.register("mogul_warhook", () -> 
            new MogulWarhookItem(new Item.Properties()
                    .stacksTo(1)
                    .durability(10430)
                    .rarity(Rarity.UNCOMMON)
            )
    );

    public static final DeferredItem<BucketItem> VITRIOL_BUCKET = ITEMS.register("vitriol_bucket", () ->
            new BucketItem(ClinkerFluids.VITRIOL.get(), new Item.Properties()
                    .craftRemainder(Items.BUCKET)
                    .stacksTo(1))
    );


//    public static final DeferredItem<Item> WATER_FERN_ITEM = ITEMS.register("water_fern", () -> new PlaceOnWaterBlockItem(ClinkerBlocks.WATER_FERN.get(), new Item.Properties()));

    public static final DeferredItem<Item> CORPSE_LILY_SEEDS = ITEMS.register("corpse_lily_seeds", () -> new ItemNameBlockItem(ClinkerBlocks.CORPSE_LILY_BUD.get(), new Item.Properties()));


    @SubscribeEvent
    public static void registerClientItemExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new AlchemistsCrossbowRenderer(), ALCHEMISTS_CROSSBOW);
        event.registerItem(new LeadArmorRenderer(), LEAD_HELMET, LEAD_CHESTPLATE, LEAD_LEGGINGS, LEAD_BOOTS);
    }
}
