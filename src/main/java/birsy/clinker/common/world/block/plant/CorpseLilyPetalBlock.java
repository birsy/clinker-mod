package birsy.clinker.common.world.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.ShapeUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CorpseLilyPetalBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BLOOM = BlockStateProperties.BLOOM;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected static final VoxelShape BLOOMED_SHAPE = Block.box(2, 2, -2, 14, 6, 7),
                                      UNBLOOMED_SHAPE = Block.box(2, 2, -2, 14, 12, 2);
    protected static final VoxelShape[] SHAPES = Util.make(() -> {
        VoxelShape[] shapes = new VoxelShape[4 * 2];
        Direction direction = Direction.NORTH;
        for (int i = 0; i < 4; i++) {
            int index = direction.get2DDataValue() * 2;
            shapes[index + 0] = ShapeUtil.rotate(BLOOMED_SHAPE, i, Direction.Axis.Y);
            shapes[index + 1] = ShapeUtil.rotate(UNBLOOMED_SHAPE, i, Direction.Axis.Y);
            direction = direction.getCounterClockWise();
        }
        return shapes;
    });

    public CorpseLilyPetalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(BLOOM, false)
        );
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        level.scheduleTick(pos, this, level.random.nextIntBetweenInclusive(10, 20));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).get2DDataValue() * 2 + (state.getValue(BLOOM) ? 0 : 1)];
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (!state.getValue(BLOOM)) {
            float pitch = Mth.randomBetween(level.random, 0.8F, 1.2F);
            level.playSound(null, pos, SoundEvents.NETHER_WART_PLANTED, SoundSource.BLOCKS, 1.0F, pitch);
            level.setBlock(pos, state.setValue(BLOOM, true), 2);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        state = state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        return state.setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return facing == state.getValue(FACING) && !this.canSurvive(state, level, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(FACING, WATERLOGGED, BLOOM);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.relative(state.getValue(FACING))).is(ClinkerBlocks.CORPSE_LILY_BULB);
    }
}
