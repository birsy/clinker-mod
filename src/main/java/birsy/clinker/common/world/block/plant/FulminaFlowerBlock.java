package birsy.clinker.common.world.block.plant;

import birsy.clinker.common.world.block.AshLayerBlock;
import birsy.clinker.common.world.block.blockentity.FulminaFlowerBlockEntity;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import birsy.clinker.core.registry.ClinkerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FulminaFlowerBlock extends DoublePlantBlock implements EntityBlock {
    protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public FulminaFlowerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LIT, false));
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(ClinkerBlocks.MUD.get())
                || pState.is(ClinkerBlocks.BRIMSTONE.get())
                || pState.is(ClinkerBlocks.ASH.get())
                || pState == ClinkerBlocks.ASH_LAYER.get().defaultBlockState().setValue(AshLayerBlock.LAYERS, 8);
    }

    @Override
    public VoxelShape getShape(BlockState p_261515_, BlockGetter p_261586_, BlockPos p_261526_, CollisionContext p_261930_) {
        Vec3 vec3 = p_261515_.getOffset(p_261586_, p_261526_);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FulminaFlowerBlockEntity(pos, state);
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ClinkerBlockEntities.FULMINA_FLOWER.get(), FulminaFlowerBlockEntity::tick);
    }

    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
        return p_152134_ == p_152133_ ? (BlockEntityTicker<A>)p_152135_ : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51305_) {
        p_51305_.add(LIT, HALF);
    }
}
