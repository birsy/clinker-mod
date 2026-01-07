package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.core.registry.ClinkerRegistries;

public class NoiseFieldCache {
    final int minX, minY, minZ;
    final int chunkHeight;
    final NoiseField[] fieldCache;
    public final NoiseContext context;
    public final SeededNoiseHolder noiseHolder;

    public NoiseFieldCache(int minX, int minY, int minZ, int chunkHeight, SeededNoiseHolder noiseHolder) {
        this.minX = minX; this.minY = minY; this.minZ = minZ;
        this.chunkHeight = chunkHeight;
        this.fieldCache = new NoiseField[ClinkerRegistries.NOISE_COMPUTER_REGISTRY.size()];
        this.noiseHolder = noiseHolder;
        this.context = new NoiseContext(this);
    }

    public NoiseField fillNoiseField(NoiseComputer computer) {
        return fillNoiseField(minY, chunkHeight-1, computer);
    }

    public NoiseField fillNoiseField(int startY, int endY, NoiseComputer computer) {
        int localStartY = startY - minY, localEndY = endY - minY;
        context.setRange(startY, endY);

        // collect all dependencies
        NoiseDependencyCollector collector = new NoiseDependencyCollector(noiseHolder);
        collector.addDependency(computer);

        // fill out the values for all noise fields, depth-first
        for (NoiseComputer dependency : collector.dependencies) {
            NoiseField field = fieldCache[dependency.id];
            if (field == null) {
                field = createNoiseField(dependency);
                field.fill(localStartY, localEndY, minX, minY, minZ, context, dependency.filler);
            }
            fieldCache[dependency.id] = field;
        }

        return fieldCache[computer.id];
    }

    protected NoiseField createNoiseField(NoiseComputer computer) {
        return computer.fieldType.get().create(this.chunkHeight, 0);
    }
}
