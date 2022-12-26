package birsy.clinker.common.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwizzlingVineBlock extends AttachedGrowingPlantHeadBlock {
    static VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    public TwizzlingVineBlock(Properties pProperties) {
        super(pProperties, Direction.DOWN, SHAPE, false, 0.1D);
    }

    public int getBlocksToGrowWhenBonemealed(RandomSource pRandom) {
        return pRandom.nextInt(1,3);
    }

    public boolean canGrowInto(BlockState pState) {
        return pState.isAir();
    }

    public Block getBodyBlock() {
        return ClinkerBlocks.TWIZZLING_VINE_PLANT.get();
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Vec3 vec3 = pState.getOffset(pLevel, pPos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }
}
