package birsy.clinker.common.world.gen.surfacebuilder;

import java.util.Random;
import java.util.stream.IntStream;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class AshSteppesSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
	private SimplexNoiseGenerator noiseGenerator;

	public AshSteppesSurfaceBuilder(Codec<SurfaceBuilderConfig> surfaceBuilderConfig) {
		super(surfaceBuilderConfig);
	}

	@Override
	public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
		if (noise > 1.75D) {
			if (noiseGenerator.getValue(x, z) > -0.95D) {
				SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, ClinkerSurfaceBuilderConfigs.ASH_CONFIG);
			} else {
				SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, ClinkerSurfaceBuilderConfigs.ROOTED_ASH_CONFIG);
			}
		} else {
			if (noiseGenerator.getValue(x, z) > -0.95D) {
				SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, ClinkerSurfaceBuilderConfigs.ASH_STEPPES_CONFIG);
			} else {
				SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, ClinkerSurfaceBuilderConfigs.ASH_STEPPES_ROOTED_CONFIG);
			}
		}
	}

	@Override
	public void setSeed(long seed) {
		if (this.noiseGenerator == null) {
			SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed + 30);
			this.noiseGenerator = new SimplexNoiseGenerator(sharedseedrandom);
		}
		super.setSeed(seed);
	}
}
