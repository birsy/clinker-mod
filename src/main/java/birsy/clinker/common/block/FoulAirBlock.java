package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FoulAirBlock extends Block
{
	public FoulAirBlock()
	{
		super(((Block.Properties.create(Material.MISCELLANEOUS).tickRandomly())));
	}
	
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(7) == 0) {
			if (!state.isValidPosition(worldIn, pos)) {
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);			
			}
		}
		
		for(Direction direction : Direction.values()) {
			BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
			if (blockstate == Blocks.TORCH.getDefaultState()) {
				worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState(), 2);
			}
		}
	}

	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote && worldIn.getDifficulty() != Difficulty.PEACEFUL) {
			if (entityIn instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity)entityIn;
				if (!livingentity.isInvulnerableTo(DamageSource.DRYOUT)) {
					livingentity.attackEntityFrom(DamageSource.DRYOUT, 0.5F);
				}
			}
		}
		if (!worldIn.isRemote && worldIn.getDifficulty() == Difficulty.HARD && entityIn instanceof LivingEntity) {
			if (EnchantmentHelper.getEnchantments(((LivingEntity) entityIn).getHeldItemMainhand().getStack()) == Enchantments.FIRE_ASPECT || EnchantmentHelper.getEnchantments(((LivingEntity) entityIn).getHeldItemOffhand().getStack()) == Enchantments.FIRE_ASPECT) {
				worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState(), 2);
			}
		}
		if(!worldIn.isRemote && entityIn.isBurning()) {
			worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState(), 2);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for(int j = 0; j < 1; ++j) {
			double d0 = (double)pos.getX() + rand.nextDouble();
			double d1 = (double)pos.getY() + rand.nextDouble();
            double d2 = (double)pos.getZ() + rand.nextDouble();
            int k = rand.nextInt(2) * 2 - 1;
            if (rand.nextBoolean()) {
            	d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
            } else {
            	d0 = (double)pos.getX() + 0.5D + 0.25D * (double)k;
            }
            worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, d0, d1, d2, 0.0, 0.0, 0.0);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (!stateIn.isValidPosition(worldIn, currentPos)) {
			worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		for(Direction direction : Direction.values()) {
			BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
			if (blockstate.isBurning(worldIn, pos) || blockstate == Blocks.FIRE.getDefaultState()) {
				return false;
			}
		}
		
		BlockState upBlockstate = worldIn.getBlockState(pos.offset(Direction.UP));
		BlockState downBlockstate = worldIn.getBlockState(pos.offset(Direction.DOWN));
		BlockState northBlockstate = worldIn.getBlockState(pos.offset(Direction.NORTH));
		BlockState southBlockstate = worldIn.getBlockState(pos.offset(Direction.SOUTH));
		BlockState eastBlockstate = worldIn.getBlockState(pos.offset(Direction.EAST));
		BlockState westBlockstate = worldIn.getBlockState(pos.offset(Direction.WEST));
		if (upBlockstate != ClinkerBlocks.FOUL_AIR.get().getDefaultState() && 
			downBlockstate != ClinkerBlocks.FOUL_AIR.get().getDefaultState() &&
			northBlockstate != ClinkerBlocks.FOUL_AIR.get().getDefaultState() &&
			southBlockstate != ClinkerBlocks.FOUL_AIR.get().getDefaultState() &&
			eastBlockstate != ClinkerBlocks.FOUL_AIR.get().getDefaultState() &&
			westBlockstate != ClinkerBlocks.FOUL_AIR.get().getDefaultState())
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public Vector3d getFogColor(BlockState state, IWorldReader world, BlockPos pos, Entity entity, Vector3d originalColor, float partialTicks) {
		return new Vector3d(0.34F, 0.35F, 0.24F);
	}
	
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face)
	{
	    return true;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 300;
	}
	
	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}

	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}
}
