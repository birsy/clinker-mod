package birsy.clinker.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SodiumLanternBlock extends LanternBlock {
    protected static final VoxelShape AABB = Shapes.or(
            Block.box(5.0, 0.0, 5.0, 11.0, 9.0, 11.0),
            Block.box(4.5, 0.0, 4.5, 11.5, 2.0, 11.5)
    );
    protected static final VoxelShape HANGING_AABB = Shapes.or(
            Block.box(5.0, 1.0, 5.0, 11.0, 10.0, 11.0),
            Block.box(4.5, 1.0, 4.5, 11.5, 3.0, 11.5)
    );

    public SodiumLanternBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? HANGING_AABB : AABB;
    }
}
