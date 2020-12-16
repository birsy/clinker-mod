package birsy.clinker.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class MetalMaterialBlock extends Block
{
	public MetalMaterialBlock()
	{
		super((Block.Properties.create(Material.IRON)
			  .hardnessAndResistance(5.0f, 6.0f)
			  .sound(SoundType.NETHERITE)
			  .harvestLevel(1)
			  .harvestTool(ToolType.PICKAXE)));
	}
}
