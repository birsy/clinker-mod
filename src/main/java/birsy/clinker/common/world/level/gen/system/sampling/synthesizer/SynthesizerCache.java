package birsy.clinker.common.world.level.gen.system.sampling.synthesizer;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldFiller;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldSampler;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import birsy.clinker.core.Clinker;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.*;

// todo: find a better name for this
public class SynthesizerCache {
    final Map<Synthesizer, InterpolatingField> resolvedFields = new HashMap<>();
    final int chunkHeight; // height of the current chunk, in blocks
    final int chunkMinX, chunkMinY, chunkMinZ; // bottom south-west corner of the current chunk, in blocks
    final PositionalRandomFactory noiseRandom;

    public SynthesizerCache(int chunkMinX, int chunkMinY, int chunkMinZ, int chunkHeight, RandomState randomState) {
        this.chunkMinX = chunkMinX;
        this.chunkMinY = chunkMinY;
        this.chunkMinZ = chunkMinZ;
        this.chunkHeight = chunkHeight;
        this.noiseRandom = randomState.getOrCreateRandomFactory(NoiseProvider.NOISE_GEN_SEED);
    }

    // computes the value of a synthesizer at a single point.
    // this is terrible so try not to use it.
    public float samplePoint(Synthesizer synthesizer, int x, int y, int z) {
        Map<Synthesizer, Float> resolvedDependencies = new HashMap<>();
        Map<Synthesizer, DependencyRetriever> resolvedDependencyRetriever = new HashMap<>();
        for (Synthesizer.Dependency dependency : synthesizer.resolvedDependencies) {
            Synthesizer dependencySynthesizer = dependency.synthesizer();
            resolvedDependencies.put(
                    dependencySynthesizer,
                    resolvePoint(
                            dependencySynthesizer,
                            x, y, z,
                            resolvedDependencies,
                            resolvedDependencyRetriever
                    )
            );
            resolvedDependencyRetriever.put(
                    dependencySynthesizer,
                    new DependencyRetriever.Direct(this, dependencySynthesizer)
            );
        }
        return resolvePoint(
                synthesizer,
                x, y, z,
                resolvedDependencies,
                resolvedDependencyRetriever
        );
    }

    // computes the values of a synthesizer within this chunk
    // simply request the amount of padding you want
    // and it'll return a nice, filled field for you.
    public InterpolatingField sampleThisChunk(Synthesizer synthesizer, int desiredXZPadding) {
        for (Synthesizer.Dependency dependency : synthesizer.resolvedDependencies) {
            resolveField(
                    dependency.synthesizer(),
                    Math.max(dependency.minY(), chunkMinY),
                    Math.min(dependency.maxY(), chunkMinY + chunkHeight),
                    desiredXZPadding
            );
        }
        return resolveField(
                synthesizer,
                Math.max(synthesizer.minY, chunkMinY),
                Math.min(synthesizer.maxY, chunkMinY + chunkHeight),
                desiredXZPadding
        );
    }

    float resolvePoint(Synthesizer synthesizer, int x, int y, int z, Map<Synthesizer, Float> resolvedDependencies, Map<Synthesizer, DependencyRetriever> resolvedDependencyRetrievers) {
        float val = y > synthesizer.defaultValueSliceY ? synthesizer.upperSliceDefaultValue : synthesizer.lowerSliceDefaultValue;
        for (Synthesizer.Pass pass : synthesizer.passes) {
            NoiseSampler[] noises = createNoiseSamplers(pass);
            DependencyRetriever[] retrievers = new DependencyRetriever.Field[pass.dependencies().size()];
            ImmutableList<Synthesizer.Dependency> dependencies = pass.dependencies();
            for (int i = 0; i < dependencies.size(); i++) {
                retrievers[i] = resolvedDependencyRetrievers.get(dependencies.get(i).synthesizer());
            }
            SynthesizerContext context = new SynthesizerContext(retrievers, noises);
            for (int i = 0; i < dependencies.size(); i++) {
                context.dependencyValues[i] = resolvedDependencies.get(dependencies.get(i).synthesizer());
            }
            context.oldValue = val;
            context.x = x; context.y = y; context.z = z;
            val = pass.function().compute(context);
        }
        return val;
    }

