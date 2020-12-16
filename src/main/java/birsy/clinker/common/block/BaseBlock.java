package birsy.clinker.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BaseBlock extends Block
{
	public BaseBlock(Material material, float hardness, float resistance, SoundType sound, int level, ToolType tool)
	{
		super((Block.Properties.create(material)
			  .hardnessAndResistance(hardness, resistance)
			  .sound(sound)
			  .harvestLevel(level)
			  .harvestTool(tool)));
	}
}
