package birsy.clinker.common.block.silt;

import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SiltscarVineBlock extends GrowingPlantBodyBlock {
    public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public SiltscarVineBlock(Properties properties) {
        super(properties, Direction.UP, SHAPE, true);
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return null;
    }
}
