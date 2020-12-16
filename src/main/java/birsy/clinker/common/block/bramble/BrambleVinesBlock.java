package birsy.clinker.common.block.bramble;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;

public class BrambleVinesBlock extends AbstractBrambleBodyBlock implements net.minecraftforge.common.IForgeShearable {
			
	public BrambleVinesBlock() {
		super(((Block.Properties.create(Material.ORGANIC)
				  .sound(SoundType.CROP)
				  .tickRandomly()
				  .notSolid()
				  .setRequiresTool()
				  .hardnessAndResistance(4.0F)
				  .doesNotBlockMovement())), Direction.UP);
	}

	@Override
	protected AbstractTopPlantBlock getTopPlantBlock() {
		return (AbstractTopPlantBlock)ClinkerBlocks.BRAMBLE_VINES_TOP.get();
	}
}
