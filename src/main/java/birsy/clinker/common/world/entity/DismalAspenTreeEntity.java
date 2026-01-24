package birsy.clinker.common.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DismalAspenTreeEntity extends Entity implements Attackable {
    public Limb trunk;
    public List<Limb> legs;

    public DismalAspenTreeEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public @Nullable LivingEntity getLastAttacker() {
        return null;
    }

    public static class Limb {
        HashMap<BlockPos, BlockState> map = new HashMap<>();
        public void place(Level level, BlockPos origin) {
            BlockPos.MutableBlockPos pos = origin.mutable();
            for (Map.Entry<BlockPos, BlockState> entry : map.entrySet()) {
                BlockPos offset = entry.getKey();
                pos.setWithOffset(origin, offset);
                BlockState state = entry.getValue();
                level.setBlock(pos, state, 2);
            }
        }
    }
}
