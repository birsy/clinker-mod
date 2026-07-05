package birsy.clinker.common.world.block.blockentity;

import birsy.clinker.client.entity.SingleBoneSkeleton;
import birsy.clinker.core.registry.entity.ClinkerBlockEntities;
import foundry.veil.api.client.necromancer.Skeleton;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PressureCookerBlockEntity extends BlockEntity {
    public PressureCookerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClinkerBlockEntities.PRESSURE_COOKER.get(), pos, blockState);
    }
}
