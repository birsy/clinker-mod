package birsy.clinker.datagen;

import birsy.clinker.core.Clinker;
import birsy.clinker.datagen.providers.ClinkerBlockStateProvider;
import birsy.clinker.datagen.providers.ClinkerBlockTagProvider;
import birsy.clinker.datagen.providers.ClinkerEnglishLanguageProvider;
import birsy.clinker.datagen.providers.ClinkerItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Clinker.MOD_ID)
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

        generator.addProvider(event.includeClient(), new ClinkerBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ClinkerEnglishLanguageProvider(output));
    }
}
