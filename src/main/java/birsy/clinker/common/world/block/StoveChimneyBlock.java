package birsy.clinker.common.world.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StoveChimneyBlock extends AbstractStoveBlock {
    private static VoxelShape SINGLE_CHIMNEY_SHAPE = Util.make(() -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.25, 0.9375, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.25, 0.3125, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.75, 0.9375, 1, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.9375, 1, 0.25), BooleanOp.OR);
        return shape;
    });
    private static VoxelShape DOUBLE_CHIMNEY_SHAPE = Util.make(() -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.6875, 0, 0.25, 0.9375, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.75, 0.9375, 1, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.0625, 0.9375, 1, 0.25), BooleanOp.OR);
        return shape;
    });



    public StoveChimneyBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected VoxelShape getSingleShape() {
        return SINGLE_CHIMNEY_SHAPE;
    }

    @Override
    protected VoxelShape getDoubleShape() {
        return DOUBLE_CHIMNEY_SHAPE;
    }

    @Override
    AbstractStoveBlock getType() {
        return (AbstractStoveBlock) ClinkerBlocks.STOVE_CHIMNEY.get();
    }

    @Override
    public boolean isValidAttachmentBlock(BlockState state, DoubleBlockHalf half) {
        return state.is(ClinkerBlocks.STOVE.get()) && (state.hasProperty(FACING) && state.getValue(FACING) == state.getValue(FACING));
    }

    @Override
    public BlockState getValidAttachmentBlock(BlockState state, DoubleBlockHalf half) {
        return ClinkerBlocks.STOVE.get().defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(TYPE, state.getValue(TYPE));
    }

    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return new ItemStack(ClinkerBlocks.STOVE.get());
    }

    @Override
    public DoubleBlockHalf getHalf(BlockState state) {
        return DoubleBlockHalf.UPPER;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
    }
}
