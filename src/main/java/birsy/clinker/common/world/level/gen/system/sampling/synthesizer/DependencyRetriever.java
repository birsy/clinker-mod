package birsy.clinker.common.world.level.gen.system.sampling.synthesizer;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import net.minecraft.util.Mth;

// retrieves information from a synthesizer's dependency directly
// slower than the InterpolatingFieldSampler approach, but more robust
// to be used sparingly.
public interface DependencyRetriever {
    double retrieve(double x, double y, double z);

    // todo: this
    // ideally, synthesizers have the same retrieval capabilities no matter what...
    // but i'm not sure exactly how to achieve that. some kind of cached chain constructed
    // via the SynthesizerCache, all referencing each other.
    // slow and bad but that's ok
    record Direct(Synthesizer synthesizer) implements DependencyRetriever {
        @Override
        public double retrieve(double x, double y, double z) {
            return 0;
        }
    }

    // wrapper for an interpolating field
    record Field(int minX, int minY, int minZ, InterpolatingField field) implements DependencyRetriever {
        @Override
        public double retrieve(double x, double y, double z) {
            int lX = (int) Math.round(x - minX), lY = (int) Math.round(y - minX), lZ = (int) Math.round(z - minX);
            // clamp
            lX = Mth.clamp(lX, -field.paddingBlocks, 16 + field.paddingBlocks);
            lZ = Mth.clamp(lZ, -field.paddingBlocks, 16 + field.paddingBlocks);
            lY = Mth.clamp(lY, 0, field.maxY);
            return field.retrieve(lX, lY, lZ);
        }
    }
}
