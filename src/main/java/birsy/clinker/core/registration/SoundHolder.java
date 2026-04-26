package birsy.clinker.core.registration;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record SoundHolder(
        DeferredHolder<SoundEvent, SoundEvent> holder,
        Optional<String> subtitleKey,
        List<SoundVariant> variants
) implements Supplier<SoundEvent> {

    @Override public SoundEvent get() { return holder.get(); }

    public static Builder builder(DeferredRegister<SoundEvent> registry, String key) { return new Builder(registry, key); }

    public static class Builder {
        final DeferredRegister<SoundEvent> registry;
        final String key;

        Optional<Float> range = Optional.empty();
        Optional<String> subtitleKey;

        SoundVariantProperties defaultProperties = new SoundVariantProperties();
        final LinkedHashMap<ResourceLocation, Consumer<SoundVariantProperties>> variantEntries = new LinkedHashMap<>();

        Builder(DeferredRegister<SoundEvent> registry, String key) {
            this.registry = registry;
            this.key = key;
            this.subtitleKey = Optional.of("subtitles." + key);
        }

        public Builder noSubtitle() { this.subtitleKey = Optional.empty(); return this; }
        public Builder subtitle(String key) { this.subtitleKey = Optional.of(key); return this; }
        public Builder fixedRange(float range) { this.range = Optional.of(range); return this; }

        public Builder volume(float volume) { defaultProperties.volume = volume; return this; }
        public Builder pitch(float pitch) { defaultProperties.pitch = pitch; return this; }
        public Builder weight(int weight) { defaultProperties.weight = weight; return this; }
        public Builder stream() { defaultProperties.stream = true; return this; }
        public Builder preload() { defaultProperties.preload = true; return this; }
        public Builder attenuationDistance(int distance) { defaultProperties.attenuationDistance = distance; return this; }
        public Builder type(Sound.Type type) { defaultProperties.type = type; return this; }

        public Builder variants(int count) { return this.variants(count, (properties) -> {}); }
        public Builder variants(int count, Consumer<SoundVariantProperties> properties) {
            String basePath = key.replace('.', '/');
            for (int i = 1; i <= count; i++) {
                variantEntries.put(loc(basePath + i), properties);
            }
            return this;
        }
        public Builder variants(String... paths) {
            for (String path : paths) {
                variantEntries.put(parseLoc(path), (properties) -> {});
            }
            return this;
        }
        public Builder variant(String path, Consumer<SoundVariantProperties> override) {
            variantEntries.put(parseLoc(path), override);
            return this;
        }

        public SoundHolder build() {
            if (variantEntries.isEmpty()) {
                variantEntries.put(loc(key.replace('.', '/')), (properties) -> {});
            }

            List<SoundVariant> builtVariants = variantEntries.entrySet().stream()
                    .map(entry -> {
                        SoundVariantProperties variantProperties = new SoundVariantProperties(this.defaultProperties);
                        entry.getValue().accept(variantProperties);
                        return variantProperties.create(entry.getKey());
                    }).toList();

            ResourceLocation location = loc(key);
            DeferredHolder<SoundEvent, SoundEvent> holder = registry.register(
                    key,
                    () -> range.map(r -> SoundEvent.createFixedRangeEvent(location, r))
                               .orElse(SoundEvent.createVariableRangeEvent(location))
            );

            return new SoundHolder(holder, subtitleKey, builtVariants);
        }

        public SoundHolder build(Collection<SoundHolder> list) {
            SoundHolder holder = this.build();
            list.add(holder);
            return holder;
        }

        private ResourceLocation loc(String path) {
            return ResourceLocation.fromNamespaceAndPath(registry.getNamespace(), path);
        }

        private ResourceLocation parseLoc(String path) {
            return path.contains(":") ? ResourceLocation.parse(path) : loc(path);
        }
    }

    public static class SoundVariantProperties {
        float volume, pitch;
        int weight;
        boolean stream, preload;
        int attenuationDistance;
        Sound.Type type;

        SoundVariantProperties(SoundVariantProperties from) {
            this.volume = from.volume;
            this.pitch = from.pitch;
            this.weight = from.weight;
            this.stream = from.stream;
            this.preload = from.preload;
            this.attenuationDistance = from.attenuationDistance;
            this.type = from.type;
        }

        SoundVariantProperties() {
            this.volume = SoundVariant.DEFAULT.volume;
            this.pitch = SoundVariant.DEFAULT.pitch;
            this.weight = SoundVariant.DEFAULT.weight;
            this.stream = SoundVariant.DEFAULT.stream;
            this.preload = SoundVariant.DEFAULT.preload;
            this.attenuationDistance = SoundVariant.DEFAULT.attenuationDistance;
            this.type = SoundVariant.DEFAULT.type;
        }

        public SoundVariantProperties volume(float volume) { this.volume = volume; return this; }
        public SoundVariantProperties pitch(float pitch) { this.pitch = pitch; return this; }
        public SoundVariantProperties weight(int weight) { this.weight = weight; return this; }
        public SoundVariantProperties stream() { this.stream = true; return this; }
        public SoundVariantProperties preload() { this.preload = true; return this; }
        public SoundVariantProperties attenuationDistance(int distance) { this.attenuationDistance = distance; return this; }
        public SoundVariantProperties type(Sound.Type type) { this.type = type; return this; }

        SoundVariant create(ResourceLocation filePath) {
            return new SoundVariant(filePath, volume, pitch, weight, stream, attenuationDistance, preload, type);
        }
    }

    public record SoundVariant(
            ResourceLocation filePath,
            float volume,
            float pitch,
            int weight,
            boolean stream,
            int attenuationDistance,
            boolean preload,
            Sound.Type type
    ) {
        public static final SoundVariant DEFAULT =
                new SoundVariant(null, 1.0f, 1.0f, 1, false, 16, false, Sound.Type.FILE);
        public boolean isVolumeDefault() { return volume == DEFAULT.volume; }
        public boolean isPitchDefault() { return pitch == DEFAULT.pitch; }
        public boolean isWeightDefault() { return weight == DEFAULT.weight; }
        public boolean isStreamDefault() { return stream == DEFAULT.stream; }
        public boolean isAttenuationDistanceDefault() { return attenuationDistance == DEFAULT.attenuationDistance; }
        public boolean isPreloadDefault() { return preload == DEFAULT.preload; }
        public boolean isTypeDefault() { return type == DEFAULT.type; }
    }
}
