package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.FieldFactory;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.VoronoiNoiseField;
import birsy.clinker.common.world.level.gen.system.noise.field.VoronoiNoiseField2D;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiDefinition;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator2D;
import birsy.clinker.common.world.level.gen.system.noise.voronoi.VoronoiEvaluator3D;
import birsy.clinker.core.registry.ClinkerRegistries;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.function.Supplier;

public class NoiseFieldCache {
    public final int minX, minY, minZ;
    public final int chunkHeight;
    final NoiseField[] fieldCache;
    public final CachedNoiseContext context;
    public final SeededNoiseHolder noiseHolder;
    final Object2ObjectMap<String, VoronoiEvaluator> voronoiEvaluators = new Object2ObjectOpenHashMap<>();

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

        for (String name : collector.dependentVoronoiDefinitions) {
            voronoiEvaluators.computeIfAbsent(name, (key) -> this.createVoronoiEvaluator(name)).fill(startY, endY);
        }

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
        FieldFactory factory = computer.fieldFactory.get();
        if (factory instanceof FieldFactory.Standard standardFactory) {
            return createStandardNoiseField(computer, standardFactory);
        } else if (factory instanceof FieldFactory.Voronoi voronoiFactory) {
            return createVoronoiNoiseField(computer, voronoiFactory);
        }
        return null;
    }

    protected VoronoiEvaluator createVoronoiEvaluator(String key) {
        VoronoiDefinition definition = this.noiseHolder.voronoiDefinitions.get(key);
        PositionalRandomFactory base = this.noiseHolder.worldRandom.fromHashOf(key).forkPositional();
        return definition.createEvaluatorForChunk(base, this.minX, this.minY, this.minZ, this.chunkHeight, 0);
    }

    protected NoiseField createStandardNoiseField(NoiseComputer computer, FieldFactory.Standard factory) {
        return factory.fieldType().create(this.chunkHeight, 0);
    }
    protected NoiseField createVoronoiNoiseField(NoiseComputer computer, FieldFactory.Voronoi factory) {
        // create the evaluator
        String id = "noise_computer_" + computer.id + "_voronoi";
        this.noiseHolder.registerVoronoi(id, factory::definition);
        VoronoiEvaluator evaluator = voronoiEvaluators.computeIfAbsent(id, (key) -> this.createVoronoiEvaluator(id));
        // then the noise...
        if (evaluator instanceof VoronoiEvaluator2D v2d)
            return new VoronoiNoiseField2D(v2d, this.minX, this.minZ);
        if (evaluator instanceof VoronoiEvaluator3D v3d)
            return new VoronoiNoiseField(v3d, this.minX, this.minY, this.minZ, this.chunkHeight);

        return null;
    }
}
