package birsy.clinker.common.world.level.gen.worldfeature;

import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public interface WorldFeatureType {
    Codec<WorldFeatureType> CODEC = ClinkerRegistries.WORLD_FEATURE_REGISTRY.byNameCodec();
    WorldFeature create(MetaChunk metaChunk, int separationRadius);
}
