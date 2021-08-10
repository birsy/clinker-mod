package birsy.clinker.common.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class AshBlock extends FallingBlock
{
	private final int dustColor;
	
	public AshBlock(Properties properties) {
		super(properties);
		this.dustColor = 8616308;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		super.randomTick(state, worldIn, pos, random);
		if (worldIn.getBlockState(pos.above()).getBlock() == ClinkerBlocks.ROOTSTALK.get()) {
			worldIn.setBlockAndUpdate(pos, ClinkerBlocks.ROOTED_ASH.get().defaultBlockState());
		}
	}

	public void stepOn(Level worldIn, BlockPos pos, Entity entityIn) {
		if (!entityIn.hasImpulse) {
			spawnParticles(worldIn, pos, entityIn);
		}
		super.stepOn(worldIn, pos, entityIn);
	}
	
	private static void spawnParticles(Level world, BlockPos worldIn, Entity entityIn) {
		Vec3 entityMotion = entityIn.getDeltaMovement();
		
		if(entityIn.hurtMarked) {
			for(Direction direction : Direction.values()) {
				BlockPos blockpos = worldIn.relative(direction);
				if (!world.getBlockState(blockpos).isSolidRender(world, blockpos)) {
					world.addParticle(ParticleTypes.ASH, entityIn.getX(), entityIn.getY(), entityIn.getZ(), -entityMotion.x(), -entityMotion.z(), -entityMotion.y());
				}
			}
		}
	}

	@Override
	public void onLand(Level worldIn, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
		super.onLand(worldIn, pos, fallingState, hitState, fallingBlock);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			this.addAshLayer(worldIn, pos.relative(direction), worldIn.getRandom().nextInt(3 - 1) + 1);
			final float velocityMultiplier = 20;
			for (int i = 0; i < 5; i++) {
				worldIn.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, fallingState),
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), pos.getX(), pos.getX() + 1),
						pos.getY(),
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), pos.getZ(), pos.getZ() + 1),
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), 3, 6) * velocityMultiplier,
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), 3, 6) * velocityMultiplier,
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), 3, 6) * velocityMultiplier);
			}
		}
	}

	public void addAshLayer(Level worldIn, BlockPos pos, int amount) { }

	@OnlyIn(Dist.CLIENT)
	public int getDustColor(BlockState state, BlockGetter p_189876_2_, BlockPos p_189876_3_) {
	   return this.dustColor;
	}
}
