package birsy.clinker.common.world.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MortarBlock extends Block {
    private static final VoxelShape OUTSIDE = box(3.0, 0.0, 3.0, 13.0, 5.0, 13.0);
    private static final VoxelShape INSIDE = box(4.0, 1.0, 4.0, 12.0, 5.0, 12.0);
    public static final VoxelShape SHAPE = Shapes.join(OUTSIDE, INSIDE, BooleanOp.ONLY_FIRST);
    public static final MapCodec<MortarBlock> CODEC = simpleCodec(MortarBlock::new);

    public MortarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return INSIDE;
    }

    @Override
    public MapCodec<MortarBlock> codec() {
        return CODEC;
    }
}
