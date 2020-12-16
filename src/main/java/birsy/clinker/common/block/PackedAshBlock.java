package birsy.clinker.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class PackedAshBlock extends Block
{
	public PackedAshBlock()
	{
		super(((Block.Properties.create(Material.CLAY)
			  .hardnessAndResistance(0.5F)
			  .sound(SoundType.GROUND)
			  .harvestTool(ToolType.SHOVEL))));
	}
}
