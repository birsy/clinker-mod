package birsy.clinker.common.block.driedsoulsand;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

public class DriedSoulsandBlock extends Block
{
	public DriedSoulsandBlock()
	{
		super(((Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
			.strength(1.5F, 6.0F)
			.sound(SoundType.NETHER_BRICKS)
			.harvestLevel(1)
			.harvestTool(ToolType.PICKAXE)
			)));
	}
		
	public void stepOn(Level worldIn, BlockPos pos, Entity entityIn)
	{
		Vec3 vec3d = entityIn.getDeltaMovement();
		super.stepOn(worldIn, pos, entityIn);
		entityIn.setDeltaMovement(vec3d.x * 1.3, -vec3d.y, vec3d.z * 1.3);
	}
}
