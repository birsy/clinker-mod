package birsy.clinker.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
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
}
