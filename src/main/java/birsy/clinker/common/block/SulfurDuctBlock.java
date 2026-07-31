package birsy.clinker.common.block;

import birsy.clinker.common.world.level.heat.HeatPropertiesProvider;
import birsy.clinker.common.world.level.heat.HeatReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class SulfurDuctBlock extends Block implements HeatPropertiesProvider, HeatReader {
    public static final DirectionProperty INPUT_FACE = DirectionProperty.create("input");
    public static final DirectionProperty OUTPUT_FACE = DirectionProperty.create("output");

    public SulfurDuctBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(INPUT_FACE, Direction.DOWN)
                        .setValue(OUTPUT_FACE, Direction.UP)
        );
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState currentState = super.getStateForPlacement(context);
        if (currentState == null) return null;
        return currentState
                .setValue(INPUT_FACE, context.getClickedFace().getOpposite())
                .setValue(OUTPUT_FACE, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(INPUT_FACE, OUTPUT_FACE);
    }

    @Override
    public float getHeatConductivityWeightForDirection(Level level, BlockPos pos, BlockState state, Direction direction) {
        Direction outputDirection = state.getValue(OUTPUT_FACE);
        final float backflowProbability = 0.0F;
        final float outflowProbability = 0.00F;
        float forwardFlowProbability = 1.0F - backflowProbability - outflowProbability * 4;

        if (direction == outputDirection) return forwardFlowProbability;
        Direction inputDirection = state.getValue(INPUT_FACE);
        if (direction == inputDirection) return backflowProbability;
        return outflowProbability;
    }
    @Override
    public float getAbsorptionProbability(Level level, BlockPos pos, BlockState state) {
        return 0.0F;
    }

    @Override
    public void onPacketPassed(Level level, BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel sLevel) {
            sLevel.sendParticles(
                    ParticleTypes.END_ROD,
                    pos.getX(), pos.getY(), pos.getZ(), 10,
                    0, 0, 0, 0
            );
        }
    }
}
