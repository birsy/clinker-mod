package birsy.clinker.common.world.level.gen.system.sampling;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldSampler;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

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
    // todo: maybe move this somewhere else.
    public double atPoint(Synthesizer synthesizer, int x, int y, int z) {
        if (y < synthesizer.minY || y > synthesizer.maxY) return synthesizer.defaultValue;
        // todo: this
        return synthesizer.defaultValue;
    }

    // computes the values of a synthesizer within this chunk
    // simply request the amount of padding you want, and optionally some vertical bounds
    // and it'll return a nice, filled field for you.
    // todo: padding seems... weird. Investigate if it actually works
    //       theres gotta be some way to simplify all this using the resolvedDependencies.
    public InterpolatingField forThisChunk(Synthesizer synthesizer, int desiredXZPadding, int minY, int maxY) {
        int desiredXZScale = synthesizer.resolution.xzScale();
        for (Synthesizer.Dependency dependency : synthesizer.resolvedDependencies) {
            getOrCreateField(dependency.synthesizer(),
                    desiredXZPadding, dependency.xzScale(),
                    Math.max(minY, dependency.minY()),
                    Math.min(maxY, dependency.maxY())
            );
        }
        return getOrCreateField(synthesizer, desiredXZPadding, desiredXZScale, minY, maxY);
    }

    // only xz is padded, so we only have to do the weird ass scale propagation on that axis.
    InterpolatingField getOrCreateField(Synthesizer synthesizer, int desiredXZPadding, int desiredXZScale, int fromY, int toY) {
        InterpolatingField[] computedFields = fieldsBySynthesizerId.get(synthesizer.id);
        if (computedFields == null) {
            computedFields = new InterpolatingField[4]; // max possible scale == 4. see InterpolatingFieldType
            fieldsBySynthesizerId.put(synthesizer.id, computedFields);
        }

        InterpolatingField field = computedFields[desiredXZScale];
        if (field == null || field.paddingBlocks < desiredXZPadding) {
            // currently, this wastes some work if the field exists but at a different scale. i shouldn't do that....
            // todo: wrap synthesizers such that they try to reuse work from earlier scales?
            if (synthesizer.resolution.xzScale() >= desiredXZScale) {
                field = synthesizer.resolution.create(chunkHeight, desiredXZPadding);
            } else {
                field = InterpolatingFieldResolution.fromResolution(
                        desiredXZScale,
                        synthesizer.resolution.yScale(),
                        synthesizer.resolution.twoDimensional(),
                        chunkHeight, desiredXZPadding
                );
            }
        }
        fillNoiseField(synthesizer, field, fromY, toY, desiredXZPadding, desiredXZScale);
        return field;
    }

    // assumes all prior dependencies have been created.
    void fillNoiseField(Synthesizer synthesizer, InterpolatingField field, int desiredXZPadding, int desiredXZScale, int fromY, int toY) {
        int minXZScale = Math.max(desiredXZScale, synthesizer.resolution.xzScale());
        // create context
        InterpolatingFieldSampler[] interpolators = new InterpolatingFieldSampler[synthesizer.directDependencies.size()];
        ImmutableList<Synthesizer.Dependency> dependencies = synthesizer.directDependencies;
        for (int i = 0; i < dependencies.size(); i++) {
            // there has to be a better way of doing this.
            Synthesizer dependency = dependencies.get(i).synthesizer();
            InterpolatingField dependencyField = getOrCreateField(dependency, desiredXZPadding, minXZScale, fromY, toY);
            InterpolatingFieldSampler interpolator = InterpolatingFieldSampler.create(dependencyField, field);
            interpolators[i] = interpolator;
        }

        FastNoiseLite[] noises = new FastNoiseLite[synthesizer.noises.size()];
        RandomSource randomSource = worldRandom.at(synthesizer.id, 0, 0);
        for (int i = 0; i < synthesizer.noises.size(); i++) {
            noises[i] = synthesizer.noises.get(i).create(randomSource.nextLong());
        }
        CachedContext context = new CachedContext(interpolators, noises);

        // fill field...
        field.fill(fromY, toY, chunkMinX, chunkMinY, chunkMinZ, synthesizer.function, context);
    }

    static final class CachedContext implements Synthesizer.Context {
        private final InterpolatingFieldSampler[] synthesizerFields;
        private final FastNoiseLite[] noises;
        private final double[] synthesizerValues;

        CachedContext(InterpolatingFieldSampler[] synthesizerFields, FastNoiseLite[] noises) {
            this.synthesizerFields = synthesizerFields;
            this.noises = noises;
            this.synthesizerValues = new double[synthesizerFields.length];
        }

        @Override
        public double[] sampleDependencyValues() {
            for (int i = 0; i < synthesizerFields.length; i++) {
                InterpolatingFieldSampler field = synthesizerFields[i];
                synthesizerValues[i] = field.sample();
            }
            return synthesizerValues;
        }

        @Override public FastNoiseLite[] noises() { return noises; }
        @Override public void advanceX() { for (InterpolatingFieldSampler synthesizerField : synthesizerFields) synthesizerField.advanceX(); }
        @Override public void advanceY() { for (InterpolatingFieldSampler synthesizerField : synthesizerFields) synthesizerField.advanceY(); }
        @Override public void advanceZ() { for (InterpolatingFieldSampler synthesizerField : synthesizerFields) synthesizerField.advanceZ(); }
        @Override public void setSlice(int cellY) { for (InterpolatingFieldSampler synthesizerField : synthesizerFields) synthesizerField.setSlice(cellY); }
    }
}
