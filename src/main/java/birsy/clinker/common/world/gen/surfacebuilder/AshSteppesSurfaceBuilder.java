package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.NetherForestsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;
import java.util.stream.IntStream;

public class AshSteppesSurfaceBuilder extends NetherForestsSurfaceBuilder {
	private PerlinNoiseGenerator duneNoiseGenerator;
	private PerlinNoiseGenerator bigDuneNoiseGenerator;

	private PerlinNoiseGenerator altNoiseGenerator;

	public AshSteppesSurfaceBuilder(Codec<SurfaceBuilderConfig> surfaceBuilderConfig) {
		super(surfaceBuilderConfig);
	}

	@Override
	public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
		super.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
		double distortX = altNoiseGenerator.noiseAt((z * 0.05) + 128, (x * 0.05) + 128, false) * 15;
		double distortZ = altNoiseGenerator.noiseAt((z * 0.05) + 256, (x * 0.05) + 256, false) * 15;

		float intensity = 15;
		final double baseDuneNoise = Math.abs(duneNoiseGenerator.noiseAt( (x + distortX) * 0.05, (z + distortZ) * 0.05, false));
		double duneNoise = MathUtils.bias((-1 * baseDuneNoise) + 0.5, 0.5) * intensity;

		double duneHeight = 2.8;

		double terrainHeight = MathUtils.terrace((float) bigDuneNoiseGenerator.noiseAt(x * 0.00125, z * 0.00125, false), (float) distortX / 15.0F, (float) distortZ / 15.0F) * 18.0F;

		double finalDuneHeight = (duneNoise * duneHeight) + 110 + terrainHeight;

		for (int y = startHeight; y >= 0; y--) {
			BlockPos pos = new BlockPos(x, y, z);

			if (isCliffside(chunkIn, pos)) {
				for (int h = 0; h < random.nextInt(2); h++) {
					chunkIn.setBlockState(pos.up(h), defaultBlock, false);
				}
			}
		}

		for (int y = 256; y >= startHeight; y--) {
			BlockPos pos = new BlockPos(x, y, z);

			if (y < 105 + terrainHeight) {
				chunkIn.setBlockState(pos, defaultBlock, false);
			} else if (y < finalDuneHeight) {
				chunkIn.setBlockState(pos, ClinkerBlocks.ASH.get().getDefaultState(), false);
			}
		}
	}

	private boolean isCliffside(IChunk chunkIn, BlockPos pos) {
		boolean flag = false;

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState state = chunkIn.getBlockState(pos.offset(direction).down());
			if (!state.matchesBlock(Blocks.VOID_AIR) && !chunkIn.getBlockState(pos.offset(direction)).matchesBlock(Blocks.VOID_AIR)) {
				flag = state.isSolid() && !chunkIn.getBlockState(pos.offset(direction)).isSolid();
			}
		}

		return flag && chunkIn.getBlockState(pos).isSolid();
	}

	@Override
	public void setSeed(long seed) {
		if (this.seed != seed || this.duneNoiseGenerator == null || this.bigDuneNoiseGenerator == null || this.altNoiseGenerator == null) {
			SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
			this.duneNoiseGenerator = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-1, 0));
			this.bigDuneNoiseGenerator = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-1, 0));

			this.altNoiseGenerator = new PerlinNoiseGenerator(sharedseedrandom, IntStream.rangeClosed(-1, 0));
		}
		super.setSeed(seed);
	}
}
