package birsy.clinker.common.blockentity;

import birsy.clinker.common.block.SarcophagusBlock;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SarcophagusBlockEntity extends BlockEntity {
    private float[] trueNeighboringLevels = new float[Direction.values().length + 1];
    private float[] pNeighboringLevels = new float[Direction.values().length + 1];
    private float[] neighboringLevels = new float[Direction.values().length + 1];

    public SarcophagusBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ClinkerBlockEntities.SARCOPHAGUS_INNARDS.get(), pWorldPosition, pBlockState);

        for (int i = 0; i < this.neighboringLevels.length; i++) {
            this.trueNeighboringLevels[i] = 0;
            this.pNeighboringLevels[i] = 0;
            this.neighboringLevels[i] = 0;
        }
    }

    public float getNeighboringLevels(int index, float partialTick) {
        return Mth.lerp(partialTick, pNeighboringLevels[index], neighboringLevels[index]);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SarcophagusBlockEntity pBlockEntity) {
       //hey! look at that, it's empty. wonder why!
    }

    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, SarcophagusBlockEntity pBlockEntity) {
        for (int i = 0; i < pBlockEntity.neighboringLevels.length; i++) {
            pBlockEntity.pNeighboringLevels[i] = pBlockEntity.neighboringLevels[i];
        }
        BlockPos.MutableBlockPos blockPos = pPos.mutable();

        int j = 0;
        for (Direction direction : Direction.values()) {
            blockPos.setWithOffset(blockPos, direction);
            BlockState state = pLevel.getBlockState(blockPos);

            if (state.getBlock() instanceof SarcophagusBlock) {
                pBlockEntity.trueNeighboringLevels[j] = state.getValue(SarcophagusBlock.GUTS);
            } else {
                pBlockEntity.trueNeighboringLevels[j] = -1;
            }

            j++;
        }
        pBlockEntity.trueNeighboringLevels[j] = pState.getValue(SarcophagusBlock.GUTS);


        for (int k = 0; k < pBlockEntity.neighboringLevels.length; k++) {
            pBlockEntity.neighboringLevels[k] = Mth.lerp(0.2F, pBlockEntity.neighboringLevels[k], pBlockEntity.trueNeighboringLevels[k]);
        }
    }
}
