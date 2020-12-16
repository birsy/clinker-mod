package birsy.clinker.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class BrimstoneBlock extends Block
{
	public BrimstoneBlock()
	{
		super(((Block.Properties.create(Material.ROCK, MaterialColor.BROWN_TERRACOTTA)
			  .hardnessAndResistance(2.75F, 75.0F)
			  .sound(SoundType.STONE)
			  .harvestLevel(1)
			  .harvestTool(ToolType.PICKAXE))));
	}
}
