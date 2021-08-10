package birsy.clinker.common.block.bramble;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;

public class BrambleVinesTopBlock extends AbstractBrambleTopBlock implements net.minecraftforge.common.IForgeShearable {
	
	public BrambleVinesTopBlock() {
		super(((Block.Properties.of(Material.GRASS)
				  .sound(SoundType.CROP)
				  .randomTicks()
				  .noOcclusion()
				  .requiresCorrectToolForDrops()
				  .strength(4.0F)
				  .noCollission())), Direction.UP);
	}
	
	@Override
	protected Block getBodyBlock() {
		return ClinkerBlocks.BRAMBLE_VINES.get();
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
