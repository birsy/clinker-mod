package birsy.clinker.common.block.bramble;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

public class BrambleVinesTopBlock extends AbstractBrambleTopBlock implements net.minecraftforge.common.IForgeShearable {
	
	public BrambleVinesTopBlock() {
		super(((Block.Properties.create(Material.ORGANIC)
				  .sound(SoundType.CROP)
				  .tickRandomly()
				  .notSolid()
				  .setRequiresTool()
				  .hardnessAndResistance(4.0F)
				  .doesNotBlockMovement())), Direction.UP);
	}
	
	@Override
	protected Block getBodyPlantBlock() {
		return ClinkerBlocks.BRAMBLE_VINES.get();
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
