package birsy.clinker.common.world.level.gen.system.surface.shape;

import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public class SurfaceShapeSystem {
    public static final int SEARCH_RADIUS = QuartPos.fromBlock(32),
            SQR_SEARCH_RADIUS = SEARCH_RADIUS * SEARCH_RADIUS;
    // triplet of x offset, z offset, square distance
    private static final int[] SAMPLE_OFFSETS = Util.make(() -> {
        List<int[]> temp = new ArrayList<>();
        for (int offsetQZ = -SEARCH_RADIUS; offsetQZ <= SEARCH_RADIUS; offsetQZ++) {
            for (int offsetQX = -SEARCH_RADIUS; offsetQX <= SEARCH_RADIUS; offsetQX++) {
                int distSq = offsetQX * offsetQX + offsetQZ * offsetQZ;
                if (distSq <= SQR_SEARCH_RADIUS) {
                    temp.add(new int[]{offsetQX, offsetQZ, distSq});
                }
            }
        }
        temp.sort(Comparator.comparingInt(a -> a[2]));

        int[] flattened = new int[temp.size() * 3];
        for (int i = 0; i < temp.size(); i++) {
            flattened[i * 3 + 0] = temp.get(i)[0];
            flattened[i * 3 + 1] = temp.get(i)[1];
            flattened[i * 3 + 2] = temp.get(i)[2];
        }
        return flattened;
    });

    public Synthesizer createMasterSurfaceSynthesizer(BiomeCache2d surfaceBiomes) {
        int minimumRange = Integer.MAX_VALUE, maximumRange = Integer.MIN_VALUE;
        List<Synthesizer> biomeSynthesizers = new ArrayList<>(surfaceBiomes.containedBiomes.size());
        for (Holder<Biome> biome : surfaceBiomes.containedBiomes()) {
            Synthesizer distanceToBiome = Synthesizer.builder().build(
                    InterpolatingFieldResolution.FINE_2D,
                    ctx -> {
                        int x = ctx.x(), z = ctx.z();
                        int startQX = QuartPos.fromBlock(x), startQZ = QuartPos.fromBlock(z);
                        boolean insideBiome = surfaceBiomes.retrieve(startQX, startQZ) == biome;

                        // find the closest matching biome
                        int closestDistSq = SQR_SEARCH_RADIUS;
                        for (int i = 0; i < SAMPLE_OFFSETS.length; i += 3) {
                            int qX = startQX + SAMPLE_OFFSETS[i + 0];
                            int qZ = startQZ + SAMPLE_OFFSETS[i + 1];
                            int distSq = SAMPLE_OFFSETS[i + 2];

                            if (closestDistSq < distSq) continue;
                            Holder<Biome> offsetBiome = surfaceBiomes.retrieve(qX, qZ);
                            if ((insideBiome && offsetBiome != biome) || (!insideBiome && offsetBiome == biome)) {
                                closestDistSq = distSq;
                            }
                        }

                        return Math.sqrt(closestDistSq) * (insideBiome ? -4 : 4);
                    }
            );

            // just a prototype - give each biome a random height lol
            int biomeHeight = Math.abs(biome.getRegisteredName().length()) * 5 + 10;
            int minBiomeRange = biomeHeight - 10, maxBiomeRange = biomeHeight + 10;
            Synthesizer biomeSynthesizer = Synthesizer.builder()
                    .withRange(minBiomeRange, maxBiomeRange, 100)
                    .withDependencies(distanceToBiome)
                    .withNoises(FNLNoiseProvider.create("biome"))
                    .build(InterpolatingFieldResolution.COARSE,
                        ctx -> {
                            double yDist = ctx.y() - biomeHeight;
                            double seaLevelFactor = biomeHeight > 100 ? Mth.clampedMap(ctx.y(), biomeHeight, 100, 0, 1) : 0;
                            double xzDist = ctx.dependentValue(0) - Math.sqrt(seaLevelFactor) * 20;
                            return MathUtils.smoothMax(yDist, xzDist, 5) - 2 + ctx.noise(0).sample(ctx.x() / 32.0, ctx.y() / 64.0, ctx.z() / 32.0);
                        }
                    );
            biomeSynthesizers.add(biomeSynthesizer);
            minimumRange = Math.min(minimumRange, minBiomeRange);
            maximumRange = Math.max(maximumRange, maxBiomeRange);
        }
        // build to array
        Synthesizer[] dependencies = biomeSynthesizers.toArray(new Synthesizer[0]);
        return Synthesizer.builder()
                .withRange(minimumRange, maximumRange, 100)
                .withDependencies(dependencies)
                .build(InterpolatingFieldResolution.COARSE,
                        ctx -> {
                            double dist = 100;
                            for (int i = 0; i < dependencies.length; i++) {
                                dist = MathUtils.smoothMin(dist, ctx.dependentValue(i), 3);
                            }
                            return dist;
                        }
                );
    }
}
