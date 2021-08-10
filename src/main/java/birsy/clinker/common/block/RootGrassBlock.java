package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class RootGrassBlock extends BushBlock implements BonemealableBlock, net.minecraftforge.common.IForgeShearable 
{
	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
	
	public RootGrassBlock()
	{
		super(((Block.Properties.of(Material.REPLACEABLE_PLANT)
				.noCollission()
				.instabreak()
				.sound(SoundType.GRASS)
				)));
	}

	
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
	
	@Override
	public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state) {
		   return false;
	}

	@Override
	public void performBonemeal(ServerLevel worldIn, Random rand, BlockPos pos, BlockState state) {
	}
	
	@Override
	public boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return state.is(ClinkerBlocks.ROOTED_ASH.get()) 
			|| state.is(ClinkerBlocks.ASH.get()) 
			|| state.is(ClinkerBlocks.PACKED_ASH.get()) 
			|| state.is(ClinkerBlocks.ROOTED_PACKED_ASH.get())
			|| state.is(ClinkerBlocks.ROCKY_PACKED_ASH.get())
			|| state.is(ClinkerBlocks.ROOTSTALK.get())
			|| state == ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, 8) 
			&& super.mayPlaceOn(state, worldIn, pos);
	}

	public static boolean isValidGround(BlockState state) {
		return state.is(ClinkerBlocks.ROOTED_ASH.get())
				|| state.is(ClinkerBlocks.ASH.get())
				|| state.is(ClinkerBlocks.PACKED_ASH.get())
				|| state.is(ClinkerBlocks.ROOTED_PACKED_ASH.get())
				|| state.is(ClinkerBlocks.ROCKY_PACKED_ASH.get())
				|| state.is(ClinkerBlocks.ROOTSTALK.get())
				|| state == ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, 8);
	}

	/**
	 * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
	 */
	@Override
	public BlockBehaviour.OffsetType getOffsetType() {
		 return BlockBehaviour.OffsetType.XZ;
	}
}
