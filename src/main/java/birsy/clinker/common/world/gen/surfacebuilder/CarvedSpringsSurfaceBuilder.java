package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.surfacebuilders.NetherForestsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.util.Random;

public class CarvedSpringsSurfaceBuilder extends AshSteppesSurfaceBuilder {
	public CarvedSpringsSurfaceBuilder(Codec<SurfaceBuilderConfig> surfaceBuilderConfig) {
		super(surfaceBuilderConfig);
	}

	@Override
	public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
		SurfaceBuilderConfig carvedConfig = new SurfaceBuilderConfig(ClinkerBlocks.SCORSTONE.get().getDefaultState(), ClinkerBlocks.COBBLED_SCORSTONE.get().getDefaultState(), ClinkerBlocks.SCORSTONE.get().getDefaultState());

		for (int y = startHeight; y >= 0; y--) {
			if (y > 100) {
				super.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
			} else {
				if (chunkIn.getTopBlockY(Heightmap.Type.WORLD_SURFACE, x, z) == y) {
					super.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, carvedConfig);
				}
			}
		}
	}
}
