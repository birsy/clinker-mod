package birsy.clinker.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class LocustFlowersBlock extends LeavesBlock {
    public LocustFlowersBlock(Properties properties) {
        super(properties);
    }

    public void animateTick(BlockState blockState, Level worldIn, BlockPos pos, Random random) {
        if (random.nextInt(3) == 0) {
            double particleX = (double) pos.getX() + random.nextDouble();
            double particleY = (double) pos.getY() + 0.7D;
            double particleZ = (double) pos.getZ() + random.nextDouble();
            worldIn.addParticle(ParticleTypes.ASH, particleX, particleY, particleZ, 0.2D, 0.0D, 0.1D);
        }
    }
}
