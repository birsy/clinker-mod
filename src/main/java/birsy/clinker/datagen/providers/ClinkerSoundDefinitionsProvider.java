package birsy.clinker.datagen.providers;

import birsy.clinker.core.registration.SoundHolder;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import java.util.Collection;

public class ClinkerSoundDefinitionsProvider extends SoundDefinitionsProvider {
    final Collection<SoundHolder> soundHolders;
    public ClinkerSoundDefinitionsProvider(PackOutput output, ExistingFileHelper existingFileHelper, String modId, Collection<SoundHolder> soundHolders) {
        super(output, modId, existingFileHelper);
        this.soundHolders = soundHolders;
    }

    @Override
    public void registerSounds() {
        for (SoundHolder soundHolder : soundHolders) {
            SoundDefinition definition = SoundDefinition.definition();
            soundHolder.subtitleKey().ifPresent(definition::subtitle);

            definition.with(soundHolder.variants().stream()
                    .map(variant -> {
                        SoundDefinition.Sound sound = sound(variant.filePath());
                        if (!variant.isVolumeDefault()) sound.volume(variant.volume());
                        if (!variant.isPitchDefault()) sound.pitch(variant.pitch());
                        if (!variant.isWeightDefault()) sound.weight(variant.weight());
                        if (!variant.isStreamDefault()) sound.stream(variant.stream());
                        if (!variant.isPreloadDefault()) sound.preload(variant.preload());
                        if (!variant.isAttenuationDistanceDefault()) sound.attenuationDistance(variant.attenuationDistance());
                        return sound;
                    }).toArray(SoundDefinition.Sound[]::new)
            );

            add(soundHolder, definition);
        }
    }
}
