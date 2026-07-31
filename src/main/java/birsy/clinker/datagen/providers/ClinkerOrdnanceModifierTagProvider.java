package birsy.clinker.datagen.providers;

import birsy.clinker.common.ordnance.OrdnanceModifierType;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
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
        IntrinsicTagAppender<OrdnanceModifierType<?>> detonates = this.tag(ClinkerTags.OrdnanceModifiers.DETONATES).replace(false);
        detonates.add(
                ClinkerOrdnanceModifierTypes.EXPLOSIVE.get(),
                ClinkerOrdnanceModifierTypes.FLECHETTES.get()
        );

        IntrinsicTagAppender<OrdnanceModifierType<?>> causesDetonation = this.tag(ClinkerTags.OrdnanceModifiers.CAUSES_DETONATION).replace(false);
        causesDetonation.add(
                ClinkerOrdnanceModifierTypes.FUSE_TIME.get(),
                ClinkerOrdnanceModifierTypes.UNSTABLE.get()
        );
    }
}
