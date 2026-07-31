package birsy.clinker.common.block.plant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class StromatoliteBlock extends Block {
    public static final MapCodec<StromatoliteBlock> CODEC = simpleCodec(StromatoliteBlock::new);

    public static final int MAX_SIZE_INCLUSIVE = 4;
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, MAX_SIZE_INCLUSIVE);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final VoxelShape[] shapeBySize;

    public StromatoliteBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SIZE, 0).setValue(AGE, 0).setValue(WATERLOGGED, false));

        this.shapeBySize = new VoxelShape[MAX_SIZE_INCLUSIVE + 1];
        for (int i = 0; i <= MAX_SIZE_INCLUSIVE; i++) {
            int size = MAX_SIZE_INCLUSIVE - i;
            VoxelShape inner = Shapes.box((size + 1) / 16.0, 0, (size + 1) / 16.0, (15 - size) / 16.0, 1, (15 - size) / 16.0);
            VoxelShape outer = Shapes.box(size / 16.0, 0.5, size / 16.0, (16 - size) / 16.0, 1, (16 - size) / 16.0);
            this.shapeBySize[i] = Shapes.or(inner, outer);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (true) {
            return;
        }

        if (state.getValue(AGE) >= 4) {
            super.randomTick(state, level, pos, random);
            return;
        }

        BlockState agedState = state;
        agedState = agedState.setValue(SIZE, Math.min(agedState.getValue(SIZE) + 1, MAX_SIZE_INCLUSIVE));
        agedState = agedState.setValue(AGE, Math.min(agedState.getValue(AGE) + random.nextInt(1, 2), 4));
        level.setBlock(pos, agedState, 2);
        super.randomTick(agedState, level, pos, random);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context)
                .setValue(SIZE, context.getLevel().getRandom().nextInt(4))
                .setValue(AGE, context.getLevel().getRandom().nextInt(4))
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(SIZE);
        VoxelShape shape = shapeBySize[index];
        Vec3 offset = state.getOffset(level, pos);
        return shape.move(offset.x, offset.y, offset.z);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(WATERLOGGED, SIZE, AGE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }



    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
