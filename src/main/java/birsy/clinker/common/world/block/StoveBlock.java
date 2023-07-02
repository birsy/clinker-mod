package birsy.clinker.common.world.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.ShapeUtil;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StoveBlock extends AbstractStoveBlock {
    private static VoxelShape SINGLE_STOVE_SHAPE = Util.make(() -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.1875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.1875, 0, 1, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.1875, 0.875, 0.875, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0, 1, 1, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0.75, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.625, 0.25, 1, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0.25, 0.3125, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0, 0.1875, 0.625, 1), BooleanOp.OR);
        return shape;
    });
    private static VoxelShape DOUBLE_STOVE_SHAPE = Util.make(() -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.1875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.1875, 0, 1, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.875, 0.8125, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0, 1, 1, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0.75, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.625, 0.25, 1, 1, 0.75), BooleanOp.OR);
        return shape;
    });

    public StoveBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected VoxelShape getSingleShape() {
        return SINGLE_STOVE_SHAPE;
    }

    @Override
    protected VoxelShape getDoubleShape() {
        return DOUBLE_STOVE_SHAPE;
    }


    @Override
    AbstractStoveBlock getType() {
        return (AbstractStoveBlock) ClinkerBlocks.STOVE.get();
    }

    @Override
    public boolean isValidAttachmentBlock(BlockState state, DoubleBlockHalf half) {
        return state.is(ClinkerBlocks.STOVE_CHIMNEY.get()) && (state.hasProperty(FACING) && state.getValue(FACING) == state.getValue(FACING));
    }

    @Override
    public BlockState getValidAttachmentBlock(BlockState state, DoubleBlockHalf half) {
        return ClinkerBlocks.STOVE_CHIMNEY.get().defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(TYPE, state.getValue(TYPE));
    }

    @Override
    public DoubleBlockHalf getHalf(BlockState state) {
        return DoubleBlockHalf.LOWER;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
    }
}
