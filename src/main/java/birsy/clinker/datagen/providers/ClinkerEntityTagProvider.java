package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerTags;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ClinkerEntityTagProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {

    public ClinkerEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.ENTITY_TYPE, lookupProvider, entityType -> entityType.builtInRegistryHolder().key(), Clinker.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        IntrinsicHolderTagsProvider.IntrinsicTagAppender<EntityType<?>> thornImmune = this.tag(ClinkerTags.Entities.THORN_IMMUNE).replace(false);
        thornImmune.add(
                EntityType.WITHER_SKELETON, EntityType.WITHER,
                EntityType.SKELETON, EntityType.SKELETON_HORSE,
                EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.SLIME,
                EntityType.IRON_GOLEM, EntityType.ARMADILLO, EntityType.SHULKER
        );

        IntrinsicHolderTagsProvider.IntrinsicTagAppender<EntityType<?>> arthropod = this.tag(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS).replace(false);
        arthropod.add(
                ClinkerEntities.SLAB_CRAB.get()
        );
    }
}
