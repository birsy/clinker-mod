package birsy.clinker.common.block.bramble;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

public class BrambleRootsBottomBlock extends AbstractBrambleTopBlock implements net.minecraftforge.common.IForgeShearable {
	public BrambleRootsBottomBlock() {
		super(((Block.Properties.create(Material.ORGANIC)
				  .sound(SoundType.CROP)
				  .tickRandomly()
				  .notSolid()
				  .setRequiresTool()
				  .hardnessAndResistance(4.0F)
				  .doesNotBlockMovement())), Direction.DOWN);
	}
	
	@Override
	protected Block getBodyPlantBlock() {
		return ClinkerBlocks.BRAMBLE_ROOTS.get();
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
