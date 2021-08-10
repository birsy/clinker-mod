package birsy.clinker.common.world.gen.surfacebuilder;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.gen.surfacebuilders.NetherForestsSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

import java.util.Random;

public class CarvedSpringsSurfaceBuilder extends AshSteppesSurfaceBuilder {
	public CarvedSpringsSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> surfaceBuilderConfig) {
		super(surfaceBuilderConfig);
	}

	@Override
	public void apply(Random random, ChunkAccess chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderBaseConfiguration config) {
		SurfaceBuilderBaseConfiguration carvedConfig = new SurfaceBuilderBaseConfiguration(ClinkerBlocks.SCORSTONE.get().defaultBlockState(), ClinkerBlocks.COBBLED_SCORSTONE.get().defaultBlockState(), ClinkerBlocks.SCORSTONE.get().defaultBlockState());

		for (int y = startHeight; y >= 0; y--) {
			if (y > 100) {
				super.apply(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
			} else {
				if (chunkIn.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) == y) {
					super.apply(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, carvedConfig);
				}
			}
		}
	}
}
