package birsy.clinker.common.block.riekplant;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

public class RiekPlantBlock extends RotatedPillarBlock {
	public RiekPlantBlock() {
		super((Block.Properties.of(Material.GRASS)
				  .strength(2.0F)
				  .sound(SoundType.SLIME_BLOCK)
				  .harvestLevel(0)
				  .harvestTool(ToolType.AXE)));
	}
}
