package birsy.clinker.common.block;

import java.util.Random;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
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
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public FoulAirBlock()
	{
		super(Block.Properties.create(Material.MISCELLANEOUS, MaterialColor.AIR).doesNotBlockMovement().noDrops().notSolid().zeroHardnessAndResistance().tickRandomly());
		this.setDefaultState(this.getStateContainer().getBaseState().with(LIT, false));
	}

	@Override
	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}
	
	public void burn(BlockState state, World worldIn, BlockPos pos) {
		if (state.get(LIT)) {
			worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
		} else {
			worldIn.setBlockState(pos, state.with(LIT, true));
		}
	}

	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, 3);

		if (shouldBurn(state, worldIn, pos)) {
			this.burn(state, worldIn, pos);
		} else if (!isValidPosition(state, worldIn, pos)) {
			worldIn.removeBlock(pos, false);
		}
	}


	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		int minNeighbors = 3;
		int neighbors = 0;

		for (Direction direction : Direction.values()) {
			BlockState blockstate = worldIn.getBlockState(pos.offset(direction));
			if (blockstate.matchesBlock(ClinkerBlocks.FOUL_AIR.get())) {
				neighbors++;
			}
		}

		return neighbors >= minNeighbors;
	}

	public boolean shouldBurn(BlockState state, IWorldReader worldIn, BlockPos pos) {
		if (state.get(LIT)) {
			return true;
		} else {
			for (Direction direction : Direction.values()) {
				BlockState blockState = worldIn.getBlockState(pos.offset(direction));
				Block block = blockState.getBlock();

				if (blockState.matchesBlock(ClinkerBlocks.FOUL_AIR.get())) {
					return blockState.get(LIT);
				} else if (block.getDefaultState().isBurning(worldIn, pos) || block.isIn(BlockTags.FIRE)|| block == Blocks.TORCH || block == Blocks.SOUL_TORCH || block == Blocks.CAMPFIRE || block == Blocks.SOUL_CAMPFIRE || block == Blocks.REDSTONE_TORCH) {
					return true;
				}
			}

			return false;
		}
	}


	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote) {
			if (entityCausesFire(entityIn, worldIn)) {
				this.burn(state, worldIn, pos);
			} else if (entityIn instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity) entityIn;
				if (!livingentity.isInvulnerableTo(DamageSource.IN_WALL)) {
					livingentity.attackEntityFrom(DamageSource.IN_WALL, 0.25F);
				}
			}
		}
	}

	private boolean entityCausesFire(Entity entityIn, World worldIn) {
		if (entityIn.isBurning() || entityIn instanceof BlazeEntity) {
			return true;
		} else if (entityIn instanceof LivingEntity) {
			Item mainHand = ((LivingEntity) entityIn).getHeldItemMainhand().getItem();
			Item offHand = ((LivingEntity) entityIn).getHeldItemOffhand().getItem();
			//TODO: Replace this with an item tag.
			boolean isHoldingFlames = mainHand == Items.TORCH || offHand == Items.TORCH || mainHand == Items.SOUL_TORCH || offHand == Items.SOUL_TORCH || mainHand == Items.CAMPFIRE || offHand == Items.CAMPFIRE || mainHand == Items.SOUL_CAMPFIRE || offHand == Items.REDSTONE_TORCH || mainHand == Items.REDSTONE_TORCH || offHand == Items.BLAZE_ROD || offHand == Items.BLAZE_POWDER;
			boolean holdingFireAspect = EnchantmentHelper.getFireAspectModifier((LivingEntity) entityIn) > 0;

			return worldIn.getDifficulty() == Difficulty.HARD && (holdingFireAspect || isHoldingFlames);
		}

		return false;
	}


	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for(int j = 0; j < 1; ++j) {
			double x = (double) pos.getX() + rand.nextDouble();
			double y = (double) pos.getY() + rand.nextDouble();
            double z = (double) pos.getZ() + rand.nextDouble();
            int k = rand.nextInt(2) * 2 - 1;

            if (rand.nextBoolean()) {
            	z = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
            } else {
            	x = (double) pos.getX() + 0.5D + 0.25D * (double) k;
            }

            worldIn.addParticle(stateIn.get(LIT) ? ParticleTypes.FLAME : ParticleTypes.ENTITY_EFFECT, x, y, z, 0.0, 0.0, 0.0);
		}
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
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
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
