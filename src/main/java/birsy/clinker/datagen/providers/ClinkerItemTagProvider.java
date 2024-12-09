package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.internal.NeoForgeItemTagsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ClinkerItemTagProvider extends ItemTagsProvider {
    public ClinkerItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Clinker.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ClinkerTags.NOT_BUOYANT)
                .add(ClinkerItems.RAW_LEAD.get(),
                    ClinkerItems.LEAD_INGOT.get(),
                    ClinkerItems.LEAD_NUGGET.get(),
                    ClinkerBlocks.RAW_LEAD_BLOCK.asItem(),
                    ClinkerBlocks.LEAD_BLOCK.asItem(),
                    ClinkerItems.LEAD_SWORD.get(),
                    ClinkerItems.LEAD_AXE.get(),
                    ClinkerItems.LEAD_PICKAXE.get(),
                    ClinkerItems.LEAD_SHOVEL.get(),
                    ClinkerItems.LEAD_HOE.get());
        this.tag(ClinkerTags.ALCHEMISTS_CROSSBOW_REPAIRABLE)
                .addTag(ItemTags.PLANKS)
                .addTag(Tags.Items.RODS_WOODEN);
    }
}
