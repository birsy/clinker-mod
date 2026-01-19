package birsy.clinker.core.registry;

import birsy.clinker.common.page.Page;
import birsy.clinker.common.world.item.components.PageContents;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static birsy.clinker.core.registry.ClinkerBlocks.*;
import static birsy.clinker.core.registry.ClinkerItems.*;

public class ClinkerCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Clinker.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CLINKER = TABS.register("clinker", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.clinker.clinker").withStyle(Style.EMPTY.withFont(Clinker.resource("serif"))))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(ORDNANCE.get().asItem()))
            .displayItems((parameters, output) -> {
                ClinkerCreativeModeTabs.addItems(parameters, output);
                ClinkerCreativeModeTabs.addBlocks(parameters, output);
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CLINKER_PAGES = TABS.register("pages", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.clinker.pages").withStyle(Style.EMPTY.withFont(Clinker.resource("serif"))))
            .withTabsBefore(CLINKER.getKey())
            .icon(() -> new ItemStack(ALCHEMY_BOOK.get().asItem()))
            .displayItems((parameters, output) -> {
                output.accept(ALCHEMY_BOOK.get());
                output.accept(PageContents.createItemStack(Holder.direct(Page.BLANK_PAGE)));
                parameters.holders()
                        .lookup(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY)
                        .ifPresent((holderLookup) -> {
                            holderLookup.listElements()
                                    .map(PageContents::createItemStack)
                                    .forEach(output::accept);
                        });
            }).build());


    public static void addBlocks(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        pOutput.accept(SALTPETRE_LEACHED_DIRT.get());

        pOutput.accept(FERMENTATION_BARREL.get());
        pOutput.accept(COUNTER.get());
        pOutput.accept(BLANK_SARCOPHAGUS.get());
        pOutput.accept(STOVE.get());
        pOutput.accept(LEAD_BLOCK.get());
        pOutput.accept(RAW_LEAD_BLOCK.get());
        pOutput.accept(ASH.get());
        pOutput.accept(ASH_LAYER.get());
        pOutput.accept(ASHEN_REGOLITH.get());
        pOutput.accept(MUD.get());

        pOutput.accept(BRIMSTONE.get());
        pOutput.accept(BRIMSTONE_SLAB.get());
        pOutput.accept(BRIMSTONE_STAIRS.get());
        pOutput.accept(BRIMSTONE_WALL.get());
        pOutput.accept(BRIMSTONE_PILLAR.get());
        pOutput.accept(COBBLED_BRIMSTONE.get());
        pOutput.accept(COBBLED_BRIMSTONE_SLAB.get());
        pOutput.accept(COBBLED_BRIMSTONE_STAIRS.get());
        pOutput.accept(COBBLED_BRIMSTONE_WALL.get());
        pOutput.accept(POLISHED_BRIMSTONE.get());
        pOutput.accept(POLISHED_BRIMSTONE_SLAB.get());
        pOutput.accept(POLISHED_BRIMSTONE_STAIRS.get());
        pOutput.accept(POLISHED_BRIMSTONE_WALL.get());
        pOutput.accept(BRIMSTONE_BRICKS.get());
        pOutput.accept(BRIMSTONE_BRICK_SLAB.get());
        pOutput.accept(BRIMSTONE_BRICK_STAIRS.get());
        pOutput.accept(BRIMSTONE_BRICK_WALL.get());
        pOutput.accept(CRACKED_BRIMSTONE_BRICKS.get());
        pOutput.accept(CRACKED_BRIMSTONE_BRICK_SLAB.get());
        pOutput.accept(CRACKED_BRIMSTONE_BRICK_STAIRS.get());
        pOutput.accept(CRACKED_BRIMSTONE_BRICK_WALL.get());

        pOutput.accept(CHISELED_BRIMSTONE.get());
        pOutput.accept(SMOOTH_BRIMSTONE.get());
        pOutput.accept(CALAMINE.get());
        pOutput.accept(POLISHED_CALAMINE.get());
        pOutput.accept(CALAMINE_BRICKS.get());
        pOutput.accept(CAPSTONE.get());
        pOutput.accept(CAPSTONE_SLAB.get());
        pOutput.accept(CAPSTONE_STAIRS.get());
        pOutput.accept(CAPSTONE_WALL.get());
        pOutput.accept(POLISHED_CAPSTONE.get());
        pOutput.accept(POLISHED_CAPSTONE_SLAB.get());
        pOutput.accept(POLISHED_CAPSTONE_STAIRS.get());
        pOutput.accept(POLISHED_CAPSTONE_WALL.get());
        pOutput.accept(CAPSTONE_BRICKS.get());
        pOutput.accept(CAPSTONE_BRICK_SLAB.get());
        pOutput.accept(CAPSTONE_BRICK_STAIRS.get());
        pOutput.accept(CAPSTONE_BRICK_WALL.get());
        pOutput.accept(SULFUR_CRYSTAL_BLOCK.get());
        pOutput.accept(SULFUR_ROCK_BLOCK.get());
        pOutput.accept(SHALE.get());
        pOutput.accept(SHALE_PILLAR.get());
        pOutput.accept(SMOOTH_SHALE.get());
        pOutput.accept(SMOOTH_SHALE_SLAB.get());
        pOutput.accept(SMOOTH_SHALE_STAIRS.get());
        pOutput.accept(SMOOTH_SHALE_WALL.get());
        pOutput.accept(POLISHED_SHALE.get());
        pOutput.accept(POLISHED_SHALE_SLAB.get());
        pOutput.accept(POLISHED_SHALE_STAIRS.get());
        pOutput.accept(POLISHED_SHALE_WALL.get());
        pOutput.accept(SHALE_BRICKS.get());
        pOutput.accept(SHALE_BRICKS_SLAB.get());
        pOutput.accept(SHALE_BRICKS_STAIRS.get());
        pOutput.accept(SHALE_BRICKS_WALL.get());
        pOutput.accept(SMALL_SHALE_BRICKS.get());
        pOutput.accept(SMALL_SHALE_BRICKS_SLAB.get());
        pOutput.accept(SMALL_SHALE_BRICKS_STAIRS.get());
        pOutput.accept(SMALL_SHALE_BRICKS_FENCE.get());

        pOutput.accept(CALC.get());
        pOutput.accept(CALC_SLAB.get());
        pOutput.accept(CALC_STAIRS.get());
        pOutput.accept(CALC_WALL.get());

        pOutput.accept(CALC_BRICKS.get());
        pOutput.accept(CALC_BRICK_SLAB.get());
        pOutput.accept(CALC_BRICK_STAIRS.get());
        pOutput.accept(CALC_BRICK_WALL.get());

        pOutput.accept(POLISHED_CALC.get());
        pOutput.accept(POLISHED_CALC_SLAB.get());
        pOutput.accept(POLISHED_CALC_STAIRS.get());
        pOutput.accept(POLISHED_CALC_WALL.get());

        pOutput.accept(SALT_GRAVEL.get());
        pOutput.accept(SEA_SHELL.get());

        pOutput.accept(SALTMOSS.get());
        pOutput.accept(SALTMOSS_SPROUTS.get());
        pOutput.accept(DRIED_SALTMOSS_SPROUTS.get());
        pOutput.accept(SALTMOSS_BLOSSOM.get());
        pOutput.accept(YARROW.get());
        pOutput.accept(CAVE_SPROUTS.get());

        pOutput.accept(STROMATOLITE.get());

        pOutput.accept(LEAD_ORE.get());

        pOutput.accept(LOCUST_LOG.get());
        pOutput.accept(TRIMMED_LOCUST_LOG.get());
        pOutput.accept(STRIPPED_LOCUST_LOG.get());
        pOutput.accept(LOCUST_PLANKS.get());
        pOutput.accept(LOCUST_STAIRS.get());
        pOutput.accept(LOCUST_SLAB.get());

        pOutput.accept(DISMAL_ASPEN_LOG.get());
        pOutput.accept(BUNDLED_DISMAL_ASPEN_LOGS.get());
        pOutput.accept(DISMAL_ASPEN_PLANKS.get());
        pOutput.accept(DISMAL_ASPEN_STAIRS.get());
        pOutput.accept(DISMAL_ASPEN_SLAB.get());
        pOutput.accept(DISMAL_ASPEN_DOOR.get());
        pOutput.accept(DISMAL_ASPEN_TRAPDOOR.get());
        pOutput.accept(DISMAL_ASPEN_FENCE.get());
        pOutput.accept(DISMAL_ASPEN_FENCE_GATE.get());
        pOutput.accept(DISMAL_ASPEN_PRESSURE_PLATE.get());
        pOutput.accept(DISMAL_ASPEN_BUTTON.get());

        pOutput.accept(TALL_MUD_REEDS.get());
        pOutput.accept(SHORT_MUD_REEDS.get());
        pOutput.accept(MUD_REEDS.get());
        pOutput.accept(CAVE_FIG_STEM.get());
        pOutput.accept(CAVE_FIG_ROOTS.get());
        pOutput.accept(DRIED_CLOVERS.get());
        pOutput.accept(THORNY_STEM.get());
        pOutput.accept(BRAMBLE_BLOSSOM.get());

        pOutput.accept(SHEET_MOSS.get());
        pOutput.accept(LONG_SHEET_MOSS.get());
    }

    public static void addItems(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        pOutput.accept(ALCHEMY_BOOK.get());
        pOutput.accept(PECULIAR_MIRROR.get());

        pOutput.accept(MORTAR.get());
        pOutput.accept(PESTLE.get());

        pOutput.accept(RAW_LEAD.get());
        pOutput.accept(LEAD_INGOT.get());
        pOutput.accept(LEAD_NUGGET.get());

        pOutput.accept(SULFUR.get());
        pOutput.accept(SALTPETRE.get());

        pOutput.accept(FAIRY_FRUIT.get());
        pOutput.accept(LEAD_SWORD.get());
        pOutput.accept(LEAD_AXE.get());
        pOutput.accept(LEAD_PICKAXE.get());
        pOutput.accept(LEAD_SHOVEL.get());
        pOutput.accept(LEAD_HOE.get());
        pOutput.accept(LADLE.get());
        pOutput.accept(ORDNANCE.get());
        pOutput.accept(ALCHEMISTS_CROSSBOW.get());
        pOutput.accept(CROSSBOW_REPEATER_ATTACHMENT.get());
        pOutput.accept(MOGUL_WARHOOK.get());
        pOutput.accept(FISTFUL_OF_MAGGOTS.get());
    }
}
