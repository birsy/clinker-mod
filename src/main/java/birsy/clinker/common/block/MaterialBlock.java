package birsy.clinker.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;

public class MaterialBlock extends Block
{
	public MaterialBlock()
	{
		super((Block.Properties.of(Material.METAL)
			  .strength(5.0f, 6.0f)
			  .sound(SoundType.METAL)
			  .harvestLevel(1)
			  .harvestTool(ToolType.PICKAXE)));
	}
}
