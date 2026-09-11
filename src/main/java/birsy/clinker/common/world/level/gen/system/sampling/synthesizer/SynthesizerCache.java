package birsy.clinker.common.world.level.gen.system.sampling.synthesizer;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldFiller;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldSampler;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import birsy.clinker.core.Clinker;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.Arrays;

// todo: find a better name for this
public class SynthesizerCache {
    // holds fields, sorted by xz scale
    final Int2ObjectMap<InterpolatingField[]> fieldsBySynthesizerId = new Int2ObjectOpenHashMap<>();
    final int chunkHeight; // height of the current chunk, in blocks
    final int chunkMinX, chunkMinY, chunkMinZ; // bottom south-west corner of the current chunk, in blocks
    final PositionalRandomFactory worldRandom;

    public SynthesizerCache(int chunkMinX, int chunkMinY, int chunkMinZ, int chunkHeight, PositionalRandomFactory worldRandom) {
        this.chunkMinX = chunkMinX;
        this.chunkMinY = chunkMinY;
        this.chunkMinZ = chunkMinZ;
        this.chunkHeight = chunkHeight;
        this.worldRandom = worldRandom;
    }

    // computes the value of a synthesizer at a single point.
    // todo: maybe move this somewhere else. also, make the function lol
    public double samplePoint(Synthesizer synthesizer, int x, int y, int z) {
        if (y < synthesizer.minY || y > synthesizer.maxY) return synthesizer.defaultValue;
        return synthesizer.defaultValue;
    }

    // computes the values of a synthesizer within this chunk
    // simply request the amount of padding you want, and optionally some vertical bounds
    // and it'll return a nice, filled field for you.
    // todo: padding seems... weird. Investigate if it actually works
    //       theres gotta be some way to simplify all this using the resolvedDependencies.
    public InterpolatingField sampleThisChunk(Synthesizer synthesizer, int desiredXZPadding, int minY, int maxY) {
        int desiredXZScale = synthesizer.resolution.xzScale();
        for (Synthesizer.Dependency dependency : synthesizer.resolvedDependencies) {
            getOrCreateField(dependency.synthesizer(),
                    desiredXZPadding, dependency.xzScale(),
                    Math.max(minY, dependency.minY()),
                    Math.min(maxY, dependency.maxY())
            );
        }
        return getOrCreateField(synthesizer,
                desiredXZPadding, desiredXZScale,
                Math.max(minY, synthesizer.minY),
                Math.min(maxY, synthesizer.maxY)
        );
    }

    // only xz is padded, so we only have to do the weird ass scale propagation on that axis.
    InterpolatingField getOrCreateField(Synthesizer synthesizer, int desiredXZPadding, int desiredXZScale, int fromY, int toY) {
        InterpolatingField[] computedFields = fieldsBySynthesizerId.get(synthesizer.id);
        if (computedFields == null) {
            computedFields = new InterpolatingField[5]; // max possible scale == 4. see InterpolatingFieldType
            fieldsBySynthesizerId.put(synthesizer.id, computedFields);
        }

        boolean fieldNeedsInitialization = false;
        InterpolatingField field = computedFields[desiredXZScale];
        if (field == null || field.paddingBlocks < desiredXZPadding) {
            fieldNeedsInitialization = true;
            // currently, this wastes some work if the field exists but at a different scale. i shouldn't do that....
            // todo: wrap synthesizers such that they try to reuse work from earlier scales?
            if (synthesizer.resolution.xzScale() >= desiredXZScale) {
                field = synthesizer.resolution.create(chunkHeight, desiredXZPadding);
                computedFields[desiredXZScale] = field;
            } else {
                field = InterpolatingFieldResolution.fromResolution(
                        desiredXZScale,
                        synthesizer.resolution.yScale(),
                        synthesizer.resolution.twoDimensional(),
                        chunkHeight, desiredXZPadding
                );
                computedFields[desiredXZScale] = field;
            }
        }

        fillNoiseField(synthesizer, field, desiredXZPadding, desiredXZScale, fromY, toY, fieldNeedsInitialization);
        return field;
    }

    // assumes all prior dependencies have been created.
    void fillNoiseField(Synthesizer synthesizer, InterpolatingField field, int desiredXZPadding, int desiredXZScale, int fromY, int toY, boolean fieldNeedsInitialization) {
        // initialize the field if it needs it
        if (fieldNeedsInitialization) Arrays.fill(field.array(), synthesizer.defaultValue);

        // return early if we're not in range
        if (toY <= fromY) return;

        int minXZScale = Math.max(desiredXZScale, synthesizer.resolution.xzScale());
        // create context
        DependencyRetriever.Field[] fieldRetrievers = new DependencyRetriever.Field[synthesizer.directDependencies.size()];
        InterpolatingFieldSampler[] interpolators = new InterpolatingFieldSampler[synthesizer.directDependencies.size()];
        ImmutableList<Synthesizer.Dependency> dependencies = synthesizer.directDependencies;
        for (int i = 0; i < dependencies.size(); i++) {
            // there has to be a better way of doing this.
            Synthesizer dependency = dependencies.get(i).synthesizer();
            InterpolatingField dependencyField = getOrCreateField(dependency, desiredXZPadding, minXZScale, fromY, toY);
            fieldRetrievers[i] = new DependencyRetriever.Field(chunkMinX, chunkMinY, chunkMinZ, dependencyField);
            interpolators[i] = InterpolatingFieldSampler.create(dependencyField, field);
        }

        NoiseSampler[] noises = new NoiseSampler[synthesizer.noises.size()];
        for (int i = 0; i < synthesizer.noises.size(); i++) {
            // seedify all the noises
            NoiseProvider sampler = synthesizer.noises.get(i);
            RandomSource randomSource = worldRandom.fromHashOf(sampler.name());
            noises[i] = sampler.fromSeed(randomSource.nextLong());
        }

        SynthesizerContext context = new SynthesizerContext(fieldRetrievers, noises);
        SynthesizerFiller filler = new SynthesizerFiller(
                field, chunkMinX - field.paddingBlocks, chunkMinY, chunkMinZ - field.paddingBlocks,
                interpolators, context, synthesizer.function
        );
        // fill field...
        field.fill(fromY - chunkMinY, toY - chunkMinY, filler);
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
        public double compute(int x, int y, int z) {
            for (int i = 0; i < interpolators.length; i++) {
                InterpolatingFieldSampler field = interpolators[i];
                context.dependencyValues[i] = field.sample();
            }
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
