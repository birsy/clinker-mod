package birsy.clinker.common.block.bramble;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.Direction;

public class BrambleVinesBlock extends AbstractBrambleBodyBlock implements net.minecraftforge.common.IForgeShearable {
			
	public BrambleVinesBlock() {
		super(((Block.Properties.of(Material.GRASS)
				  .sound(SoundType.CROP)
				  .randomTicks()
				  .noOcclusion()
				  .requiresCorrectToolForDrops()
				  .strength(4.0F)
				  .noCollission())), Direction.UP);
	}

	@Override
	protected GrowingPlantHeadBlock getHeadBlock() {
		return (GrowingPlantHeadBlock)ClinkerBlocks.BRAMBLE_VINES_TOP.get();
	}
}
