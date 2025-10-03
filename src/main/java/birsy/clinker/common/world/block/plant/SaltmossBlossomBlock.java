package birsy.clinker.common.world.block.plant;

import birsy.clinker.client.particle.BlossomBugParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SaltmossBlossomBlock extends OthershorePlantBlock {
    public SaltmossBlossomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (random.nextInt(4) == 0) {
            double x = pos.getX() + 0.5 + random.nextGaussian() * 2.5, //Mth.lerp(random.nextDouble(), -4.0, 4.0),
                    z = pos.getZ() + 0.5 + random.nextGaussian() * 2.5, //Mth.lerp(random.nextDouble(), -4.0, 4.0),
                    y = pos.getY() + 0.5 + Mth.lerp(random.nextDouble(), -0.25, 1.5);

            level.addParticle(
                    new BlossomBugParticle.BlossomBugParticleOptions(pos),
                    x, y, z, 0, 0, 0
            );
        }
    }
}
