package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.WorldFeatureCapability;
import birsy.clinker.core.registry.ClinkerRegistries;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceKey;

import java.util.*;

public class WorldFeatureSet {
    private static final ClassValue<Set<Class<? extends WorldFeatureCapability>>> CAPABILITY_CACHE =
            new ClassValue<>() {
                @Override protected Set<Class<? extends WorldFeatureCapability>> computeValue(Class<?> type) {
                    Set<Class<? extends WorldFeatureCapability>> capabilities = new HashSet<>();
                    for (Map.Entry<ResourceKey<Class<? extends WorldFeatureCapability>>, Class<? extends WorldFeatureCapability>> entry
                            : ClinkerRegistries.WORLD_FEATURE_CAPABILITY_REGISTRY.entrySet()) {
                        Class<? extends WorldFeatureCapability> capability = entry.getValue();
                        if (capability.isAssignableFrom(type)) capabilities.add(capability);
                    }
                    return capabilities;
                }
            };

    final Map<Class<? extends WorldFeatureCapability>, ObjectArrayList<WorldFeature>> byCapability;

    public WorldFeatureSet(Collection<WorldFeature> worldFeatures) {
        this.byCapability = new Object2ObjectArrayMap<>(ClinkerRegistries.WORLD_FEATURE_CAPABILITY_REGISTRY.size());
        worldFeatures.stream()
                .sorted(Comparator.comparingInt(worldFeature -> -worldFeature.type().priority()))
                .forEachOrdered(
                    worldFeature -> {
                        Class<? extends WorldFeature> worldFeatureClass = worldFeature.getClass();

                        Set<Class<? extends WorldFeatureCapability>> capabilitiesForFeature = CAPABILITY_CACHE.get(worldFeatureClass);
                        for (Class<? extends WorldFeatureCapability> capability : capabilitiesForFeature) {
                            byCapability.computeIfAbsent(capability, (key) -> new ObjectArrayList<>()).add(worldFeature);
                        }
                    }
                );
    }

    public <T> List<T> byCapability(Class<T> capability) {
        ObjectArrayList<T> list = (ObjectArrayList<T>) byCapability.getOrDefault(capability, ObjectArrayList.of());
        return Collections.unmodifiableList(list);
    }
}
