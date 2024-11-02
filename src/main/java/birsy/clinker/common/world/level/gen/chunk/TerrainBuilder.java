package birsy.clinker.common.world.level.gen.chunk;

import birsy.clinker.common.world.level.gen.NoiseFieldWithOffset;
import birsy.clinker.common.world.level.gen.NoiseSampler;
import birsy.clinker.common.world.level.gen.chunk.biome.TerrainProviders;
import birsy.clinker.common.world.level.gen.chunk.biome.terrainprovider.TerrainProvider;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

// blends the results from many terrain providers...
public class TerrainBuilder {
    protected final int blendRadius;
    //protected final Map<Holder<Biome>, Float> biomeContributions;
    protected final Function<Float, Float> kernel;
    protected final NoiseSampler sampler;

    public TerrainBuilder(int blendRadius, Function<Float, Float> kernel, NoiseSampler sampler) {
        this.blendRadius = blendRadius;
        this.kernel = kernel;
        this.sampler = sampler;
        //this.biomeContributions = new HashMap<>(this.blendRadius * this.blendRadius);
    }

    public void populateNoiseField(LevelAccessor level, ChunkAccess chunk, long seed, NoiseFieldWithOffset noiseField) {
        if (level == null) return;

        noiseField.fill((currentValue, x, y, z, params) -> {
            // HATE. LET ME TELL YOU HOW MUCH I'VE COME TO HATE-
            try {
                return calculateTerrainDensity(level, chunk, seed, x, y, z);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected float calculateTerrainDensity(LevelAccessor level, ChunkAccess chunk, long seed, double x, double y, double z) throws ExecutionException {
        int blockX = Mth.floor(x), blockY = Mth.floor(y), blockZ = Mth.floor(z);
        int biomeX = QuartPos.fromBlock(blockX), biomeY = QuartPos.fromBlock(blockY), biomeZ = QuartPos.fromBlock(blockZ);
        //Holder<Biome> biome = chunk.getNoiseBiome(biomeX, biomeY, biomeZ);
        //TerrainProvider provider = TerrainProviders.getTerrainProvider(biome.unwrapKey().get().location());
        //return provider.sample(chunk, seed, x, y, z, this.sampler);
        Map<Holder<Biome>, Float> biomeContributions = new HashMap<>();

        float totalContribution = 0.0F;
        for (int xo = -blendRadius; xo < blendRadius; xo++) {
            for (int yo = -blendRadius; yo < blendRadius; yo++) {
                for (int zo = -blendRadius; zo < blendRadius; zo++) {
                    int bx = biomeX + xo, by = biomeY + yo, bz = biomeZ + zo;
                    // kernel takes in the normalized distance
                    float contribution = kernel.apply(Mth.sqrt(xo*xo + yo*yo + zo*zo) / blendRadius);
                    totalContribution += contribution;

                    Holder<Biome> biomeAtPos = level.getNoiseBiome(bx, by, bz);
                    if (biomeContributions.containsKey(biomeAtPos)) {
                        biomeContributions.replace(biomeAtPos, biomeContributions.get(biomeAtPos) + contribution);
                    } else {
                        biomeContributions.put(biomeAtPos, contribution);
                    }
                }
            }
        }

        float finalNoiseValue = 0;
        for (Map.Entry<Holder<Biome>, Float> holderFloatEntry : biomeContributions.entrySet()) {
            float contribution = holderFloatEntry.getValue() / totalContribution;
            ResourceLocation biomeLocation = holderFloatEntry.getKey().unwrapKey().get().location();

            TerrainProvider provider = TerrainProviders.getTerrainProvider(biomeLocation);
            finalNoiseValue += provider.sample(chunk, seed, x, y, z, this.sampler) * contribution;
        }

        biomeContributions.clear();
        return finalNoiseValue;
    }
}
