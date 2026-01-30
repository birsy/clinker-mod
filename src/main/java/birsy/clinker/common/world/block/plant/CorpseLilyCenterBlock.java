package birsy.clinker.common.world.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CorpseLilyCenterBlock extends OthershorePlantBlock {
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public CorpseLilyCenterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(DOWN, false));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null && context.getLevel().getBlockState(context.getClickedPos().below()).is(ClinkerBlocks.THORNY_STEM.get()))
            state = state.setValue(DOWN, true);
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockState updatedState = super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        if (facing == Direction.DOWN && facingState.is(ClinkerBlocks.THORNY_STEM.get())) {
            updatedState = updatedState.setValue(DOWN, true);
        } else if (facing == Direction.DOWN) {
            updatedState = updatedState.setValue(DOWN, false);
        }
        return updatedState;
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return super.mayPlaceOn(state, level, pos) || state.is(ClinkerTags.OTHERSHORE_SOIL) || state.is(ClinkerBlocks.THORNY_STEM.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(DOWN);
    }
}
