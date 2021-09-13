package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.common.world.gen.OthershoreChunkGenerator;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.surfacebuilders.NetherForestsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;

public class OthershoreTerrainTest extends NetherForestsSurfaceBuilder {
	private long seed;
	private OthershoreChunkGenerator.OthershoreNoiseSampler noiseSampler;

	public OthershoreTerrainTest(Codec<SurfaceBuilderConfig> surfaceBuilderConfig) {
		super(surfaceBuilderConfig);
	}

	@Override
	public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
		/**
		float factor = ((noiseSampler.sampleFactorNoise(x, z) + 1) / 2) * 160;

		BlockPos.Mutable pos = new BlockPos.Mutable(x, 256, z);
		for (int y = 256; y > 0; y--) {
			pos.setY(y);
			float terrainNoise = noiseSampler.sampleTerrainNoise(x, y * 0.7F, z);
			int distanceBelowSeaLevel = y - seaLevel;
			terrainNoise -= (distanceBelowSeaLevel) / (y > seaLevel ? factor : factor / ((distanceBelowSeaLevel * -1.0F) / 9.0F));

			if (terrainNoise > 0) {
				chunkIn.setBlockState(pos, defaultBlock, false);
			} else {
				chunkIn.setBlockState(pos, y > seaLevel ? Blocks.AIR.getDefaultState() : defaultFluid, false);
			}
		}
		 */

		float continentalNoise = noiseSampler.sampleContinentalNoise(x, z);
		float ashPlainsSurface = (float) ((Math.pow(continentalNoise, 1.0F/9.0F) * (seaLevel - 5)) + (1.8F * seaLevel));
		float seabedSurface = (float) ((Math.pow(-continentalNoise, 1.0F/9.0F) * -(seaLevel - 5)) + (1.8F * seaLevel));

		final float continentalMaxHeight = (float) ((Math.pow(1.0F, 1.0F/9.0F) * (seaLevel - 5)) + (1.8F * seaLevel));
		final float continentalMidHeight = (float) ((Math.pow(0.0001F, 1.0F/9.0F) * (seaLevel - 5)) + (1.8F * seaLevel));
		final float continentalMinHeight = (float) ((Math.pow(1.0F, 1.0F/9.0F) * -(seaLevel - 5)) + (1.8F * seaLevel));
		final float overhangSize = 0.6F;

		BlockPos.Mutable pos = new BlockPos.Mutable(x, 256, z);
		for (int y = 256; y > 0; y--) {
			pos.setY(y);

			float threeDContinentalNoise = continentalNoise > 0 ? ashPlainsSurface : seabedSurface;
			threeDContinentalNoise = MathHelper.clamp((threeDContinentalNoise - y) * 0.0625F, 0.0F, 1.0F);
			threeDContinentalNoise *= Math.abs(continentalNoise);

			float continentalOverhangMask = MathHelper.clamp(y < continentalMidHeight ? MathUtils.mapRange(continentalMidHeight, continentalMaxHeight, 0.0F, 1.0F, y) : MathUtils.mapRange(continentalMinHeight, continentalMidHeight, 1.0F, 0.0F, y), 0.0F, 1.0F);

			float finalNoise = threeDContinentalNoise - continentalOverhangMask;

			if (finalNoise > 0) {
				chunkIn.setBlockState(pos, defaultBlock, false);
			} else {
				chunkIn.setBlockState(pos, y > seaLevel ? Blocks.AIR.getDefaultState() : defaultFluid, false);
			}

			Clinker.LOGGER.info("continental overhang mask is at " + continentalOverhangMask + " you absolute baboon.");
		}

		super.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
	}

	@Override
	public void setSeed(long seed) {
		if (this.seed != seed || this.noiseSampler == null) {
			this.seed = seed;
			this.noiseSampler = new OthershoreChunkGenerator.OthershoreNoiseSampler((int) seed);
		}

		super.setSeed(seed);
	}
}
