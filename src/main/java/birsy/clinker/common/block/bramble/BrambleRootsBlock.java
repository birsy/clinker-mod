package birsy.clinker.common.block.bramble;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BrambleRootsBlock extends AbstractBrambleBodyBlock implements net.minecraftforge.common.IForgeShearable {
			
	public BrambleRootsBlock() {
		super(((Block.Properties.create(Material.ORGANIC)
				  .sound(SoundType.CROP)
				  .tickRandomly()
				  .notSolid()
				  .setRequiresTool()
				  .hardnessAndResistance(4.0F)
				  .doesNotBlockMovement())), Direction.DOWN);
	}

	@Override
	protected AbstractTopPlantBlock getTopPlantBlock() {
		return (AbstractTopPlantBlock)ClinkerBlocks.BRAMBLE_ROOTS_BOTTOM.get();
	}
	
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
		entityIn.setMotionMultiplier(state, new Vector3d(0.35D, 0.35D, 0.35D));
		entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
    }
	
	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return true;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		if(state.hasProperty(BlockStateProperties.WATERLOGGED)) {
			return 0;
		}
		return 60;
	}
}
