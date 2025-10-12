package birsy.clinker.common.world.block.plant;

import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.IShearable;

public class SheetMossBlock extends OthershorePlantBlock implements IShearable {
    public static final VoxelShape SHAPE = Block.box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);

    public SheetMossBlock(Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Vec3 offset = pState.getOffset(pLevel, pPos);
        return SHAPE.move(offset.x, 0, offset.z);
    }

    public static boolean canPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(ClinkerTags.OTHERSHORE_SOIL) && pState.isFaceSturdy(pLevel, pPos, Direction.DOWN);
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return canPlaceOn(pState, pLevel, pPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState aboveBlockState = level.getBlockState(blockpos);
        net.neoforged.neoforge.common.util.TriState soilDecision = aboveBlockState.canSustainPlant(level, blockpos, Direction.DOWN, state);
        if (!soilDecision.isDefault()) return soilDecision.isTrue();

        return this.mayPlaceOn(aboveBlockState, level, blockpos);
    }
}
