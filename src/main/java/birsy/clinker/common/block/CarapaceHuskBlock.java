package birsy.clinker.common.block;

import birsy.clinker.common.world.level.BlockBreakageSystem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CarapaceHuskBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<CarapaceHuskBlock> CODEC = simpleCodec(CarapaceHuskBlock::new);
    public static final BooleanProperty DECAYING = BooleanProperty.create("decaying");

    public CarapaceHuskBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(DECAYING, false)
        );
    }

    @Override
    public MapCodec<CarapaceHuskBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, DECAYING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!state.getValue(DECAYING) && level instanceof ServerLevel serverLevel) {
            int i = serverLevel.random.nextIntBetweenInclusive(10, 15);
            serverLevel.playSound(null, entity, SoundEvents.MUDDY_MANGROVE_ROOTS_BREAK, entity.getSoundSource(), 0.125F, 0.125F);
            serverLevel.setBlock(pos, state.setValue(DECAYING, true), 2);
            BlockBreakageSystem.get(serverLevel)
                    .updateBreakage(pos, 8);
            serverLevel.scheduleTick(pos, this, i);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(DECAYING)) level.destroyBlock(pos, true);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        // clear out the breakage progress whenever this block is broken
        if (level instanceof ServerLevel serverLevel) BlockBreakageSystem.get(serverLevel).clearBreakage(pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
