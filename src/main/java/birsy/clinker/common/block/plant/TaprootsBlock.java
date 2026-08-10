package birsy.clinker.common.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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

public class TaprootsBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty INPUT_FACE = DirectionProperty.create("input");
    public static final DirectionProperty OUTPUT_FACE = DirectionProperty.create("output");

    private static final VoxelShape[][] SHAPE_CACHE = Util.make(TaprootsBlock::createShapes);

    public TaprootsBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(INPUT_FACE, Direction.UP)
                        .setValue(OUTPUT_FACE, Direction.DOWN)
                        .setValue(WATERLOGGED, false)
        );
    }

    private static VoxelShape[][] createShapes() {
        Direction[] directions = Direction.values();
        Map<Direction, VoxelShape> templates = Map.of(
                Direction.NORTH, Block.box(2, 2, 0, 14, 14, 10),
                Direction.SOUTH, Block.box(2, 2, 6, 14, 14, 16),
                Direction.WEST, Block.box(0, 2, 2, 10, 14, 14),
                Direction.EAST, Block.box(6, 2, 2, 16, 14, 14),
                Direction.DOWN, Block.box(2, 0, 2, 14, 10, 14),
                Direction.UP, Block.box(2, 6, 2, 14, 16, 14)
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
        return SHAPE_CACHE[state.getValue(INPUT_FACE).get3DDataValue()][state.getValue(OUTPUT_FACE).get3DDataValue()];
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
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        BlockPos.MutableBlockPos mPos = pos.mutable();
        Direction inDir = state.getValue(INPUT_FACE),
                  outDir = state.getValue(OUTPUT_FACE);
        BlockState inState = level.getBlockState(mPos.set(pos).move(inDir)),
                   outState = level.getBlockState(mPos.set(pos).move(outDir));
        boolean connectedIn = (inState.is(this) && (inState.getValue(INPUT_FACE) == inDir.getOpposite() || inState.getValue(OUTPUT_FACE) == inDir.getOpposite())) | inState.is(ClinkerBlocks.TAPROOT_BURL),
                connectedOut = (outState.is(this) && (outState.getValue(INPUT_FACE) == outDir.getOpposite() || outState.getValue(OUTPUT_FACE) == outDir.getOpposite())) | outState.is(ClinkerBlocks.TAPROOT_BURL);
        if (connectedIn == connectedOut) return;

        BlockState dripState = connectedIn ? outState : inState;
        if (dripState.isSolid()) return;

        Direction dripDir = connectedIn ? outDir : inDir;
        if (dripDir == Direction.UP) return;

        Direction localXDir = dripDir.getAxis().isHorizontal() ? dripDir.getClockWise() : Direction.EAST;
        Direction localYDir = dripDir.getAxis().isHorizontal() ? Direction.UP : Direction.NORTH;

        int particleCount = dripDir.getAxis().isHorizontal() ?
                (random.nextInt(8) == 0 ? 1 : 0) :
                (random.nextInt(5) == 0 ? 1 : 0);
        for (int i = 0; i < particleCount; i++) {
            int tubeX = random.nextBoolean() ? -1 : 1, tubeY = random.nextBoolean() ? -1 : 1;
            double localX = random.triangle(tubeX * 0.25 , 1.0/16.0),
                   localY = random.triangle(tubeY * 0.25, 1.0/16.0);

            double x = pos.getX() + localX * localXDir.getStepX() + localY * localYDir.getStepX() + ((dripDir.getStepX() * 1.05) * 0.5 + 0.5),
                   y = pos.getY() + localX * localXDir.getStepY() + localY * localYDir.getStepY() + ((dripDir.getStepY() * 1.05) * 0.5 + 0.5) - 0.125,
                   z = pos.getZ() + localX * localXDir.getStepZ() + localY * localYDir.getStepZ() + ((dripDir.getStepZ() * 1.05) * 0.5 + 0.5);
            level.addParticle(
                    ParticleTypes.DRIPPING_HONEY,
                    x, y, z, 0, 0, 0
            );
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, INPUT_FACE, OUTPUT_FACE);
    }
}
