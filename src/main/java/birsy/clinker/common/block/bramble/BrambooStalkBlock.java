package birsy.clinker.common.block.bramble;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class BrambooStalkBlock extends BrambooLogBlock {
    protected static final VoxelShape SHAPE_X = Block.makeCuboidShape(0.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
    protected static final VoxelShape SHAPE_Y = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    protected static final VoxelShape SHAPE_Z = Block.makeCuboidShape(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 16.0D);

    public BrambooStalkBlock() {}

    public void grow(IWorld worldIn, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.get(AXIS);

        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == axis) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState = worldIn.getBlockState(blockPos);

                int age = 0;
                if (blockState.isAir()) {
                    worldIn.setBlockState(blockPos, ClinkerBlocks.BRAMBOO_STALK.get().getDefaultState().with(AXIS, axis), 2);
                    age += 8;
                }

                worldIn.setBlockState(pos, ClinkerBlocks.BRAMBOO_STALK.get().getDefaultState().with(AXIS, axis).with(PROPERTY_AGE, age), 4);
            }
        }
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(AXIS)) {
            case X:
                return SHAPE_X;
            case Y:
                return SHAPE_Y;
            case Z:
                return SHAPE_Z;
            default:
                return null;
        }
    }
}
