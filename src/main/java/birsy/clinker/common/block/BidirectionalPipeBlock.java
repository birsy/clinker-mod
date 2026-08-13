package birsy.clinker.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BidirectionalPipeBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty INPUT_FACE = DirectionProperty.create("input");
    public static final DirectionProperty OUTPUT_FACE = DirectionProperty.create("output");
    protected final VoxelShape[][] shapeCache;

    public BidirectionalPipeBlock(Properties properties, float radius, float rounding) {
        super(properties);
        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(INPUT_FACE, Direction.UP)
                        .setValue(OUTPUT_FACE, Direction.DOWN)
                        .setValue(WATERLOGGED, false)
        );
        this.shapeCache = createShapes(radius, rounding);
    }

    protected VoxelShape[][] createShapes(float radius, float rounding) {
        float min = 8 - (radius * 16), max = 8 + (radius * 16);
        float sMin = 0, sMax = 16;
        float eMin = 8 - (radius - rounding) * 16, eMax = 8 + (radius - rounding) * 16;

        Direction[] directions = Direction.values();
        Map<Direction, VoxelShape> templates = Map.of(
                Direction.NORTH, Block.box( min,  min, sMin,  max,  max, eMax),
                Direction.SOUTH, Block.box( min,  min, eMin,  max,  max, sMax),
                Direction.WEST,  Block.box(sMin,  min,  min, eMax,  max,  max),
                Direction.EAST,  Block.box(eMin,  min,  min, sMax,  max,  max),
                Direction.DOWN,  Block.box( min, sMin,  min,  max, eMax,  max),
                Direction.UP,    Block.box( min, eMin,  min,  max, sMax,  max)
        );
        VoxelShape[][] array = new VoxelShape[directions.length][directions.length];
        for (Direction inputDir : directions) {
            for (Direction outputDir : directions) {
                Direction effectiveOutputDir = outputDir;
                if (inputDir == outputDir) effectiveOutputDir = outputDir.getOpposite();
                array[inputDir.get3DDataValue()][outputDir.get3DDataValue()] = Shapes.or(
                        templates.get(inputDir),
                        templates.get(effectiveOutputDir)
                ).optimize();
            }
        }
        return array;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeCache[state.getValue(INPUT_FACE).get3DDataValue()][state.getValue(OUTPUT_FACE).get3DDataValue()];
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;

        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        state = state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

        Direction inputFace = context.getClickedFace().getOpposite();
        Direction outputFace = context.getNearestLookingDirection().getOpposite();
        if (inputFace == outputFace) outputFace = outputFace.getOpposite();

        return state
                .setValue(INPUT_FACE, inputFace)
                .setValue(OUTPUT_FACE, outputFace);
    }

    @Override
    protected BlockState updateShape(BlockState initialState, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState state = super.updateShape(initialState, direction, neighborState, level, pos, neighborPos);
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return state;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, INPUT_FACE, OUTPUT_FACE);
    }
}
