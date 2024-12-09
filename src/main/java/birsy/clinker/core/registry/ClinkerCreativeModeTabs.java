package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClinkerCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Clinker.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CLINKER = TABS.register("clinker", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.clinker").withStyle(Style.EMPTY.withFont(Clinker.resource("alchemical")).withColor(ChatFormatting.YELLOW).withBold(true)))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(ClinkerItems.ALCHEMY_BOOK.get().asItem()))
            .displayItems((parameters, output) -> {
                ClinkerCreativeModeTabs.addItems(parameters, output);
                ClinkerCreativeModeTabs.addBlocks(parameters, output);
            }).build());


    public static void addBlocks(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        pOutput.accept(ClinkerBlocks.FERMENTATION_BARREL.get());
        pOutput.accept(ClinkerBlocks.COUNTER.get());
        pOutput.accept(ClinkerBlocks.BLANK_SARCOPHAGUS.get());
        pOutput.accept(ClinkerBlocks.STOVE.get());
        pOutput.accept(ClinkerBlocks.LEAD_BLOCK.get());
        pOutput.accept(ClinkerBlocks.RAW_LEAD_BLOCK.get());
        pOutput.accept(ClinkerBlocks.ASH.get());
        pOutput.accept(ClinkerBlocks.ASH_LAYER.get());
        pOutput.accept(ClinkerBlocks.ASHEN_REGOLITH.get());
        pOutput.accept(ClinkerBlocks.MUD.get());
        pOutput.accept(ClinkerBlocks.BRIMSTONE.get());
        pOutput.accept(ClinkerBlocks.BRIMSTONE_SLAB.get());
        pOutput.accept(ClinkerBlocks.BRIMSTONE_STAIRS.get());
        pOutput.accept(ClinkerBlocks.BRIMSTONE_WALL.get());
        pOutput.accept(ClinkerBlocks.BRIMSTONE_PILLAR.get());
        pOutput.accept(ClinkerBlocks.COBBLED_BRIMSTONE.get());
        pOutput.accept(ClinkerBlocks.COBBLED_BRIMSTONE_SLAB.get());
        pOutput.accept(ClinkerBlocks.COBBLED_BRIMSTONE_STAIRS.get());
        pOutput.accept(ClinkerBlocks.COBBLED_BRIMSTONE_WALL.get());
        pOutput.accept(ClinkerBlocks.POLISHED_BRIMSTONE.get());
        pOutput.accept(ClinkerBlocks.POLISHED_BRIMSTONE_SLAB.get());
        pOutput.accept(ClinkerBlocks.POLISHED_BRIMSTONE_STAIRS.get());
        pOutput.accept(ClinkerBlocks.POLISHED_BRIMSTONE_WALL.get());
        pOutput.accept(ClinkerBlocks.BRIMSTONE_BRICKS.get());
        pOutput.accept(ClinkerBlocks.BRIMSTONE_BRICK_SLAB.get());
        pOutput.accept(ClinkerBlocks.BRIMSTONE_BRICK_STAIRS.get());
        pOutput.accept(ClinkerBlocks.BRIMSTONE_BRICK_WALL.get());
        pOutput.accept(ClinkerBlocks.CHISELED_BRIMSTONE.get());
        pOutput.accept(ClinkerBlocks.SMOOTH_BRIMSTONE.get());
        pOutput.accept(ClinkerBlocks.CALAMINE.get());
        pOutput.accept(ClinkerBlocks.POLISHED_CALAMINE.get());
        pOutput.accept(ClinkerBlocks.CALAMINE_BRICKS.get());
        pOutput.accept(ClinkerBlocks.CAPSTONE.get());
        pOutput.accept(ClinkerBlocks.CAPSTONE_SLAB.get());
        pOutput.accept(ClinkerBlocks.CAPSTONE_STAIRS.get());
        pOutput.accept(ClinkerBlocks.CAPSTONE_WALL.get());
        pOutput.accept(ClinkerBlocks.POLISHED_CAPSTONE.get());
        pOutput.accept(ClinkerBlocks.POLISHED_CAPSTONE_SLAB.get());
        pOutput.accept(ClinkerBlocks.POLISHED_CAPSTONE_STAIRS.get());
        pOutput.accept(ClinkerBlocks.POLISHED_CAPSTONE_WALL.get());
        pOutput.accept(ClinkerBlocks.CAPSTONE_BRICKS.get());
        pOutput.accept(ClinkerBlocks.CAPSTONE_BRICK_SLAB.get());
        pOutput.accept(ClinkerBlocks.CAPSTONE_BRICK_STAIRS.get());
        pOutput.accept(ClinkerBlocks.CAPSTONE_BRICK_WALL.get());
        pOutput.accept(ClinkerBlocks.SULFUR_CRYSTAL_BLOCK.get());
        pOutput.accept(ClinkerBlocks.SULFUR_ROCK_BLOCK.get());
        pOutput.accept(ClinkerBlocks.SHALE.get());
        pOutput.accept(ClinkerBlocks.SHALE_PILLAR.get());
        pOutput.accept(ClinkerBlocks.SMOOTH_SHALE.get());
        pOutput.accept(ClinkerBlocks.SMOOTH_SHALE_SLAB.get());
        pOutput.accept(ClinkerBlocks.SMOOTH_SHALE_STAIRS.get());
        pOutput.accept(ClinkerBlocks.SMOOTH_SHALE_WALL.get());
        pOutput.accept(ClinkerBlocks.POLISHED_SHALE.get());
        pOutput.accept(ClinkerBlocks.POLISHED_SHALE_SLAB.get());
        pOutput.accept(ClinkerBlocks.POLISHED_SHALE_STAIRS.get());
        pOutput.accept(ClinkerBlocks.POLISHED_SHALE_WALL.get());
        pOutput.accept(ClinkerBlocks.SHALE_BRICKS.get());
        pOutput.accept(ClinkerBlocks.SHALE_BRICKS_SLAB.get());
        pOutput.accept(ClinkerBlocks.SHALE_BRICKS_STAIRS.get());
        pOutput.accept(ClinkerBlocks.SHALE_BRICKS_WALL.get());
        pOutput.accept(ClinkerBlocks.SMALL_SHALE_BRICKS.get());
        pOutput.accept(ClinkerBlocks.SMALL_SHALE_BRICKS_SLAB.get());
        pOutput.accept(ClinkerBlocks.SMALL_SHALE_BRICKS_STAIRS.get());
        pOutput.accept(ClinkerBlocks.SMALL_SHALE_BRICKS_FENCE.get());
        pOutput.accept(ClinkerBlocks.LEAD_ORE.get());
        pOutput.accept(ClinkerBlocks.LOCUST_LOG.get());
        pOutput.accept(ClinkerBlocks.TRIMMED_LOCUST_LOG.get());
        pOutput.accept(ClinkerBlocks.STRIPPED_LOCUST_LOG.get());
        pOutput.accept(ClinkerBlocks.LOCUST_PLANKS.get());
        pOutput.accept(ClinkerBlocks.LOCUST_STAIRS.get());
        pOutput.accept(ClinkerBlocks.LOCUST_SLAB.get());
        pOutput.accept(ClinkerBlocks.SWAMP_ASPEN_LOG.get());
        pOutput.accept(ClinkerBlocks.STRIPPED_SWAMP_ASPEN_LOG.get());
        pOutput.accept(ClinkerBlocks.TALL_MUD_REEDS.get());
        pOutput.accept(ClinkerBlocks.SHORT_MUD_REEDS.get());
        pOutput.accept(ClinkerBlocks.MUD_REEDS.get());
        pOutput.accept(ClinkerBlocks.CAVE_FIG_STEM.get());
        pOutput.accept(ClinkerBlocks.CAVE_FIG_ROOTS.get());
        pOutput.accept(ClinkerBlocks.DRIED_CLOVERS.get());
    }

    public static void addItems(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        pOutput.accept(ClinkerItems.ALCHEMY_BOOK.get());
        pOutput.accept(ClinkerItems.RAW_LEAD.get());
        pOutput.accept(ClinkerItems.LEAD_INGOT.get());
        pOutput.accept(ClinkerItems.LEAD_NUGGET.get());
        pOutput.accept(ClinkerItems.SULFUR.get());
        pOutput.accept(ClinkerItems.BISMUTH.get());
        pOutput.accept(ClinkerItems.PYRITE.get());
        pOutput.accept(ClinkerItems.IRON_PYRITE.get());
        pOutput.accept(ClinkerItems.PHOSPHOR.get());
        pOutput.accept(ClinkerItems.IMPURE_RUBY.get());
        pOutput.accept(ClinkerItems.RUBY.get());
        pOutput.accept(ClinkerItems.CENTIPEDE_SHELL.get());
        pOutput.accept(ClinkerItems.SALT.get());
        pOutput.accept(ClinkerItems.FAIRY_FRUIT.get());
        pOutput.accept(ClinkerItems.LEAD_SWORD.get());
        pOutput.accept(ClinkerItems.LEAD_AXE.get());
        pOutput.accept(ClinkerItems.LEAD_PICKAXE.get());
        pOutput.accept(ClinkerItems.LEAD_SHOVEL.get());
        pOutput.accept(ClinkerItems.LEAD_HOE.get());
        pOutput.accept(ClinkerItems.LADLE.get());
        pOutput.accept(ClinkerItems.ORDNANCE.get());
    }
}
