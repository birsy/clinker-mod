package birsy.clinker.common.block.driedsoulsand;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

public class DriedSoulsandStairsBlock extends StairBlock
{
	public DriedSoulsandStairsBlock()
	{
		super(() -> ClinkerBlocks.DRIED_SOULSAND.get().defaultBlockState(), ((Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY)
			.strength(2.75F, 75.0F)
			.sound(SoundType.NETHER_BRICKS)
			.harvestLevel(1)
			.harvestTool(ToolType.PICKAXE))));
	}
		
	public void stepOn(Level worldIn, BlockPos pos, Entity entityIn)
	{
		Vec3 vec3d = entityIn.getDeltaMovement();
		super.stepOn(worldIn, pos, entityIn);
		entityIn.setDeltaMovement(vec3d.x * 1.3, -vec3d.y, vec3d.z * 1.3);
	}
}
