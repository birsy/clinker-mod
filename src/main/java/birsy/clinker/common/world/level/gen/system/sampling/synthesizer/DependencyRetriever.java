package birsy.clinker.common.world.level.gen.system.sampling.synthesizer;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;

// retrieves information from a synthesizer's dependency directly
// slower than the InterpolatingFieldSampler approach, but more robust
// to be used sparingly.
public interface DependencyRetriever {
    double retrieve(double x, double y, double z);

    // todo: this
    // ideally, synthesizers have the same retrieval capabilities no matter what...
    // but i'm not sure exactly how to achieve that. some kind of chain constructed via the SynthesizerCache.
    // slow and bad but that's ok
    record Direct(Synthesizer synthesizer) implements DependencyRetriever {
        @Override public double retrieve(double x, double y, double z) {
            return 0;
        }
    }

    // wrapper for an interpolating field
    record Field(int minX, int minY, int minZ, InterpolatingField field) implements DependencyRetriever {
        @Override public double retrieve(double x, double y, double z) {
            return field.retrieve((int) Math.round(x - minX), (int) Math.round(y - minX), (int) Math.round(z - minX));
        }
    }
}
