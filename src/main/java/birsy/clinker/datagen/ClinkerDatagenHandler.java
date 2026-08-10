package birsy.clinker.datagen;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerSounds;
import birsy.clinker.datagen.providers.*;
import birsy.clinker.datagen.providers.loottable.ClinkerBlockLootTableProvider;
import birsy.clinker.datagen.providers.loottable.ClinkerMiscLootTableProvider;
import birsy.clinker.datagen.providers.ClinkerRecipesProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
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
        event.addProvider(blockTags);
        event.addProvider(new ClinkerItemTagProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
        event.addProvider(new ClinkerEntityTagProvider(output, lookupProvider, existingFileHelper));
        event.addProvider(new ClinkerDamageTypeTagProvider(output, lookupProvider, existingFileHelper));
        event.addProvider(new ClinkerOrdnanceModifierTagProvider(output, lookupProvider, existingFileHelper));
        event.addProvider(new ClinkerRecipesProvider(output, lookupProvider));
        event.addProvider(new ClinkerDataMapProvider(output, lookupProvider));
        generator.addProvider(true,
                new DatapackBuiltinEntriesProvider(output, lookupProvider,
                        new RegistrySetBuilder().add(Registries.BIOME, ClinkerBiomeProvider::addBiomes),
                        Set.of(Clinker.MOD_ID)
                )
        );
        event.addProvider(new LootTableProvider(output, Set.of(), List.of(
                        new LootTableProvider.SubProviderEntry(ClinkerBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(ClinkerMiscLootTableProvider::new, LootContextParamSets.EMPTY)
                ), lookupProvider)
        );

        boolean includeClient = event.includeClient();
        generator.addProvider(includeClient, new ClinkerBlockStateProvider(output, existingFileHelper));
        generator.addProvider(includeClient, new ClinkerItemModelProvider(output, existingFileHelper));
        generator.addProvider(includeClient, new ClinkerEnglishLanguageProvider(output));
        generator.addProvider(includeClient, new ClinkerSoundDefinitionsProvider(output, existingFileHelper, Clinker.MOD_ID, ClinkerSounds.SOUND_HOLDERS));
        generator.addProvider(includeClient, new ClinkerCounterTransformOverrideProvider(output, lookupProvider));
    }
}
