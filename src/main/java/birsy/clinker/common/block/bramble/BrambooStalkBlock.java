package birsy.clinker.common.block.bramble;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

import VoxelShape;

public class BrambooStalkBlock extends BrambooLogBlock {
    protected static final VoxelShape SHAPE_X = Block.box(0.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
    protected static final VoxelShape SHAPE_Y = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    protected static final VoxelShape SHAPE_Z = Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 16.0D);

    public BrambooStalkBlock() {}

    public void grow(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        Direction.Axis axis = state.getValue(AXIS);

        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == axis) {
                BlockPos blockPos = pos.relative(direction);
                BlockState blockState = worldIn.getBlockState(blockPos);

                int age = 0;
                if (blockState.isAir()) {
                    worldIn.setBlock(blockPos, ClinkerBlocks.BRAMBOO_STALK.get().defaultBlockState().setValue(AXIS, axis), 2);
                    age += 8;
                }

                worldIn.setBlock(pos, ClinkerBlocks.BRAMBOO_STALK.get().defaultBlockState().setValue(AXIS, axis).setValue(PROPERTY_AGE, age), 4);
            }
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        switch (state.getValue(AXIS)) {
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
