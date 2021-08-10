package birsy.clinker.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

public class MetalMaterialBlock extends Block
{
	public MetalMaterialBlock()
	{
		super((Block.Properties.of(Material.METAL)
			  .strength(5.0f, 6.0f)
			  .sound(SoundType.NETHERITE_BLOCK)
			  .harvestLevel(1)
			  .harvestTool(ToolType.PICKAXE)));
	}
}
