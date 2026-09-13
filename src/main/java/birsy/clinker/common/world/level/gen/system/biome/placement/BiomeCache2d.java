package birsy.clinker.common.world.level.gen.system.biome.placement;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.HashSet;
import java.util.Set;

public final class BiomeCache2d {
    public final int minQuartX, minQuartZ, maxQuartX, maxQuartZ;
    public final int sizeX, sizeZ;
    public final Holder<Biome>[] biomes;
    public final Set<Holder<Biome>> containedBiomes;

    public BiomeCache2d(int minQuartX, int minQuartZ, int maxQuartX, int maxQuartZ) {
        this.minQuartX = minQuartX; this.minQuartZ = minQuartZ;
        this.maxQuartX = maxQuartX; this.maxQuartZ = maxQuartZ;
        this.sizeX = maxQuartX - minQuartX; this.sizeZ = maxQuartZ - minQuartZ;
        this.biomes = new Holder[sizeX * sizeZ];
        this.containedBiomes = new HashSet<>(3);
    }

    public Holder<Biome> retrieve(int qX, int qZ) {
        int localX = qX - minQuartX, localZ = qZ - minQuartZ;
        return biomes[localX + localZ * sizeX];
    }

    public Set<Holder<Biome>> containedBiomes() {
        return containedBiomes;
    }

    public BiomeCache2d subsection(int minQX, int minQZ, int maxQX, int maxQZ) {
        minQX = Math.max(minQX, this.minQuartX); minQZ = Math.max(minQZ, this.minQuartZ);
        maxQX = Math.min(maxQX, this.maxQuartX); maxQZ = Math.min(maxQZ, this.maxQuartZ);

        BiomeCache2d sub = new BiomeCache2d(minQX, minQZ, maxQX, maxQZ);
        for (int qz = minQZ; qz < maxQZ; qz++) {
            for (int qx = minQX; qx < maxQX; qx++) {
                Holder<Biome> biome = retrieve(qx, qz);
                sub.biomes[(qx - minQX) + (qz - minQZ) * sub.sizeX] = biome;
                sub.containedBiomes.add(biome);
            }
        }
        return sub;
    }
}
