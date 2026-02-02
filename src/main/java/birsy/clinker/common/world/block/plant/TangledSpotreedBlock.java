package birsy.clinker.common.world.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TangledSpotreedBlock extends RotatedPillarBlock {
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 1.0, 1.0, 16.0, 15.0, 15.0),
                                      Y_AXIS_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0),
                                      Z_AXIS_AABB = Block.box(1.0, 1.0, 0.0, 15.0, 15.0, 16.0);

    public TangledSpotreedBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case Z -> Z_AXIS_AABB;
            case Y -> Y_AXIS_AABB;
            default -> X_AXIS_AABB;
        };
    }
}
