package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.core.util.noise.FastNoiseLite;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.SortedSet;

public class SynthesizerCache {
    final Int2ObjectMap<SortedSet<NoiseField>> fieldsBySynthesizerId = new Int2ObjectOpenHashMap<>();


    static class CachedContext implements Synthesizer.Context {
        final FieldInterpolator[] synthesizerFields;
        final FastNoiseLite[] noises;
        final int offsetX, offsetY, offsetZ;
        int currentX, currentY, currentZ;

        @Override
        public double retrieveSynth(int synthesizerIndex, double x, double y, double z) {
            if (Math.abs(x - currentX))
            return 0;
        }

        @Override
        public double retrieveNoise(int noiseIndex, double x, double y, double z) {
            return 0;
        }
    }
}
