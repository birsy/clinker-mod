package birsy.clinker.common.block.mitesoil;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class AbstractMitesoilBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public AbstractMitesoilBlock (Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick (BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
        if (state.getBlock() instanceof AbstractMitesoilBlock) {
            if (state.getValue(AbstractMitesoilBlock.ACTIVE)) {
                worldIn.setBlock(pos, state.setValue(AbstractMitesoilBlock.ACTIVE, false), 2);
            }
        }
        super.randomTick(state, worldIn, pos, random);
    }

    @Override
    public void stepOn (Level worldIn, BlockPos pos, Entity entityIn) {
        this.inform(16, pos, worldIn);
        super.stepOn(worldIn, pos, entityIn);
    }

    /**
     * Recursive function that will inform nearby blocks when a player steps on it.
     * @param distance The distance from the origin. This dissipates over time, reducing lag and allowing for more strategy.
     * @param pos The position that was informed.
     * @param world The world that the block is informed in.
     */
    public void inform (int distance, BlockPos pos, LevelAccessor world) {
        if (distance > 0) {
            this.activate(pos, world);

            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                BlockState state = world.getBlockState(pos.relative(direction));
                if (state.getBlock() instanceof AbstractMitesoilBlock) {
                    if (!state.getValue(ACTIVE)) {
                        ((AbstractMitesoilBlock) state.getBlock()).inform(distance - 1, pos.relative(direction), world);
                    }
                }
            }
        }
    }

    /**
     * Preforms a function when the block is informed.
     * @param pos The position of the block.
     * @param world The world that the block is in.
     */
    private void activate (BlockPos pos, LevelAccessor world) {
        world.setBlock(pos, world.getBlockState(pos).setValue(ACTIVE, true), 2);
    }
}
