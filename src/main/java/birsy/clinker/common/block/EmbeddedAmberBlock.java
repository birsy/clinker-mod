package birsy.clinker.common.block;

import birsy.clinker.common.block.blockentity.EmbeddedAmberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EmbeddedAmberBlock extends AmberBlock implements EntityBlock {
    public EmbeddedAmberBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (level instanceof ServerLevel serverLevel && serverLevel.getBlockEntity(pos) instanceof EmbeddedAmberBlockEntity blockEntity) {
            blockEntity.dropEmbeddedObjects();
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EmbeddedAmberBlockEntity(pos, state);
    }
}
