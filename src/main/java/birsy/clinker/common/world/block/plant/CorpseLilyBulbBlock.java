package birsy.clinker.common.world.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CorpseLilyBulbBlock extends CorpseLilyCenterBlock {
    protected static final VoxelShape SHAPE = Shapes.join(
            Shapes.joinUnoptimized(
                    Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0),
                    Block.box(4.0, 0.1, 4.0, 12.0, 5.9, 12.0),
                    BooleanOp.ONLY_FIRST
            ),
            Block.box(5.0, 0.1, 5.0, 11.0, 16.0, 11.0),
            BooleanOp.ONLY_FIRST
    );

    public CorpseLilyBulbBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        BlockPos.MutableBlockPos mPos = pos.mutable();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            mPos.set(pos).move(direction);
            BlockState offsetState = level.getBlockState(mPos);
            if (offsetState.is(ClinkerBlocks.CORPSE_LILY_PETAL)) continue;
            if (!offsetState.canBeReplaced()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandomSource, BlockPos pPos, BlockState pState) {
        this.grow(pLevel, pPos, pState, pRandomSource);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(0, 5) == 0) this.grow(level, pos, state, random);
    }

    void grow(Level level, BlockPos pos, BlockState state, RandomSource random) {
        BlockPos.MutableBlockPos mPos = pos.mutable();
        for (Direction direction : Direction.Plane.HORIZONTAL.shuffledCopy(random)) {
            mPos.set(pos).move(direction);
            BlockState offsetState = level.getBlockState(mPos);

            if (offsetState.is(ClinkerBlocks.CORPSE_LILY_PETAL)) continue;
            if (!offsetState.canBeReplaced()) continue;

            BlockState petal = ClinkerBlocks.CORPSE_LILY_PETAL.get().defaultBlockState()
                    .setValue(CorpseLilyPetalBlock.FACING, direction.getOpposite())
                    .setValue(CorpseLilyPetalBlock.WATERLOGGED, level.getFluidState(mPos).is(Fluids.WATER));
            level.setBlock(mPos, petal, 2);
            return;
        }
    }

    public static void place(Level level, BlockPos pos) {
        BlockState bulb = ClinkerBlocks.CORPSE_LILY_BULB.get().defaultBlockState()
                .setValue(CorpseLilyPetalBlock.WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
        level.setBlock(pos, bulb, 2);

        BlockPos.MutableBlockPos mPos = pos.mutable();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            mPos.set(pos).move(direction);
            BlockState offsetState = level.getBlockState(mPos);
            if (offsetState.is(ClinkerBlocks.CORPSE_LILY_PETAL)) continue;
            if (!offsetState.canBeReplaced()) continue;

            BlockState petal = ClinkerBlocks.CORPSE_LILY_PETAL.get().defaultBlockState()
                    .setValue(CorpseLilyPetalBlock.FACING, direction.getOpposite())
                    .setValue(CorpseLilyPetalBlock.WATERLOGGED, level.getFluidState(mPos).is(Fluids.WATER));
            level.setBlock(mPos, petal, 2);
        }
    }
}
