package birsy.clinker.common.block.mitesoil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public abstract class AbstractMitesoilBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public AbstractMitesoilBlock (Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick (BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (state.getBlock() instanceof AbstractMitesoilBlock) {
            if (state.get(AbstractMitesoilBlock.ACTIVE)) {
                worldIn.setBlockState(pos, state.with(AbstractMitesoilBlock.ACTIVE, false), 2);
            }
        }
        super.randomTick(state, worldIn, pos, random);
    }

    @Override
    public void onEntityWalk (World worldIn, BlockPos pos, Entity entityIn) {
        this.inform(16, pos, worldIn);
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    /**
     * Recursive function that will inform nearby blocks when a player steps on it.
     * @param distance The distance from the origin. This dissipates over time, reducing lag and allowing for more strategy.
     * @param pos The position that was informed.
     * @param world The world that the block is informed in.
     */
    public void inform (int distance, BlockPos pos, IWorld world) {
        if (distance > 0) {
            this.activate(pos, world);

            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                BlockState state = world.getBlockState(pos.offset(direction));
                if (state.getBlock() instanceof AbstractMitesoilBlock) {
                    if (!state.get(ACTIVE)) {
                        ((AbstractMitesoilBlock) state.getBlock()).inform(distance - 1, pos.offset(direction), world);
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
    private void activate (BlockPos pos, IWorld world) {
        world.setBlockState(pos, world.getBlockState(pos).with(ACTIVE, true), 2);
    }
}