    InterpolatingField resolveField(Synthesizer synthesizer, int fromY, int toY, int desiredXZPadding) {
        // todo: manage filled y values here, rather than in InterpolatingField
        InterpolatingField resolvedField = resolvedFields.get(synthesizer);
        if (resolvedField != null && resolvedField.paddingBlocks >= desiredXZPadding) return resolvedField;

        long startTime = System.nanoTime();

        // initialize the field
        InterpolatingField field = InterpolatingFieldResolution.fromResolution(
                synthesizer.resolution.xzScale(), synthesizer.resolution.yScale(), synthesizer.resolution.twoDimensional(),
                chunkHeight, desiredXZPadding);
        field.defaultValueFill(
                synthesizer.defaultValueSliceY - chunkMinY,
                synthesizer.lowerSliceDefaultValue,
                synthesizer.upperSliceDefaultValue
        );

        int cellsFilled = 0;
        // run each pass in order
        for (Synthesizer.Pass pass : synthesizer.passes) {
            NoiseSampler[] noises = createNoiseSamplers(pass);

            // get or create all dependency fields
            DependencyRetriever.Field[] fieldRetrievers = new DependencyRetriever.Field[pass.dependencies().size()];
            InterpolatingFieldSampler[] interpolators = new InterpolatingFieldSampler[pass.dependencies().size()];
            ImmutableList<Synthesizer.Dependency> passDependencies = pass.dependencies();
            for (int i = 0; i < passDependencies.size(); i++) {
                Synthesizer dependencySynthesizer = passDependencies.get(i).synthesizer();

                InterpolatingField dependencyField = resolvedFields.get(dependencySynthesizer);
                assert dependencyField != null;

                fieldRetrievers[i] = new DependencyRetriever.Field(chunkMinX, chunkMinY, chunkMinZ, dependencyField);
                try {
                    interpolators[i] = InterpolatingFieldSampler.create(dependencyField, field);
                } catch (Exception e) {
                    Clinker.LOGGER.info("{} {}", dependencySynthesizer.debugName, synthesizer.debugName);
                    throw e;
                }
            }

            SynthesizerContext context = new SynthesizerContext(fieldRetrievers, noises);
            SynthesizerFiller filler = new SynthesizerFiller(field,
                    chunkMinX - field.paddingBlocks, chunkMinY,
                    chunkMinZ - field.paddingBlocks,
                    interpolators, context, pass.function()
            );
            cellsFilled += field.fill(Math.max(pass.minY(), fromY) - chunkMinY, Math.min(pass.maxY(), toY) - chunkMinY, filler);
        }
        SynthesizerProfiler.INSTANCE.addTime(synthesizer.debugName, System.nanoTime() - startTime, cellsFilled);
        resolvedFields.put(synthesizer, field);
        return field;
    }

    NoiseSampler[] createNoiseSamplers(Synthesizer.Pass pass) {
        // create and seedify all the noises
        NoiseSampler[] noises = new NoiseSampler[pass.noises().size()];
        for (int i = 0; i < pass.noises().size(); i++) {
            NoiseProvider sampler = pass.noises().get(i);
            RandomSource randomSource = noiseRandom.fromHashOf(sampler.name());
            noises[i] = sampler.fromRandom(randomSource);
        }
        return noises;
    }

    // fills a field with data from a synthesizer.
    static final class SynthesizerFiller implements InterpolatingFieldFiller {
        final int yCellScale, xzCellSize, yCellSize;
        final int minX, minY, minZ;
        final InterpolatingFieldSampler[] interpolators;
        final SynthesizerContext context;
        final Synthesizer.Function func;

        int globalX, globalY, globalZ;

        SynthesizerFiller(InterpolatingField toFill, int minX, int minY, int minZ, InterpolatingFieldSampler[] interpolators, SynthesizerContext context, Synthesizer.Function func) {
            this.yCellScale = toFill.yCellScale;
            this.xzCellSize = toFill.xzCellSize;
            this.yCellSize = toFill.yCellSize;

            this.minX = minX; this.minY = minY; this.minZ = minZ;
            this.interpolators = interpolators;
            this.context = context;
            this.func = func;

            this.globalX = minX; this.globalY = minY; this.globalZ = minZ;
            context.x = minX; context.y = minY; context.z = minZ;
        }

        @Override
        public float compute(float currentVal, int x, int y, int z) {
            for (int i = 0; i < interpolators.length; i++) {
                InterpolatingFieldSampler field = interpolators[i];
                context.dependencyValues[i] = field.sample();
            }
            context.oldValue = currentVal;
            return func.compute(context);
        }

        // see InterpolatingFieldSampler for more info on these
        @Override
        public void advanceX() {
            globalX += xzCellSize;
            context.x = globalX;
            for (InterpolatingFieldSampler synthesizerField : interpolators)
                synthesizerField.advanceX();
        }
        @Override
        public void advanceZ() {
            globalX = minX;
            context.x = globalX;

            globalZ += xzCellSize;
            context.z = globalZ;
            for (InterpolatingFieldSampler synthesizerField : interpolators)
                synthesizerField.advanceZ();
        }
        @Override
        public void advanceY() {
            globalX = minX; globalZ = minZ;
            context.x = globalX; context.z = globalZ;

            globalY += yCellSize;
            context.y = globalY;
            for (InterpolatingFieldSampler synthesizerField : interpolators)
                synthesizerField.advanceY();
        }
        @Override
        public void setSlice(int cellY) {
            globalX = minX; globalZ = minZ;
            context.x = globalX; context.z = globalZ;

            globalY = (cellY << yCellScale) + minY;
            context.y = globalY;
            for (InterpolatingFieldSampler synthesizerField : interpolators)
                synthesizerField.setSlice(cellY);
        }
    }
}
