package birsy.clinker.common.world.level.gen.system.biome;

import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.HashSet;
import java.util.Set;

public class Voronoi2dClimateZone extends ClimateZone {
    private BiomeSelectorEntry voidBiome;

    final int cellSize, halfCellSize;
    final Set<Holder<Biome>> possibleBiomes;
    final SimpleWeightedRandomList<BiomeSelectorEntry> biomeSelector;

    final int maxCacheSize = 256;
    private final ThreadLocal<Long2ObjectLinkedOpenHashMap<CellInfo>> cache;

    public Voronoi2dClimateZone(HolderGetter<Biome> biomeGetter, int cellSize, BiomeEntry... biomes) {
        super(biomeGetter);
        this.voidBiome = new BiomeSelectorEntry(biomeGetter.getOrThrow(Biomes.THE_VOID), 0);

        this.cellSize = cellSize;
        this.halfCellSize = cellSize / 2;
        this.cache = ThreadLocal.withInitial(Long2ObjectLinkedOpenHashMap::new);

        SimpleWeightedRandomList.Builder<BiomeSelectorEntry> selectorBuilder = SimpleWeightedRandomList.builder();
        this.possibleBiomes = HashSet.newHashSet(biomes.length);
        for (BiomeEntry biomeEntry : biomes) {
            Holder<Biome> biome = biomeGetter.getOrThrow(biomeEntry.biome());
            selectorBuilder.add(
                    new BiomeSelectorEntry(biome, biomeEntry.sizeMultiplier() * biomeEntry.sizeMultiplier()),
                    biomeEntry.weight);
            this.possibleBiomes.add(biome);
        }
        this.biomeSelector = selectorBuilder.build();
    }

    @Override
    public Set<Holder<Biome>> collectPossibleBiomes() {
        return possibleBiomes;
    }

    @Override
    public Holder<Biome> getBiome(int quartPosX, int quartPosY, int quartPosZ, NoiseFieldCache noiseExecutor, RandomState randomState) {
//        int blockX = QuartPos.toBlock(quartPosX),
//            blockZ = QuartPos.toBlock(quartPosZ);
//        int noiseOffset = (int)(noiseExecutor.compute(blockX, 0, blockZ, OthershoreNoiseComputers.BASE_NOISE_2D[7]) * 32);
//        blockX += noiseOffset; blockZ += noiseOffset;
//
//        int cellX = getCellCoordinate(blockX),
//            cellZ = getCellCoordinate(blockZ);
//        CellInfo closestCellInfo = null;
//        long closestCellDistance = Long.MAX_VALUE;
//        for (int xOffset = -1; xOffset <= 1; xOffset++) {
//            for (int zOffset = -1; zOffset <= 1; zOffset++) {
//                CellInfo cellInfo = getCellInfo(cellX + xOffset, cellZ + zOffset, randomState);
//                long dx = cellInfo.centerX() - blockX;
//                long dz = cellInfo.centerZ() - blockZ;
//                long distance = dx * dx + dz * dz;
//                distance = (long) (distance * cellInfo.biome().sizeMultiplierSquared());
//                if (distance < closestCellDistance) {
//                    closestCellInfo = cellInfo;
//                    closestCellDistance = distance;
//                }
//            }
//        }
//        return closestCellInfo.biome().biome();
        return voidBiome.biome();
    }

    protected int getCellCoordinate(int blockCoordinate) {
        return Math.floorDiv(blockCoordinate, cellSize);
    }

    protected CellInfo getCellInfo(int cellX, int cellZ, RandomState randomState) {
        long packedID = ChunkPos.asLong(cellX, cellZ);
        Long2ObjectLinkedOpenHashMap<CellInfo> threadCache = cache.get();
        CellInfo cellInfo = threadCache.get(packedID);
        if (cellInfo == null) {
            cellInfo = createCellInfo(cellX, cellZ, randomState);
            threadCache.put(packedID, cellInfo);
            if (threadCache.size() > maxCacheSize)
                threadCache.removeFirst();
        }
        return cellInfo;
    }

    protected CellInfo createCellInfo(int cellX, int cellZ, RandomState randomState) {
        RandomSource randomSource = randomState.random.at(cellX, 0, cellZ);
        int xOffset = randomSource.nextInt(cellSize) - halfCellSize,
            zOffset = randomSource.nextInt(cellSize) - halfCellSize;
        BiomeSelectorEntry biome = this.biomeSelector.getRandomValue(randomSource).orElse(voidBiome);
        return new CellInfo(biome, cellX * cellSize + xOffset, cellZ * cellSize + zOffset);
    }

    public record BiomeEntry(ResourceKey<Biome> biome, double sizeMultiplier, int weight) {}
    protected record BiomeSelectorEntry(Holder<Biome> biome, double sizeMultiplierSquared) {}
    protected record CellInfo(BiomeSelectorEntry biome, int centerX, int centerZ) {}
}
