package birsy.clinker.common.block.plant;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.registry.ClinkerItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TwizzlingVinePlantBlock extends AttachedGrowingPlantBodyBlock {
    static VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public TwizzlingVinePlantBlock(Properties pProperties) {
        super(pProperties, Direction.DOWN, SHAPE, false);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ATTACHED, false));
    }

    public GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) ClinkerBlocks.TWIZZLING_VINE.get();
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Vec3 vec3 = pState.getOffset(pLevel, pPos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
        return new ItemStack(ClinkerItems.TWIZZLING_VINE_ITEM.get());
    }
}
