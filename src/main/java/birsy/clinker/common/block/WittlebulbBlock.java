package birsy.clinker.common.block;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class WittlebulbBlock extends Block implements IGrowable, net.minecraftforge.common.IForgeShearable {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 3.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public WittlebulbBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        super.randomTick(state, worldIn, pos, random);
        if (random.nextInt(2000) == 0) {
            worldIn.setBlockState(pos, ClinkerBlocks.BLOOMING_WITTLEBULB.get().getDefaultState());
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(Direction.UP));
        return blockstate.isIn(ClinkerBlocks.SILT_BLOCK.get()) ? this.grow(context.getWorld()) : ClinkerBlocks.WITTLEBULB.get().getDefaultState();
    }

    public BlockState grow(IWorld world) {
        return this.getDefaultState();
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.offset(Direction.DOWN.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (!this.canGrowOn(block)) {
            return false;
        } else {
            return blockstate.getBlock() == ClinkerBlocks.SILT_BLOCK.get() && blockstate.isSolidSide(worldIn, blockpos, Direction.DOWN);
        }
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!state.isValidPosition(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    protected boolean canGrowOn(Block block) {
        return block == ClinkerBlocks.SILT_BLOCK.get();
    }
    
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        worldIn.setBlockState(pos, ClinkerBlocks.BLOOMING_WITTLEBULB.get().getDefaultState());
    }
}
