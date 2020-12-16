package birsy.clinker.common.world.gen.surfacebuilder;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class AshSteppesSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
	public AshSteppesSurfaceBuilder(Codec<SurfaceBuilderConfig> surfaceBuilderConfig) {
		super(surfaceBuilderConfig);
	}

	public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
		if (noise > 1.75D) {
			SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, ClinkerSurfaceBuilderConfigs.ASH_STEPPES_ROOTED_CONFIG);
		} else {
			SurfaceBuilder.DEFAULT.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, ClinkerSurfaceBuilderConfigs.ASH_STEPPES_CONFIG);
		}
	}

}
