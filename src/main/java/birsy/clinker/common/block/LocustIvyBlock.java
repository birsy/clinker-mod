package birsy.clinker.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import javax.annotation.Nullable;
import java.util.Random;

public class LocustIvyBlock extends DirectionalBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    public static final EnumProperty<IvyAttachment> IVY_ATTACHMENT = EnumProperty.create("ivy_attachment", IvyAttachment.class);

    public LocustIvyBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(IVY_ATTACHMENT, IvyAttachment.SIDE));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facingDirection = Direction.Plane.HORIZONTAL.test(context.getClickedFace()) ? context.getClickedFace() : context.getHorizontalDirection();
        //TODO: Replace "instanceof LeavesBlock" with tag.
        boolean isSolidBlockAbove = Block.isFaceFull(context.getLevel().getBlockState(context.getClickedPos().above()).getCollisionShape(context.getLevel(), context.getClickedPos()), Direction.DOWN);

        BlockState belowState = context.getLevel().getBlockState(context.getClickedPos().below());
        boolean isIvyEnd = true;
        if (belowState.getBlock() instanceof LocustIvyBlock) {
            isIvyEnd = belowState.getValue(FACING) != facingDirection;
        }

        return this.defaultBlockState().setValue(FACING, facingDirection).setValue(IVY_ATTACHMENT, isSolidBlockAbove ? IvyAttachment.TOP : isIvyEnd ? IvyAttachment.BOTTOM : IvyAttachment.SIDE);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            boolean isSolidBlockAbove = Block.isFaceFull(facingState.getCollisionShape(level, facingPos), Direction.DOWN);
            if (isSolidBlockAbove) {
                return state.setValue(IVY_ATTACHMENT, IvyAttachment.TOP);
            } else {
                boolean isIvyEnd = false;
                if (facingState.getBlock() instanceof LocustIvyBlock) {
                    isIvyEnd = facingState.getValue(FACING) != state.getValue(FACING);
                }
                return state.setValue(IVY_ATTACHMENT, isIvyEnd ? IvyAttachment.BOTTOM : IvyAttachment.SIDE);
            }
        } else {
            return state;
        }
    }

        @Override
    public boolean isValidBonemealTarget(BlockGetter pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, Random pRandom, BlockPos pPos, BlockState pState) {

    }

    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, AGE, IVY_ATTACHMENT);
    }

    public enum IvyAttachment implements StringRepresentable {
        TOP("top"), SIDE("side"), BOTTOM("bottom");

        private final String name;

        IvyAttachment(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
