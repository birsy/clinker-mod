package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
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
        IntrinsicHolderTagsProvider.IntrinsicTagAppender fenceTags = this.tag(BlockTags.FENCES).replace(false);
        IntrinsicHolderTagsProvider.IntrinsicTagAppender fenceGateTags = this.tag(BlockTags.FENCE_GATES).replace(false);
        IntrinsicHolderTagsProvider.IntrinsicTagAppender wallTags = this.tag(BlockTags.WALLS).replace(false);
        for (DeferredHolder<Block, ? extends Block> entry : ClinkerBlocks.BLOCKS.getEntries()) {
            if (entry.get() instanceof FenceBlock fenceBlock) fenceTags.add(fenceBlock);
            if (entry.get() instanceof FenceGateBlock fenceGateBlock) fenceGateTags.add(fenceGateBlock);
            if (entry.get() instanceof WallBlock wall) wallTags.add(wall);
        }

        IntrinsicHolderTagsProvider.IntrinsicTagAppender othershoreSoil = this.tag(ClinkerTags.OTHERSHORE_SOIL).replace(false);
        othershoreSoil.add(ClinkerBlocks.ASH.get());
        othershoreSoil.add(ClinkerBlocks.ASH_LAYER.get());
        othershoreSoil.add(ClinkerBlocks.ASHEN_REGOLITH.get());
        othershoreSoil.add(ClinkerBlocks.BRIMSTONE.get());
        othershoreSoil.add(ClinkerBlocks.SALTMOSS.get());
        othershoreSoil.add(ClinkerBlocks.CALC.get());
        othershoreSoil.add(ClinkerBlocks.MUD.get());
        othershoreSoil.add(ClinkerBlocks.SALT_GRAVEL.get());

        IntrinsicHolderTagsProvider.IntrinsicTagAppender climbables = this.tag(BlockTags.CLIMBABLE).replace(false);
        climbables.add(ClinkerBlocks.THORNY_STEM.get());

        IntrinsicHolderTagsProvider.IntrinsicTagAppender brambleFlowers = this.tag(ClinkerTags.BRAMBLE_FLOWERS).replace(false);
        brambleFlowers.add(ClinkerBlocks.BRAMBLE_BLOSSOM.get());
        brambleFlowers.add(ClinkerBlocks.WITHERING_BRAMBLE_BLOSSOM.get());
    }
}
