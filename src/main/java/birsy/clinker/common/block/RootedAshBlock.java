package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;


public class RootedAshBlock extends SpreadableAshBlock
{
	public RootedAshBlock(Block blockIn)
	{
		super(Block.Properties.of(Material.GRASS, MaterialColor.TERRACOTTA_ORANGE)
				.randomTicks()
				.strength(0.6F)
				.sound(SoundType.ROOTED_DIRT),
				blockIn);
	}
}
