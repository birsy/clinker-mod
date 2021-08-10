package birsy.clinker.common.block.silt;

import net.minecraft.block.AbstractBodyPlantBlock;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class SiltscarVineBlock extends AbstractBodyPlantBlock {
    public static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public SiltscarVineBlock(Properties properties) {
        super(properties, Direction.UP, SHAPE, true);
    }

    @Override
    protected AbstractTopPlantBlock getTopPlantBlock() {
        return null;
    }
}
