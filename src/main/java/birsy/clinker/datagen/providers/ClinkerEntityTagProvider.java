package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ClinkerEntityTagProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {

    public ClinkerEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.ENTITY_TYPE, lookupProvider, entityType -> entityType.builtInRegistryHolder().key(), Clinker.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<EntityType<?>> thornImmune = this.tag(ClinkerTags.THORN_IMMUNE).replace(false);
        thornImmune.add(
                EntityType.WITHER_SKELETON, EntityType.WITHER,
                EntityType.SKELETON, EntityType.SKELETON_HORSE,
                EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.SLIME,
                EntityType.IRON_GOLEM, EntityType.ARMADILLO, EntityType.SHULKER
        );
    }
}
