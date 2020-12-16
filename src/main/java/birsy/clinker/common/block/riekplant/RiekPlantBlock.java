package birsy.clinker.common.block.riekplant;

import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class RiekPlantBlock extends RotatedPillarBlock {
	public RiekPlantBlock() {
		super((Block.Properties.create(Material.ORGANIC)
				  .hardnessAndResistance(2.0F)
				  .sound(SoundType.SLIME)
				  .harvestLevel(0)
				  .harvestTool(ToolType.AXE)));
	}
}
