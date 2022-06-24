package birsy.clinker.common.block;

import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.registry.world.ClinkerWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AshBlock extends FallingBlock
{
	private final int dustColor;
	
	public AshBlock(Properties properties) {
		super(properties);
		this.dustColor = 8616308;
	}

	@OnlyIn(Dist.CLIENT)
	public int getDustColor(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return this.dustColor;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		super.animateTick(state, level, pos, random);
		if (random.nextInt(1) == 0 && level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos.above()) > 10 && level.dimension() == ClinkerWorld.OTHERSHORE) {
			float range = 1.2F;
			level.addParticle(ClinkerParticles.ASH_CLOUD.get(), pos.getX() + (random.nextGaussian() * range), pos.getY() + (random.nextGaussian() * range), pos.getZ() + (random.nextGaussian() * range), 0.0, 0.0, 0.0);
		}
	}
}
