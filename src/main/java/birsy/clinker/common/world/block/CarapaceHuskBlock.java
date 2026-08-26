package birsy.clinker.common.world.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;

public class CarapaceHuskBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<CarapaceHuskBlock> CODEC = simpleCodec(CarapaceHuskBlock::new);
    public static final BooleanProperty DECAYING = BooleanProperty.create("decaying");

    @Override
    public MapCodec<CarapaceHuskBlock> codec() {
        return CODEC;
    }

    public CarapaceHuskBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(DECAYING, false)
        );
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
    if (!state.getValue(DECAYING) && !level.isClientSide) {
        int i = level.random.nextIntBetweenInclusive(10, 15);
        level.playSound(null, entity, SoundEvents.FUNGUS_STEP, entity.getSoundSource(), 0.125F, 0.125F);
        level.setBlock(pos, state.setValue(DECAYING, true), 2);
        level.scheduleTick(pos, this, i);
    }
    super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(DECAYING)) {
            level.destroyBlock(pos, true);
        }
    }

}
