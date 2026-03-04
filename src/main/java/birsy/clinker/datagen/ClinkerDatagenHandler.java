package birsy.clinker.datagen;

import birsy.clinker.core.Clinker;
import birsy.clinker.datagen.providers.*;
import birsy.clinker.datagen.providers.loottable.ClinkerBlockLootTableProvider;
import birsy.clinker.datagen.providers.loottable.ClinkerMiscLootTableProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClinkerDatagenHandler {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ClinkerBlockTagProvider blockTags = new ClinkerBlockTagProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(true, blockTags);
        generator.addProvider(true, new ClinkerItemTagProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(true, new ClinkerEntityTagProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(true, new ClinkerDamageTypeTagProvider(output, lookupProvider, existingFileHelper));

        generator.addProvider(true, new ClinkerRecipeProvider(output, lookupProvider));

        generator.addProvider(event.includeClient(), new ClinkerBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ClinkerItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ClinkerEnglishLanguageProvider(output));

        event.createProvider((providerOutput, providerLookupProvider) -> new LootTableProvider(providerOutput, Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(ClinkerBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(ClinkerMiscLootTableProvider::new, LootContextParamSets.EMPTY)
                ), providerLookupProvider)
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) (packOutput) -> new DatapackBuiltinEntriesProvider(
                        packOutput,
                        event.getLookupProvider(),
                        new RegistrySetBuilder().add(Registries.BIOME, ClinkerBiomeProvider::addBiomes),
                        Set.of(Clinker.MOD_ID)
                )
        );
    }
}
