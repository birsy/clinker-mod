package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ClinkerBlockTagProvider extends BlockTagsProvider {

    public ClinkerBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Clinker.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        IntrinsicTagAppender<Block> fenceTag = this.tag(BlockTags.FENCES).replace(false);
        IntrinsicTagAppender<Block> fenceGateTag = this.tag(BlockTags.FENCE_GATES).replace(false);
        IntrinsicTagAppender<Block> wallTag = this.tag(BlockTags.WALLS).replace(false);
        for (DeferredHolder<Block, ? extends Block> entry : ClinkerBlocks.BLOCKS.getEntries()) {
            if (entry.get() instanceof FenceBlock fenceBlock) fenceTag.add(fenceBlock);
            if (entry.get() instanceof FenceGateBlock fenceGateBlock) fenceGateTag.add(fenceGateBlock);
            if (entry.get() instanceof WallBlock wall) wallTag.add(wall);
        }

        IntrinsicTagAppender<Block> othershoreSoil = this.tag(ClinkerTags.Blocks.OTHERSHORE_SOIL).replace(false);
        othershoreSoil.add(
                ClinkerBlocks.ASH.get(),
                ClinkerBlocks.ASH_LAYER.get(),
                ClinkerBlocks.PACKED_ASH.get(),
                ClinkerBlocks.BRIMSTONE.get(),
                ClinkerBlocks.BARRIERROCK.get(),
                ClinkerBlocks.SALTMOSS.get(),
                ClinkerBlocks.CALC.get(),
                ClinkerBlocks.MUD.get(),
                ClinkerBlocks.SALT_GRAVEL.get(),
                ClinkerBlocks.PEAT_MOSS.get()
        );

        IntrinsicTagAppender<Block> climbables = this.tag(BlockTags.CLIMBABLE).replace(false);
        climbables.add(ClinkerBlocks.THORNY_STEM.get());

        IntrinsicTagAppender<Block> brambleFlowers = this.tag(ClinkerTags.Blocks.BRAMBLE_FLOWERS).replace(false);
        brambleFlowers.add(
                ClinkerBlocks.BRAMBLE_BLOSSOM.get(),
                ClinkerBlocks.WITHERING_BRAMBLE_BLOSSOM.get(),
                ClinkerBlocks.CORPSE_LILY_BUD.get(),
                ClinkerBlocks.CORPSE_LILY_BULB.get()
        );

        IntrinsicTagAppender<Block> brambles = this.tag(ClinkerTags.Blocks.BRAMBLES).replace(false);
        brambles.add(
                ClinkerBlocks.THORNY_STEM.get(),
                ClinkerBlocks.SALTY_STEM.get()
        );

        IntrinsicTagAppender<Block> usesPickaxe = this.tag(BlockTags.MINEABLE_WITH_PICKAXE).replace(false);
        IntrinsicTagAppender<Block> usesAxe = this.tag(BlockTags.MINEABLE_WITH_AXE).replace(false);
        IntrinsicTagAppender<Block> usesShovel = this.tag(BlockTags.MINEABLE_WITH_SHOVEL).replace(false);
        IntrinsicTagAppender<Block> usesHoe = this.tag(BlockTags.MINEABLE_WITH_HOE).replace(false);
        IntrinsicTagAppender<Block> usesSword = this.tag(BlockTags.SWORD_EFFICIENT).replace(false);

        usesSword.add(
                ClinkerBlocks.THORNY_STEM.get(),
                ClinkerBlocks.BRAMBLE_BLOSSOM.get(),
                ClinkerBlocks.WITHERING_BRAMBLE_BLOSSOM.get()
        );

        usesPickaxe.add(ClinkerBlocks.SALTMOSS.get());
        usesShovel.add(
                ClinkerBlocks.SALT_GRAVEL.get(),
                ClinkerBlocks.SALTPETRE_LEACHED_DIRT.get(),
                ClinkerBlocks.PEAT_MOSS.get()
        );

        usesHoe.add(
                ClinkerBlocks.PEAT_MOSS.get(),
                ClinkerBlocks.SPOTREED.get()
        );

        usesAxe.add(
                ClinkerBlocks.SPOTREED.get()
        );

        for (DeferredHolder<Block, ? extends Block> block : ClinkerBlocks.BLOCKS.getEntries()) {
            String name = block.getRegisteredName().toLowerCase();
            if (name.contains("brimstone") ||
                name.contains("calc") ||
                name.contains("calamine") ||
                name.contains("capstone") ||
                name.contains("shale") ||
                name.contains("_ore")) {
                usesPickaxe.add(block.get());
            }
            if (name.contains("ash")) {
                usesShovel.add(block.get());
            }
            if (name.contains("dismal_aspen")) {
                usesAxe.add(block.get());
            }
        }

        IntrinsicTagAppender<Block> needsDiamondTool = this.tag(BlockTags.NEEDS_DIAMOND_TOOL).replace(false);
        needsDiamondTool.add(ClinkerBlocks.BARRIERROCK.get());

        IntrinsicTagAppender<Block> isDirt = this.tag(BlockTags.DIRT).replace(false);
        isDirt.add(ClinkerBlocks.SALTPETRE_LEACHED_DIRT.get());
    }
}
