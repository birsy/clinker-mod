package birsy.clinker.common.world.level.gen.system.surface.shape;

import birsy.clinker.common.world.level.gen.system.biome.BiomeGenerationInfo;
import birsy.clinker.common.world.level.gen.system.biome.placement.BiomeCache2d;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.core.util.MathUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.*;

public class SurfaceShapeSystem {
    public static final int SEARCH_RADIUS = QuartPos.fromBlock(32),
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

    private record BoundaryInfo(int closestQX, int closestQZ, boolean insideBiome) {}

    public Synthesizer createMasterSurfaceSynthesizer(ChunkAccess chunk, BiomeCache2d surfaceBiomes) {
        ChunkPos chunkPos = chunk.getPos();
        BiomeCache2d biomesSurfacesInChunk = surfaceBiomes.subsection(
                QuartPos.fromBlock(chunkPos.getMinBlockX()) - 3, QuartPos.fromBlock(chunkPos.getMinBlockZ()) - 3,
                QuartPos.fromBlock(chunkPos.getMaxBlockX()) + 3, QuartPos.fromBlock(chunkPos.getMaxBlockZ()) + 3
        );

        List<Holder<Biome>> allBiomes = List.copyOf(surfaceBiomes.containedBiomes());
        Long2ObjectMap<BoundaryInfo[]> boundaryCache = new Long2ObjectOpenHashMap<>();

        int minimumRange = Integer.MAX_VALUE, maximumRange = Integer.MIN_VALUE;
        List<Synthesizer> biomeSynthesizers = new ArrayList<>(allBiomes.size());
        for (int i = 0; i < allBiomes.size(); i++) {
            Holder<Biome> biome = allBiomes.get(i);
            Synthesizer distanceToBiome = createBiomeSDFSynthesizer(biome, i, allBiomes, surfaceBiomes, boundaryCache);

            BiomeGenerationInfo generationInfo = BiomeGenerationInfo.fromBiome(biome);
            SurfaceShape surfaceShape = generationInfo.shaper();

            biomeSynthesizers.add(surfaceShape.create(distanceToBiome, biomesSurfacesInChunk.containedBiomes.contains(biome)));
            minimumRange = Math.min(minimumRange, surfaceShape.minSurfaceY);
            maximumRange = Math.max(maximumRange, surfaceShape.maxSurfaceY);
        }

        Synthesizer[] dependencies = biomeSynthesizers.toArray(new Synthesizer[0]);
        return Synthesizer.builder()
                .withRange(minimumRange, maximumRange, 100)
                .withDependencies(dependencies)
                .build(InterpolatingFieldResolution.FINE,
                        ctx -> {
                            double dist = 100;
                            for (int i = 0; i < dependencies.length; i++) {
                                dist = MathUtils.smoothMin(dist, ctx.dependentValue(i), 3);
                            }
                            return dist;
                        }
                );
    }

    private Synthesizer createBiomeSDFSynthesizer(Holder<Biome> biome, int biomeIndex, List<Holder<Biome>> allBiomes,
                                                  BiomeCache2d surfaceBiomes,
                                                  Long2ObjectMap<BoundaryInfo[]> boundaryCache) {
        return Synthesizer.builder()
                .withNoises(FNLNoiseProvider.create("biomeOffset"))
                .build(InterpolatingFieldResolution.FINE_2D,
                        ctx -> {
                            int x = ctx.x(), z = ctx.z();
                            int startQX = QuartPos.fromBlock(x), startQZ = QuartPos.fromBlock(z);
                            long key = ((long) startQX << 32) | (startQZ & 0xFFFFFFFFL);

                            BoundaryInfo[] boundaries = boundaryCache.get(key);
                            if (boundaries == null) {
                                boundaries = scanAllBiomeBoundaries(startQX, startQZ, allBiomes, surfaceBiomes);
                                boundaryCache.put(key, boundaries);
                            }
                            BoundaryInfo info = boundaries[biomeIndex];

                            if (info.closestQX() == Integer.MAX_VALUE)
                                return SQR_SEARCH_RADIUS * (info.insideBiome() ? -1.0 : 1.0);

                            double biomeOffset = ctx.noise(0).sample(x * 0.05, z * 0.05);
                            return Mth.length(
                                    (x + biomeOffset * 4) - (QuartPos.toBlock(info.closestQX()) + 2),
                                    (z + biomeOffset * 4) - (QuartPos.toBlock(info.closestQZ()) + 2)
                            ) * (info.insideBiome() ? -1.0 : 1.0);
                        }
                );
    }

    private static BoundaryInfo[] scanAllBiomeBoundaries(int startQX, int startQZ, List<Holder<Biome>> targets, BiomeCache2d surfaceBiomes) {
        int n = targets.size();
        Holder<Biome> startBiome = surfaceBiomes.retrieve(startQX, startQZ);

        boolean[] insideBiome = new boolean[n];
        boolean[] resolved = new boolean[n];
        int[] closestQX = new int[n];
        int[] closestQZ = new int[n];
        Arrays.fill(closestQX, Integer.MAX_VALUE);

        for (int b = 0; b < n; b++) insideBiome[b] = (startBiome == targets.get(b));

        int remaining = n;
        for (int i = 0; i < SAMPLE_OFFSETS.length && remaining > 0; i += 2) {
            int qX = startQX + SAMPLE_OFFSETS[i], qZ = startQZ + SAMPLE_OFFSETS[i + 1];
            Holder<Biome> found = surfaceBiomes.retrieve(qX, qZ);

            for (int b = 0; b < n; b++) {
                if (resolved[b]) continue;
                boolean isBoundary = insideBiome[b] == (found != targets.get(b));
                if (isBoundary) {
                    closestQX[b] = qX;
                    closestQZ[b] = qZ;
                    resolved[b] = true;
                    remaining--;
                }
            }
        }

        BoundaryInfo[] result = new BoundaryInfo[n];
        for (int b = 0; b < n; b++) result[b] = new BoundaryInfo(closestQX[b], closestQZ[b], insideBiome[b]);
        return result;
    }
}
