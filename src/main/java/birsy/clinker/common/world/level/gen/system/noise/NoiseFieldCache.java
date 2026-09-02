package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.*;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiDefinition;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator2D;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator3D;
import birsy.clinker.core.registry.ClinkerRegistries;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

public class NoiseFieldCache {
    public final int minX, minY, minZ;
    public final int chunkHeight;
    final SortedSet<NoiseField>[] fieldCache;
    public final CachedNoiseContext context;
    public final SeededNoiseHolder noiseHolder;
    final Object2ObjectMap<String, VoronoiEvaluator> voronoiEvaluators = new Object2ObjectOpenHashMap<>();

    public NoiseFieldCache(int minX, int minY, int minZ, int chunkHeight, SeededNoiseHolder noiseHolder) {
        this.minX = minX; this.minY = minY; this.minZ = minZ;
        this.chunkHeight = chunkHeight;
        this.fieldCache = new SortedSet[ClinkerRegistries.NOISE_COMPUTER_REGISTRY.size()];
        this.noiseHolder = noiseHolder;
        this.context = new CachedNoiseContext(this);
    }

    public NoiseField fillNoiseField(NoiseComputer computer) {
        return fillNoiseField(minY, chunkHeight-1, computer);
    }
    public NoiseField fillNoiseField(Supplier<NoiseComputer> computer) {
        return this.fillNoiseField(computer.get());
    }
    public NoiseField fillNoiseField(int startY, int endY, Supplier<NoiseComputer> computer) {
        return this.fillNoiseField(startY, endY, computer.get());
    }
    public NoiseField fillNoiseField(int startY, int endY, NoiseComputer computer) {
        // collect all dependencies
        NoiseDependencyCollector collector = new NoiseDependencyCollector(noiseHolder);
        for (String name : collector.dependentVoronoiDefinitions) {
            voronoiEvaluators.computeIfAbsent(name, (key) -> this.createVoronoiEvaluator(name)).fill(startY, endY);
        }

        int localStartY = Math.clamp(startY - minY, 0, chunkHeight),
            localEndY = Math.clamp(endY - minY, 0, chunkHeight);
        context.setRange(startY, endY);

        // fill out the values for all noise fields, depth-first
        for (NoiseComputer dependency : collector.dependencies)
            getOrCreateNoiseFieldAtResolution(dependency, computer.fieldType.xzScale(), localStartY, localEndY);
        return getOrCreateNoiseFieldAtResolution(computer, computer.fieldType.xzScale(), localStartY, localEndY);
    }

    protected NoiseField getOrCreateNoiseFieldAtResolution(NoiseComputer computer, int minXZScale, int localStartY, int localEndY) {
        SortedSet<NoiseField> computedFields = fieldCache[computer.id];
        if (computedFields == null) {
            computedFields = new TreeSet<>();
            fieldCache[computer.id] = computedFields;
        }

        for (NoiseField computedField : computedFields) {
            if (computedField.xzCellScale >= minXZScale) return computedField;
        }

        // currently this wastes some work. i shouldn't do that....
        NoiseField field;
        if (computer.fieldType.xzScale() >= minXZScale) {
            field = computer.fieldType.create(chunkHeight, 1);
        } else if (computer.fieldType.twoDimensional()) {
            field = NoiseField.twoDimensional(minXZScale, 1);
        } else {
            field = new NoiseField(chunkHeight, minXZScale, computer.fieldType.yScale(), 1);
        }
        field.fill(localStartY, localEndY, minX, minY, minZ, context, computer.filler);
        computedFields.add(field);

        return field;
    }

    protected VoronoiEvaluator createVoronoiEvaluator(String key) {
        VoronoiDefinition definition = this.noiseHolder.voronoiDefinitions.get(key);
        PositionalRandomFactory base = this.noiseHolder.worldRandom.fromHashOf(key).forkPositional();
        return definition.createEvaluatorForChunk(base, this.minX, this.minY, this.minZ, this.chunkHeight, 0);
    }
}
