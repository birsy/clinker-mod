package birsy.clinker.common.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class AshBlock extends FallingBlock
{
	private final int dustColor;
	
	public AshBlock(Properties properties) {
		super(properties);
		this.dustColor = 8616308;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		super.randomTick(state, worldIn, pos, random);
		if (worldIn.getBlockState(pos.up()).getBlock() == ClinkerBlocks.ROOTSTALK.get()) {
			worldIn.setBlockState(pos, ClinkerBlocks.ROOTED_ASH.get().getDefaultState());
		}
	}

	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		if (!entityIn.isAirBorne) {
			spawnParticles(worldIn, pos, entityIn);
		}
		super.onEntityWalk(worldIn, pos, entityIn);
	}
	
	private static void spawnParticles(World world, BlockPos worldIn, Entity entityIn) {
		Vector3d entityMotion = entityIn.getMotion();
		
		if(entityIn.velocityChanged) {
			for(Direction direction : Direction.values()) {
				BlockPos blockpos = worldIn.offset(direction);
				if (!world.getBlockState(blockpos).isOpaqueCube(world, blockpos)) {
					world.addParticle(ParticleTypes.ASH, entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), -entityMotion.getX(), -entityMotion.getZ(), -entityMotion.getY());
				}
			}
		}
	}

	@Override
	public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
		super.onEndFalling(worldIn, pos, fallingState, hitState, fallingBlock);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			this.addAshLayer(worldIn, pos.offset(direction), worldIn.getRandom().nextInt(3 - 1) + 1);
			final float velocityMultiplier = 20;
			for (int i = 0; i < 5; i++) {
				worldIn.addParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, fallingState),
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), pos.getX(), pos.getX() + 1),
						pos.getY(),
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), pos.getZ(), pos.getZ() + 1),
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), 3, 6) * velocityMultiplier,
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), 3, 6) * velocityMultiplier,
						MathUtils.getRandomFloatBetween(worldIn.getRandom(), 3, 6) * velocityMultiplier);
			}
		}
	}

	public void addAshLayer(World worldIn, BlockPos pos, int amount) { }

	@OnlyIn(Dist.CLIENT)
	public int getDustColor(BlockState state, IBlockReader p_189876_2_, BlockPos p_189876_3_) {
	   return this.dustColor;
	}
}
