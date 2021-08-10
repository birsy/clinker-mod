package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.surfacebuilders.NetherForestSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

import java.util.Random;
import java.util.stream.IntStream;

public class AshSteppesSurfaceBuilder extends NetherForestSurfaceBuilder {
	private PerlinSimplexNoise duneNoiseGenerator;
	private PerlinSimplexNoise bigDuneNoiseGenerator;

	private PerlinSimplexNoise altNoiseGenerator;

	public AshSteppesSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> surfaceBuilderConfig) {
		super(surfaceBuilderConfig);
	}

	@Override
	public void apply(Random random, ChunkAccess chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderBaseConfiguration config) {
		super.apply(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
		double distortX = altNoiseGenerator.getValue((z * 0.05) + 128, (x * 0.05) + 128, false) * 15;
		double distortZ = altNoiseGenerator.getValue((z * 0.05) + 256, (x * 0.05) + 256, false) * 15;

		float intensity = 15;
		final double baseDuneNoise = Math.abs(duneNoiseGenerator.getValue( (x + distortX) * 0.05, (z + distortZ) * 0.05, false));
		double duneNoise = MathUtils.bias((-1 * baseDuneNoise) + 0.5, 0.5) * intensity;

		double duneHeight = 2.8;

		double terrainHeight = MathUtils.terrace((float) bigDuneNoiseGenerator.getValue(x * 0.00125, z * 0.00125, false), (float) distortX / 15.0F, (float) distortZ / 15.0F,  1.0F).first * 18.0F;

		double finalDuneHeight = (duneNoise * duneHeight) + 60 + terrainHeight;

		for (int y = startHeight; y >= 0; y--) {
			BlockPos pos = new BlockPos(x, y, z);

			if (isCliffside(chunkIn, pos)) {
				for (int h = 0; h < random.nextInt(2); h++) {
					chunkIn.setBlockState(pos.above(h), defaultBlock, false);
				}
			}
		}

		for (int y = 256; y >= startHeight; y--) {
			BlockPos pos = new BlockPos(x, y, z);

			if (y < 105 + terrainHeight) {
				chunkIn.setBlockState(pos, defaultBlock, false);
			} else if (y < finalDuneHeight) {
				chunkIn.setBlockState(pos, ClinkerBlocks.ASH.get().defaultBlockState(), false);
			}
		}
	}

	private boolean isCliffside(ChunkAccess chunkIn, BlockPos pos) {
		boolean flag = false;

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState state = chunkIn.getBlockState(pos.relative(direction).below());
			if (!state.is(Blocks.VOID_AIR) && !chunkIn.getBlockState(pos.relative(direction)).is(Blocks.VOID_AIR)) {
				flag = state.canOcclude() && !chunkIn.getBlockState(pos.relative(direction)).canOcclude();
			}
		}

		return flag && chunkIn.getBlockState(pos).canOcclude();
	}

	@Override
	public void initNoise(long seed) {
		if (this.seed != seed || this.duneNoiseGenerator == null || this.bigDuneNoiseGenerator == null || this.altNoiseGenerator == null) {
			WorldgenRandom sharedseedrandom = new WorldgenRandom(seed);
			this.duneNoiseGenerator = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-1, 0));
			this.bigDuneNoiseGenerator = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-1, 0));

			this.altNoiseGenerator = new PerlinSimplexNoise(sharedseedrandom, IntStream.rangeClosed(-1, 0));
		}
		super.initNoise(seed);
	}
}
