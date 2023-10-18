package birsy.clinker.common.world.block.blockentity;

import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import birsy.clinker.core.registry.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CounterBlockEntity extends BlockEntity {
    public Workstation workstation;

    public CounterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ClinkerBlockEntities.COUNTER.get(), pPos, pBlockState);

    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level.isClientSide()) {
            if (WorkstationManager.clientWorkstationManager != null) this.workstation = WorkstationManager.clientWorkstationManager.getWorkstationAtBlock(this.getBlockPos());
        } else {
            this.workstation = WorkstationManager.managerByLevel.get(this.level).getWorkstationAtBlock(this.getBlockPos());
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CounterBlockEntity entity) {
        if (level.isClientSide()) {
            entity.workstation = WorkstationManager.clientWorkstationManager.getWorkstationAtBlock(pos);
        } else {
            entity.workstation = WorkstationManager.managerByLevel.get(level).getWorkstationAtBlock(pos);
        }
    }
}
