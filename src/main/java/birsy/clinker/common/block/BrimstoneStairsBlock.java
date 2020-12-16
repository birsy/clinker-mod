package birsy.clinker.common.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BrimstoneStairsBlock extends StairsBlock
{
	public BrimstoneStairsBlock()
	{
		super(() -> new BlockState(ClinkerBlocks.BRIMSTONE.get(), null, null), ((Block.Properties.create(Material.ROCK)
				  .hardnessAndResistance(2.75F, 75.0F)
				  .sound(SoundType.STONE)
				  .harvestLevel(1)
				  .harvestTool(ToolType.PICKAXE))));
	}
}
