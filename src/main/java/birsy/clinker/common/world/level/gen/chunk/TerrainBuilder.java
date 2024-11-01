package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.common.world.level.gen.NoiseField;
import birsy.clinker.common.world.level.gen.NoiseFieldWithOffset;
import birsy.clinker.common.world.level.gen.NoiseSampler;
import birsy.clinker.common.world.level.gen.chunk.biome.TerrainProviders;
import birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider.TerrainProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// blends the results from many terrain providers...
public class TerrainBuilder {
    protected final int blendRadius;
    protected final Map<Holder<Biome>, Float> biomeContributions;
    protected final Function<Float, Float> kernel;
    protected final NoiseSampler sampler;

    public TerrainBuilder(int blendRadius, Function<Float, Float> kernel, NoiseSampler sampler) {
        this.blendRadius = blendRadius;
        this.kernel = kernel;
        this.sampler = sampler;
        this.biomeContributions = new HashMap<>(this.blendRadius * this.blendRadius);
    }

    public NoiseField populateNoiseField(WorldGenLevel level, ChunkAccess chunk, long seed, NoiseFieldWithOffset noiseField) {
        noiseField.fill((currentValue, x, y, z, params) -> calculateTerrainDensity(level, chunk, seed, x, y, z));
        return noiseField;
    }

    protected float calculateTerrainDensity(WorldGenLevel level, ChunkAccess chunk, long seed, double x, double y, double z) {
        int blockX = Mth.floor(x), blockY = Mth.floor(y), blockZ = Mth.floor(z);
        int biomeX = QuartPos.fromBlock(blockX), biomeY = QuartPos.fromBlock(blockY), biomeZ = QuartPos.fromBlock(blockZ);

        for (int xo = -blendRadius; xo < blendRadius; xo++) {
            for (int yo = -blendRadius; yo < blendRadius; yo++) {
                for (int zo = -blendRadius; zo < blendRadius; zo++) {
                    int bx = biomeX + xo, by = biomeY + yo, bz = biomeZ + zo;

                    // kernel takes in the normalized distance
                    float contribution = kernel.apply(Mth.sqrt(xo*xo + yo*yo + zo*zo) / blendRadius);
                    biomeContributions.put(level.getNoiseBiome(bx, by, bz), contribution);
                }
            }
        }

        float finalNoiseValue = 0;
        for (Map.Entry<Holder<Biome>, Float> holderFloatEntry : biomeContributions.entrySet()) {
            float contribution = holderFloatEntry.getValue();
            ResourceLocation biomeLocation = holderFloatEntry.getKey().unwrapKey().get().location();

            TerrainProvider provider = TerrainProviders.getTerrainProvider(biomeLocation);
            finalNoiseValue += provider.sample(level, chunk, seed, x, y, z, this.sampler) * contribution;
        }

        biomeContributions.clear();
        return finalNoiseValue;
    }
}
