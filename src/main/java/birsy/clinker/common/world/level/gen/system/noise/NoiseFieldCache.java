package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.core.registry.ClinkerRegistries;

import java.util.function.Supplier;

public class NoiseFieldCache {
    public final int minX, minY, minZ;
    public final int chunkHeight;
    final NoiseField[] fieldCache;
    public final CachedNoiseContext context;
    public final SeededNoiseHolder noiseHolder;

    public NoiseFieldCache(int minX, int minY, int minZ, int chunkHeight, SeededNoiseHolder noiseHolder) {
        this.minX = minX; this.minY = minY; this.minZ = minZ;
        this.chunkHeight = chunkHeight;
        this.fieldCache = new NoiseField[ClinkerRegistries.NOISE_COMPUTER_REGISTRY.size()];
        this.noiseHolder = noiseHolder;
        this.context = new CachedNoiseContext(this);
    }

    public NoiseField fillNoiseField(NoiseComputer computer) {
        return fillNoiseField(minY, chunkHeight-1, computer);
    }
    public NoiseField fillNoiseField(Supplier<NoiseComputer> computer) {
        return this.fillNoiseField(computer.get());
    }

    public NoiseField fillNoiseField(int startY, int endY, NoiseComputer computer) {
        int localStartY = Math.clamp(startY - minY, 0, chunkHeight), localEndY = Math.clamp(endY - minY, 0, chunkHeight);
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
    public NoiseField fillNoiseField(int startY, int endY, Supplier<NoiseComputer> computer) {
        return this.fillNoiseField(startY, endY, computer.get());
    }

    protected NoiseField createNoiseField(NoiseComputer computer) {
        return computer.fieldType.get().create(this.chunkHeight, 0);
    }
}
