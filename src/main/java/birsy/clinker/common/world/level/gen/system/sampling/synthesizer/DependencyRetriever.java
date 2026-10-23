package birsy.clinker.common.world.level.gen.system.sampling.synthesizer;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.core.Clinker;

// should probably come up with a better name for this.
// retrieves information from a synthesizer's dependency directly
// slower than the InterpolatingFieldSampler approach, but more robust
// to be used sparingly.
public interface DependencyRetriever {
    float retrieve(int x, int y, int z);

    // awful
    record Direct(SynthesizerCache cache, Synthesizer synthesizer) implements DependencyRetriever {
        @Override
        public float retrieve(int x, int y, int z) {
            return cache.samplePoint(synthesizer, x, y, z);
        }
    }

    // wrapper for an interpolating field
    record Field(int minX, int minY, int minZ, InterpolatingField field) implements DependencyRetriever {
        @Override
        public float retrieve(int x, int y, int z) {
            return field.retrieve(x - minX, y - minY, z - minZ);
        }
    }
}
