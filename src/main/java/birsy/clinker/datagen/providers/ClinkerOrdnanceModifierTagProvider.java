package birsy.clinker.datagen.providers;

import birsy.clinker.common.world.ordnance.OrdnanceModifierType;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.registry.ClinkerTags;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ClinkerOrdnanceModifierTagProvider extends IntrinsicHolderTagsProvider<OrdnanceModifierType<?>> {

    public ClinkerOrdnanceModifierTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY_KEY, lookupProvider,
                entityType -> entityType.builtInRegistryHolder().key(), Clinker.MOD_ID, existingFileHelper
        );
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        IntrinsicTagAppender<OrdnanceModifierType<?>> hasFuse = this.tag(ClinkerTags.OrdnanceModifiers.HAS_FUSE).replace(false);
        hasFuse.add(
                ClinkerOrdnanceModifierTypes.EXPLOSIVE.get(),
                ClinkerOrdnanceModifierTypes.FLECHETTES.get()
        );
    }
}
