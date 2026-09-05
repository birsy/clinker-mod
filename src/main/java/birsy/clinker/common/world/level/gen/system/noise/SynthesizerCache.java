package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.noise.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.noise.field.InterpolatingFieldSampler;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public class SynthesizerCache {
    // holds fields, sorted by xz scale
    final Int2ObjectMap<InterpolatingField[]> fieldsBySynthesizerId = new Int2ObjectOpenHashMap<>();
    final int chunkHeight; // height of the current chunk, in blocks
    final int chunkMinX, chunkMinY, chunkMinZ; // bottom south-west corner of the current chunk, in blocks
    final PositionalRandomFactory worldRandom;

    public SynthesizerCache(int chunkHeight, int chunkMinX, int chunkMinY, int chunkMinZ, PositionalRandomFactory worldRandom) {
        this.chunkHeight = chunkHeight;
        this.chunkMinX = chunkMinX;
        this.chunkMinY = chunkMinY;
        this.chunkMinZ = chunkMinZ;
        this.worldRandom = worldRandom;
    }

    // todo: padding seems... weird. Investigate if it actually works
    //       theres gotta be some way to simplify all this using the resolvedDependencies.
    public InterpolatingField compute(Synthesizer synthesizer, int desiredXZPadding, int minY, int maxY) {
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
        if (field == null || field.paddingBlocks <= desiredXZPadding) {
            // currently, this wastes some work if the field exists but at a different scale. i shouldn't do that....
            // todo: wrap synthesizers such that they try to reuse work from earlier scales?
            if (synthesizer.resolution.xzScale() >= desiredXZScale) {
                field = synthesizer.resolution.create(chunkHeight, 1);
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
            Synthesizer dependency = dependencies.get(i).synthesizer();
            InterpolatingField dependencyField = getOrCreateField(dependency, fromY, toY, desiredXZPadding, minXZScale);
            InterpolatingFieldSampler interpolator = new InterpolatingFieldSampler(dependencyField, field);
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
        public double[] synthesizerValues() {
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
