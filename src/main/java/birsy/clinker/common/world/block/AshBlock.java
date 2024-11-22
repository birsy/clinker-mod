package birsy.clinker.common.world.block;

import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.registry.world.ClinkerWorld;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;


public class AshBlock extends FallingBlock {
	private final int dustColor;
	
	public AshBlock(Properties properties) {
		super(properties);
		this.dustColor = 8616308;
	}

	@Override
	protected MapCodec<? extends FallingBlock> codec() {
		return null;
	}

	
	public int getDustColor(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return this.dustColor;
	}
}
