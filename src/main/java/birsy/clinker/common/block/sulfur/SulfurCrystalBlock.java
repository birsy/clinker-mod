package birsy.clinker.common.block.sulfur;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SulfurCrystalBlock extends DirectionalBlock {
    public static final IntegerProperty PROPERTY_AGE = IntegerProperty.create("age", 0, 15);

    protected SulfurCrystalBlock(Properties builder) {
        super(builder);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PROPERTY_AGE);
    }
}
