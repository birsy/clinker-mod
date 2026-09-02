package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.FieldFactory;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiDefinition;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public final class PaddedNoiseFieldCache extends NoiseFieldCache {
    public final int paddingSize;
    public PaddedNoiseFieldCache(int minX, int minY, int minZ, int chunkHeight, SeededNoiseHolder noiseHolder, int paddingSize) {
        super(minX, minY, minZ, chunkHeight, noiseHolder);
        this.paddingSize = paddingSize;
    }

    protected VoronoiEvaluator createVoronoiEvaluator(String key) {
        VoronoiDefinition definition = this.noiseHolder.voronoiDefinitions.get(key);
        PositionalRandomFactory base = this.noiseHolder.worldRandom.fromHashOf(key).forkPositional();
        return definition.createEvaluatorForChunk(base, this.minX, this.minY, this.minZ, this.chunkHeight, this.paddingSize);
    }

    @Override
    protected NoiseField createStandardNoiseField(Synthesizer computer, FieldFactory.Standard factory) {
        return factory.fieldType().create(this.chunkHeight, this.paddingSize + 1);
    }
}
