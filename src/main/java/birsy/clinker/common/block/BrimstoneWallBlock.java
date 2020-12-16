package birsy.clinker.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BrimstoneWallBlock extends WallBlock
{
	public BrimstoneWallBlock()
	{
		super(((Block.Properties.create(Material.ROCK)
				  .hardnessAndResistance(2.75F, 75.0F)
				  .sound(SoundType.STONE)
				  .harvestLevel(1)
				  .harvestTool(ToolType.PICKAXE))));
	}
}
