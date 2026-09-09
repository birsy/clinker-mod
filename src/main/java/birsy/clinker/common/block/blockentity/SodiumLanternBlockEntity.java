package birsy.clinker.common.block.blockentity;

import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SodiumLanternBlockEntity extends BlockEntity {
    public SodiumLanternBlockEntity(BlockPos pos, BlockState state) {
        super(ClinkerBlockEntities.SODIUM_LANTERN.get(), pos, state);
    }
}
