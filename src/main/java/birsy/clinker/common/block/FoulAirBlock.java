package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FoulAirBlock extends Block
{
	public FoulAirBlock()
	{
		super(((Block.Properties.of(Material.DECORATION).randomTicks())));
	}
	
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(7) == 0) {
			if (!state.canSurvive(worldIn, pos)) {
				worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);			
			}
		}
		
		for(Direction direction : Direction.values()) {
			BlockState blockstate = worldIn.getBlockState(pos.relative(direction));
			if (blockstate == Blocks.TORCH.defaultBlockState()) {
				worldIn.setBlock(pos, Blocks.FIRE.defaultBlockState(), 2);
			}
		}
	}

	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isClientSide && worldIn.getDifficulty() != Difficulty.PEACEFUL) {
			if (entityIn instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity)entityIn;
				if (!livingentity.isInvulnerableTo(DamageSource.DRY_OUT)) {
					livingentity.hurt(DamageSource.DRY_OUT, 0.5F);
				}
			}
		}
		if (!worldIn.isClientSide && worldIn.getDifficulty() == Difficulty.HARD && entityIn instanceof LivingEntity) {
			if (EnchantmentHelper.getEnchantments(((LivingEntity) entityIn).getMainHandItem().getStack()) == Enchantments.FIRE_ASPECT || EnchantmentHelper.getEnchantments(((LivingEntity) entityIn).getOffhandItem().getStack()) == Enchantments.FIRE_ASPECT) {
				worldIn.setBlock(pos, Blocks.FIRE.defaultBlockState(), 2);
			}
		}
		if(!worldIn.isClientSide && entityIn.isOnFire()) {
			worldIn.setBlock(pos, Blocks.FIRE.defaultBlockState(), 2);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
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
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (!stateIn.canSurvive(worldIn, currentPos)) {
			worldIn.getBlockTicks().scheduleTick(currentPos, this, 1);
		}
		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		for(Direction direction : Direction.values()) {
			BlockState blockstate = worldIn.getBlockState(pos.relative(direction));
			if (blockstate.isBurning(worldIn, pos) || blockstate == Blocks.FIRE.defaultBlockState()) {
				return false;
			}
		}
		
		BlockState upBlockstate = worldIn.getBlockState(pos.relative(Direction.UP));
		BlockState downBlockstate = worldIn.getBlockState(pos.relative(Direction.DOWN));
		BlockState northBlockstate = worldIn.getBlockState(pos.relative(Direction.NORTH));
		BlockState southBlockstate = worldIn.getBlockState(pos.relative(Direction.SOUTH));
		BlockState eastBlockstate = worldIn.getBlockState(pos.relative(Direction.EAST));
		BlockState westBlockstate = worldIn.getBlockState(pos.relative(Direction.WEST));
		if (upBlockstate != ClinkerBlocks.FOUL_AIR.get().defaultBlockState() && 
			downBlockstate != ClinkerBlocks.FOUL_AIR.get().defaultBlockState() &&
			northBlockstate != ClinkerBlocks.FOUL_AIR.get().defaultBlockState() &&
			southBlockstate != ClinkerBlocks.FOUL_AIR.get().defaultBlockState() &&
			eastBlockstate != ClinkerBlocks.FOUL_AIR.get().defaultBlockState() &&
			westBlockstate != ClinkerBlocks.FOUL_AIR.get().defaultBlockState())
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public Vec3 getFogColor(BlockState state, LevelReader world, BlockPos pos, Entity entity, Vec3 originalColor, float partialTicks) {
		return new Vec3(0.34F, 0.35F, 0.24F);
	}
	
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
	
	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face)
	{
	    return true;
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 300;
	}
	
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}
}
