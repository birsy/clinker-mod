package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldType;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class Synthesizer {
    public static final AtomicInteger NEXT_ID = new AtomicInteger(0);

    public final int id = NEXT_ID.get();
    public final ImmutableList<Synthesizer> dependencies;
    public final ImmutableList<FastNoiseLite> noises;

    public final NoiseFieldType fieldType;
    public final Synthesizer.Function function;

    public Synthesizer(NoiseFieldType fieldType, Synthesizer.Function function, ImmutableList<Synthesizer> dependencies, ImmutableList<FastNoiseLite> noises) {
        this.dependencies = dependencies;
        this.noises = noises;
        this.fieldType = fieldType;
        this.function = function;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Synthesizer) obj;
        return this.id == that.id;
    }
    @Override
    public int hashCode() {
        return id;
    }

    public static class Builder {
        List<Synthesizer> dependencies = new ArrayList<>();
        List<FastNoiseLite> requiredNoises = new ArrayList<>();

        public Builder() {}

        public Builder addDependencies(Synthesizer... synthesizers) {
            Collections.addAll(dependencies, synthesizers);
            return this;
        }
        public Builder addNoises(FastNoiseLite... noises) {
            Collections.addAll(requiredNoises, noises);
            return this;
        }
        public Synthesizer build(NoiseFieldType fieldType, Synthesizer.Function function) {
            return new Synthesizer(fieldType, function, ImmutableList.copyOf(dependencies), ImmutableList.copyOf(requiredNoises));
        }
    }

    public interface Context {
        double retrieveSynth(int synthesizerIndex, int x, int y, int z);
        double retrieveNoise(int noiseIndex, double x, double y, double z);
    }

    public interface Function {
        double compute(int x, int y, int z, Context context);
    }
}
