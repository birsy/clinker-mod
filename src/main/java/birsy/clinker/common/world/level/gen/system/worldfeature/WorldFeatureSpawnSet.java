package birsy.clinker.common.world.level.gen.system.worldfeature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;

import java.util.List;

public record WorldFeatureSpawnSet(List<Integer> metaChunkDepths, List<WorldFeatureInstance> features) {
    public static final Codec<WorldFeatureSpawnSet> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.listOf().fieldOf("spawn_depths").forGetter(WorldFeatureSpawnSet::metaChunkDepths),
                    WorldFeatureInstance.CODEC.listOf().fieldOf("features").forGetter(WorldFeatureSpawnSet::features)
            ).apply(instance, WorldFeatureSpawnSet::new)
    );

    public record WorldFeatureInstance(WorldFeatureType feature, IntProvider count, int spacingRadius) {
        public static final Codec<WorldFeatureInstance> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        WorldFeatureType.CODEC.fieldOf("feature").forGetter(WorldFeatureInstance::feature),
                        IntProvider.POSITIVE_CODEC.fieldOf("count").forGetter(WorldFeatureInstance::count),
                        Codec.INT.fieldOf("spacing").forGetter(WorldFeatureInstance::spacingRadius)
                ).apply(instance, WorldFeatureInstance::new)
        );
    }
}
