package birsy.clinker.common.block.silt;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WittlebulbBlock extends Block implements BonemealableBlock, net.minecraftforge.common.IForgeShearable {
    protected static final VoxelShape SHAPE = Block.box(2.0D, 3.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public WittlebulbBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
        super.randomTick(state, worldIn, pos, random);
        if (random.nextInt(2000) == 0) {
            worldIn.setBlockAndUpdate(pos, ClinkerBlocks.BLOOMING_WITTLEBULB.get().defaultBlockState());
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos().relative(Direction.UP));
        return blockstate.is(ClinkerBlocks.SILT_BLOCK.get()) ? this.grow(context.getLevel()) : ClinkerBlocks.WITTLEBULB.get().defaultBlockState();
    }

    public BlockState grow(LevelAccessor world) {
        return this.defaultBlockState();
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.relative(Direction.DOWN.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (!this.canGrowOn(block)) {
            return false;
        } else {
            return blockstate.getBlock() == ClinkerBlocks.SILT_BLOCK.get() && blockstate.isFaceSturdy(worldIn, blockpos, Direction.DOWN);
        }
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        if (!state.canSurvive(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    protected boolean canGrowOn(Block block) {
        return block == ClinkerBlocks.SILT_BLOCK.get();
    }
    
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel worldIn, Random rand, BlockPos pos, BlockState state) {
        worldIn.setBlockAndUpdate(pos, ClinkerBlocks.BLOOMING_WITTLEBULB.get().defaultBlockState());
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }
}
