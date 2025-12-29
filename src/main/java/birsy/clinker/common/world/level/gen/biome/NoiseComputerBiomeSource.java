package birsy.clinker.common.world.level.gen.biome;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import birsy.clinker.common.world.level.gen.noise.NoiseHolderHolder;
import birsy.clinker.common.world.level.gen.noise.UncachedNoiseComputerExecutor;
import birsy.clinker.core.registry.world.ClinkerWorld;
import com.google.common.collect.Sets;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.RandomState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Set;

public abstract class NoiseComputerBiomeSource extends BiomeSource {

    public NoiseComputerBiomeSource(HolderGetter<Biome> biomeGetter) {}

    public abstract ClimateZone getClimateZone(int quartPosX, int quartPosY, int quartPosZ,
                                               NoiseComputerExecutor noiseExecutor);

    public Holder<Biome> getNoiseBiome(int quartPosX, int quartPosY, int quartPosZ,
                                       NoiseComputerExecutor noiseExecutor) {
        ClimateZone climateZone = getClimateZone(quartPosX, quartPosY, quartPosZ, noiseExecutor);
        return climateZone.getBiome(quartPosX, quartPosY, quartPosZ, noiseExecutor);
    }

    public Set<Holder<Biome>> getBiomesWithin(int x1, int y1, int z1,
                                              int x2, int y2, int z2,
                                              NoiseComputerExecutor executor) {
        int minX = QuartPos.fromBlock(Math.min(x1, x2)),
                minY = QuartPos.fromBlock(Math.min(y1, y2)),
                minZ = QuartPos.fromBlock(Math.min(z1, z2));
        int maxX = QuartPos.fromBlock(Math.max(x1, x2)) + 1,
                maxY = QuartPos.fromBlock(Math.max(y1, y2)) + 1,
                maxZ = QuartPos.fromBlock(Math.max(z1, z2)) + 1;
        Set<Holder<Biome>> set = Sets.newHashSet();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    set.add(this.getNoiseBiome(x, y, z, executor));
                }
            }
        }
        return set;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartPosX, int quartPosY, int quartPosZ, Climate.Sampler sampler) {
        return this.getNoiseBiome(quartPosX, quartPosY, quartPosZ, createNoiseComputerExecutor());
    }

    @Override
    public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int radius, Climate.Sampler sampler) {
        return this.getBiomesWithin(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius,
                createNoiseComputerExecutor()
        );
    }

    protected static NoiseComputerExecutor createNoiseComputerExecutor() {
        RandomState randomState = ServerLifecycleHooks.getCurrentServer()
                .getLevel(ClinkerWorld.OTHERSHORE)
                .getChunkSource().chunkMap.randomState();
        return new UncachedNoiseComputerExecutor(
                ((NoiseHolderHolder)(Object)randomState).clinker$noiseHolder()
        );
    }

    public interface ClimateZone {
        Set<Biome> getPossibleBiomes();
        Holder<Biome> getBiome(int quartPosX, int quartPosY, int quartPosZ, NoiseComputerExecutor noiseExecutor);
    }
}
