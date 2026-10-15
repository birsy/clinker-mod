package birsy.clinker.datagen.providers;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerTags;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import birsy.clinker.core.registry.worldgen.ClinkerBiomes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ClinkerBiomeTagProvider extends TagsProvider<Biome> {
    public ClinkerBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, lookupProvider, Clinker.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TagAppender<Biome> hasTownRuins = this.tag(ClinkerTags.Biomes.HAS_TOWN_RUINS).replace(false);
        hasTownRuins.add(ClinkerBiomes.ASH_DUNES, ClinkerBiomes.ASH_STEPPE);
    }
}
