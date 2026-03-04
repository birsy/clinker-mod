package birsy.clinker.core.registration;

import birsy.clinker.common.world.level.gen.system.surface.decorator.SurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaper;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeSpecialEffectsBuilder;

import javax.annotation.Nullable;
import java.util.*;

public record ClinkerBiome(ResourceKey<Biome> key,
                           BiomeSpecialEffects effects,
                           Map<GenerationStep.Decoration, List<ResourceKey<PlacedFeature>>> features,
                           @Nullable SurfaceShaper surfaceShaper,
                           @Nullable SurfaceDecorator surfaceDecorator) implements DataValue<Biome> {

    @Override
    public Biome create(BootstrapContext<Biome> context) {
        Biome.BiomeBuilder builder = new Biome.BiomeBuilder();
        builder.temperature(0.0F);
        builder.downfall(0.0F);
        builder.hasPrecipitation(false);

        BiomeGenerationSettings.Builder generationSettingsBuilder = new BiomeGenerationSettings.Builder(
                context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER));
        for (Map.Entry<GenerationStep.Decoration, List<ResourceKey<PlacedFeature>>> entry : this.features.entrySet()) {
            for (ResourceKey<PlacedFeature> feature : entry.getValue()) generationSettingsBuilder.addFeature(entry.getKey(), feature);
        }

        builder.generationSettings(generationSettingsBuilder.build());
        builder.mobSpawnSettings(MobSpawnSettings.EMPTY);
        builder.specialEffects(this.effects());
        return builder.build();
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        final ResourceKey<Biome> key;
        final Map<GenerationStep.Decoration, List<ResourceKey<PlacedFeature>>> featureMap = new HashMap<>();
        final BiomeSpecialEffectsBuilder effectsBuilder = BiomeSpecialEffectsBuilder.create(0x8b755d, 0x64615a, 0x0b0a0b, 0x403c3e);
        SurfaceShaper shaper;
        SurfaceDecorator decorator;

        private Builder(String name) {
            this.key = ResourceKey.create(Registries.BIOME, Clinker.resource(name));
        }
        public Builder surfaceShaper(SurfaceShaper shaper) {
            this.shaper = shaper;
            return this;
        }
        public Builder surfaceDecorator(SurfaceDecorator decorator) {
            this.decorator = decorator;
            return this;
        }
        public Builder addFeatures(GenerationStep.Decoration step, ResourceKey<PlacedFeature>... features) {
            Collections.addAll(this.featureMap.computeIfAbsent(step, (key) -> new ArrayList<>()), features);
            return this;
        }

        // ambience
        public Builder fogColor(int fogColor) {
            effectsBuilder.fogColor(fogColor);
            return this;
        }
        public Builder waterColor(int waterColor) {
            effectsBuilder.waterColor(waterColor);
            return this;
        }
        public Builder waterFogColor(int waterFogColor) {
            effectsBuilder.waterFogColor(waterFogColor);
            return this;
        }
        public Builder skyColor(int skyColor) {
            effectsBuilder.skyColor(skyColor);
            return this;
        }
        public Builder foliageColor(int foliageColorOverride) {
            effectsBuilder.foliageColorOverride(foliageColorOverride);
            return this;
        }
        public Builder grassColor(int grassColor) {
            effectsBuilder.grassColorOverride(grassColor);
            return this;
        }
        public Builder grassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier) {
            effectsBuilder.grassColorModifier(grassColorModifier);
            return this;
        }
        public Builder particle(AmbientParticleSettings ambientParticle) {
            effectsBuilder.ambientParticle(ambientParticle);
            return this;
        }
        public Builder loopSound(Holder<SoundEvent> ambientLoopSoundEvent) {
            effectsBuilder.ambientLoopSound(ambientLoopSoundEvent);
            return this;
        }
        public Builder moodSound(AmbientMoodSettings ambientMoodSettings) {
            effectsBuilder.ambientMoodSound(ambientMoodSettings);
            return this;
        }
        public Builder additionsSound(AmbientAdditionsSettings ambientAdditionsSettings) {
            effectsBuilder.ambientAdditionsSound(ambientAdditionsSettings);
            return this;
        }
        public Builder music(Music music) {
            effectsBuilder.backgroundMusic(music);
            return this;
        }
        
        public ClinkerBiome build() {
            return new ClinkerBiome(
                    key,
                    effectsBuilder.build(),
                    Map.copyOf(featureMap),
                    shaper,
                    decorator
            );
        }
    }
}
