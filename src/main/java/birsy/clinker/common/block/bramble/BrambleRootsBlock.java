package birsy.clinker.common.block.bramble;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class BrambleRootsBlock extends AbstractBrambleBodyBlock implements net.minecraftforge.common.IForgeShearable {
			
	public BrambleRootsBlock() {
		super(((Block.Properties.of(Material.GRASS)
				  .sound(SoundType.CROP)
				  .randomTicks()
				  .noOcclusion()
				  .requiresCorrectToolForDrops()
				  .strength(4.0F)
				  .noCollission())), Direction.DOWN);
	}

	@Override
	protected GrowingPlantHeadBlock getHeadBlock() {
		return (GrowingPlantHeadBlock)ClinkerBlocks.BRAMBLE_ROOTS_BOTTOM.get();
	}
	
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
		entityIn.makeStuckInBlock(state, new Vec3(0.35D, 0.35D, 0.35D));
		entityIn.hurt(DamageSource.CACTUS, 1.0F);
    }
	
	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return true;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if(state.hasProperty(BlockStateProperties.WATERLOGGED)) {
			return 0;
		}
		return 60;
	}
}
