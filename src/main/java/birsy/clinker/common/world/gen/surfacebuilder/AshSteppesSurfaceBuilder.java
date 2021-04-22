package birsy.clinker.common.world.gen.surfacebuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.NetherForestsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class AshSteppesSurfaceBuilder extends NetherForestsSurfaceBuilder {

	public AshSteppesSurfaceBuilder(Codec<SurfaceBuilderConfig> surfaceBuilderConfig) {
		super(surfaceBuilderConfig);
	}

	@Override
	public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
		super.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);

		for (int y = startHeight; y >= 0; y--) {
			BlockPos pos = new BlockPos(x, y, z);

			if (isCliffside(chunkIn, pos)) {
				for (int h = 0; h < random.nextInt(2); h++) {
					chunkIn.setBlockState(pos.up(h), defaultBlock, false);
				}
			}
		}
	}

	private boolean isCliffside(IChunk chunkIn, BlockPos pos) {
		boolean flag = false;

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState state = chunkIn.getBlockState(pos.offset(direction).down());
			if (!state.isIn(Blocks.VOID_AIR) && !chunkIn.getBlockState(pos.offset(direction)).isIn(Blocks.VOID_AIR)) {
				flag = state.isSolid() && !chunkIn.getBlockState(pos.offset(direction)).isSolid();
			}
		}

		return flag && chunkIn.getBlockState(pos).isSolid();
	}
}
