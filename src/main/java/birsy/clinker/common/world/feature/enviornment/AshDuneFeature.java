package birsy.clinker.common.world.feature.enviornment;

import java.util.Random;

import com.mojang.serialization.Codec;

import birsy.clinker.common.block.AshLayerBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class AshDuneFeature extends Feature<NoFeatureConfig> {

	public AshDuneFeature(Codec<NoFeatureConfig> FeatureConfig) {
		super(FeatureConfig);
	}

	@Override
	public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		if (pos.getY() < 5) {
			return false;
		} else {
			int baseHeight = rand.nextInt(6) + 2;
			fillWithAsh(reader, generator, rand, pos, baseHeight);			
			return true;
		}
	}
	
	public void fillWithAsh(ISeedReader worldIn, ChunkGenerator chunkIn, Random rand, BlockPos pos, int baseHeightIn) {
		Direction[] directionArray = {Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};
		
		BlockState ashLayers = ClinkerBlocks.ASH_LAYER.get().getDefaultState();
		
		worldIn.setBlockState(pos, ashLayers.with(AshLayerBlock.LAYERS, baseHeightIn), 4);
		
		
		for (int i = baseHeightIn; i > 0; --i) {
			int distanceToMove = baseHeightIn - i;
			
			//int randVar = rand.nextInt(1);
			int height = i;
			
			for(Direction direction : directionArray) {
				BlockPos placementPos = getPlacementPosition(worldIn, pos, direction, distanceToMove);
				if (placementPos != null) {
					worldIn.setBlockState(placementPos, ashLayers.with(AshLayerBlock.LAYERS, height), 4);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public BlockPos getPlacementPosition(IWorld worldIn, BlockPos pos, Direction directionIn, int distanceToMove) {
		BlockPos placementPos = pos.offset(directionIn, distanceToMove);
		
		for(int i = 0; i < 20; i++) {
			if(worldIn.getBlockState(placementPos.down()).isAir()) {
				placementPos = placementPos.down();
			} else {
				break;
			}
		}
		
		if (worldIn.getBlockState(placementPos.down()).isAir() || worldIn.getBlockState(placementPos).isSolid()) {
			return null;
		} else {
			return placementPos;
		}
	}
}
