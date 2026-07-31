package birsy.clinker.common.block.plant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class GiantSeaLilyBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<GiantSeaLilyBlock> CODEC = simpleCodec(GiantSeaLilyBlock::new);
    public static final VoxelShape[] SHAPES = {
            Block.box(6.0, 0.0, 6.0, 16.0, 1.5, 16.0),
            Block.box(6.0, 0.0, 0.0, 16.0, 1.5, 10.0),
            Block.box(0.0, 0.0, 0.0, 10.0, 1.5, 10.0),
            Block.box(0.0, 0.0, 6.0, 10.0, 1.5, 16.0)
    };

    public GiantSeaLilyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction direction = state.getValue(FACING);
            BlockPos.MutableBlockPos mPos = pos.mutable();
            for (int i = 0; i < 3; i++) {
                mPos.move(direction);
                direction = direction.getClockWise();
                level.setBlock(mPos, state.setValue(FACING, direction), 3);
                level.blockUpdated(pos, Blocks.AIR);
                state.updateNeighbourShapes(level, pos, 3);
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).ordinal()];
    }

    @Override
    protected MapCodec<? extends GiantSeaLilyBlock> codec() {
        return CODEC;
    }
}
