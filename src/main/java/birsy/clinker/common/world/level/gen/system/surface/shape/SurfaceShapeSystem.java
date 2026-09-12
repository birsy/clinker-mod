package birsy.clinker.common.world.level.gen.system.surface.shape;

import birsy.clinker.common.world.level.gen.system.biome.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.VoronoiNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.WhiteNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public class SurfaceShapeSystem {
    public static final int SEARCH_RADIUS = QuartPos.fromBlock(64),
            SQR_SEARCH_RADIUS = SEARCH_RADIUS * SEARCH_RADIUS;
    // double of x offset, z offset
    private static final int[] SAMPLE_OFFSETS = Util.make(() -> {
        List<int[]> temp = new ArrayList<>();
        for (int qZ = -SEARCH_RADIUS; qZ <= SEARCH_RADIUS; qZ++) {
            for (int qX = -SEARCH_RADIUS; qX <= SEARCH_RADIUS; qX++) {
                if (Mth.lengthSquared(qX, qZ) <= SQR_SEARCH_RADIUS) {
                    temp.add(new int[]{qX, qZ});
                }
            }
        }
        // sort by distance
        temp.sort(Comparator.comparingDouble(a -> Mth.lengthSquared(a[0], a[1])));

        int[] flattened = new int[temp.size() * 2];
        for (int i = 0; i < temp.size(); i++) {
            flattened[i * 2 + 0] = temp.get(i)[0];
            flattened[i * 2 + 1] = temp.get(i)[1];
        }
        return flattened;
    });

    public int getBiomeCachePadding(int maxPadding) {
        return SEARCH_RADIUS + QuartPos.fromBlock(maxPadding) + 3;
    }

    public Synthesizer createMasterSurfaceSynthesizer(BiomeCache2d surfaceBiomes) {
        int minimumRange = Integer.MAX_VALUE, maximumRange = Integer.MIN_VALUE;
        List<Synthesizer> biomeSynthesizers = new ArrayList<>(surfaceBiomes.containedBiomes.size());
        for (Holder<Biome> biome : surfaceBiomes.containedBiomes()) {
            Synthesizer distanceToBiome = Synthesizer.builder()
                    .withNoises(FNLNoiseProvider.create("biomeOffset"))
                    .build(InterpolatingFieldResolution.FINE_2D,
                        ctx -> {
                            int x = ctx.x(), z = ctx.z();
                            int startQX = QuartPos.fromBlock(x), startQZ = QuartPos.fromBlock(z);
                            boolean insideBiome = surfaceBiomes.retrieve(startQX, startQZ) == biome;

                            // find the closest boundary cell
                            int closestQX = Integer.MAX_VALUE, closestQZ = Integer.MAX_VALUE;
                            for (int i = 0; i < SAMPLE_OFFSETS.length; i += 2) {
                                int qX = startQX + SAMPLE_OFFSETS[i], qZ = startQZ + SAMPLE_OFFSETS[i + 1];
                                Holder<Biome> found = surfaceBiomes.retrieve(qX, qZ);
                                boolean isBoundary = insideBiome ? found != biome : found == biome;
                                if (isBoundary) {
                                    closestQX = qX; closestQZ = qZ;
                                    break;
                                }
                            }
                            // no boundary found within search radius
                            if (closestQX == Integer.MAX_VALUE) return SQR_SEARCH_RADIUS * (insideBiome ? -1.0 : 1.0);
                            // block-space distance to center of the nearest boundary cell
                            double biomeOffset = ctx.noise(0).sample(x * 0.05, z * 0.05);
                            return Mth.length(
                                    (x + biomeOffset * 4) - (QuartPos.toBlock(closestQX) + 2),
                                    (z + biomeOffset * 4) - (QuartPos.toBlock(closestQZ) + 2)
                            ) * (insideBiome ? -1.0 : 1.0);
                        }
                );

            Synthesizer cliffYSynth = Synthesizer.builder()
                    .withDependencies(distanceToBiome)
                    .withNoises(VoronoiNoiseProvider.create("cliffiness", 1.0, false))
                    .build(new InterpolatingFieldResolution(4, 2),
                            ctx -> {
                                return ctx.noise(0).sampleSet(ctx.x() / 64.0, ctx.y() / 12.0, ctx.z() / 64.0)[1] * 12.0;
                            }
                    );

            // just a prototype - give each biome a random height lol
            int biomeHeight = Math.abs(biome.getRegisteredName().length()) * 5 + 10;
            int minBiomeRange = biomeHeight - 10, maxBiomeRange = biomeHeight + 10;
            Synthesizer biomeSynthesizer = Synthesizer.builder()
                    .withRange(minBiomeRange, maxBiomeRange, 100)
                    .withDependencies(distanceToBiome, cliffYSynth)
                    .withNoises(FNLNoiseProvider.create("biome"))
                    .build(InterpolatingFieldResolution.COARSE,
                        ctx -> {
                            double yDist = ctx.y() - biomeHeight;
                            double seaLevelFactor = biomeHeight > 100 ? Mth.clampedMap(ctx.dependentValue(1), biomeHeight, 100, 0, 1) : 0;
                            double xzDist = ctx.dependentValue(0) - seaLevelFactor * 20;
                            return MathUtils.smoothMax(yDist, xzDist, 5) - 2
                                    + ctx.noise(0).sample(ctx.x() * 0.02, ctx.y() * 0.02, ctx.z() * 0.02);
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
