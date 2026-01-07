package birsy.clinker.common.world.level.gen.system.worldfeature;

import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.serialization.Codec;

public interface WorldFeatureType {
    Codec<WorldFeatureType> CODEC = ClinkerRegistries.WORLD_FEATURE_REGISTRY.byNameCodec();
    WorldFeature create(MetaChunk metaChunk, int separationRadius);
}
