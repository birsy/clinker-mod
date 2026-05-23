package birsy.clinker.common.world.level.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface HeatPropertiesProvider {
    // return true if the properties cannot be cached by blockstate and direction alone
    default boolean dynamicHeatProperties() { return false; }

    float getHeatConductivityWeightForDirection(Level level, BlockPos pos, BlockState state, Direction direction);
    float getAbsorptionProbability(Level level, BlockPos pos, BlockState state);
}
