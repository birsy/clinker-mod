package birsy.clinker.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

public class BaseBlock extends Block
{
	public BaseBlock(Material material, float hardness, float resistance, SoundType sound, int level, ToolType tool)
	{
		super((Block.Properties.of(material)
			  .strength(hardness, resistance)
			  .sound(sound)
			  .harvestLevel(level)
			  .harvestTool(tool)));
	}
}
