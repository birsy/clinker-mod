package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RootGrassBlock extends BushBlock implements IGrowable, net.minecraftforge.common.IForgeShearable 
{
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
	
	public RootGrassBlock()
	{
		super(((Block.Properties.create(Material.TALL_PLANTS)
				.doesNotBlockMovement()
				.zeroHardnessAndResistance()
				.sound(SoundType.PLANT)
				)));
	}

	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		   return false;
	}

	@Override
	public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
	}
	
	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return state.isIn(ClinkerBlocks.ROOTED_ASH.get()) 
			|| state.isIn(ClinkerBlocks.ASH.get()) 
			|| state.isIn(ClinkerBlocks.PACKED_ASH.get()) 
			|| state.isIn(ClinkerBlocks.ROOTED_PACKED_ASH.get())
			|| state.isIn(ClinkerBlocks.ROCKY_PACKED_ASH.get())
			|| state.isIn(ClinkerBlocks.ROOTSTALK.get())
			|| state == ClinkerBlocks.ASH_LAYER.get().getDefaultState().with(AshLayerBlock.LAYERS, 8) 
			&& super.isValidGround(state, worldIn, pos);
	}
	
	/**
	 * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
	 */
	@Override
	public AbstractBlock.OffsetType getOffsetType() {
		 return AbstractBlock.OffsetType.XZ;
	}
}
