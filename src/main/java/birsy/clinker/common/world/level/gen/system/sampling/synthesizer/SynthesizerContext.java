package birsy.clinker.common.world.level.gen.system.sampling.synthesizer;

import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseSampler;
import birsy.clinker.core.Clinker;

import java.util.Arrays;

// provides parameters for a Synthesizer function
public final class SynthesizerContext {
    final DependencyRetriever[] dependencyRetrievers;
    final float[] dependencyValues;
    final NoiseSampler[] noises;
    int x, y, z;
    float oldValue;

    public SynthesizerContext(DependencyRetriever[] dependencyRetrievers, NoiseSampler[] noises) {
        this.dependencyRetrievers = dependencyRetrievers;
        this.dependencyValues = new float[dependencyRetrievers.length];
        this.noises = noises;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int z() { return z; }

    public float oldValue() { return oldValue; }

    public float dependentValue(int i) { return dependencyValues[i]; }
    public NoiseSampler noise(int i) { return noises[i]; }

    public float retrieveFromDependency(int i, int x, int y, int z) {
        return dependencyRetrievers[i].retrieve(x, y, z);
    }
}
