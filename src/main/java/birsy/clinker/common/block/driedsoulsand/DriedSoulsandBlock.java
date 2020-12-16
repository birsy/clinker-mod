package birsy.clinker.common.block.driedsoulsand;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class DriedSoulsandBlock extends Block
{
	public DriedSoulsandBlock()
	{
		super(((Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_GRAY)
			.hardnessAndResistance(1.5F, 6.0F)
			.sound(SoundType.NETHER_BRICK)
			.harvestLevel(1)
			.harvestTool(ToolType.PICKAXE)
			)));
	}
		
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
	{
		Vector3d vec3d = entityIn.getMotion();
		super.onEntityWalk(worldIn, pos, entityIn);
		entityIn.setMotion(vec3d.x * 1.3, -vec3d.y, vec3d.z * 1.3);
	}
}
