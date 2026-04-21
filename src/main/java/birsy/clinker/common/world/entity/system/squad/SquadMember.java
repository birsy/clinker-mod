package birsy.clinker.common.world.entity.system.squad;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public interface SquadMember<E extends LivingEntity> {
    @Nullable
    Squad getSquad();
    void setSquad(@Nullable Squad squad);

    default E asEntity() { return (E) this; }
    default void serializeSquad(CompoundTag nbt) {
        if (this.getSquad() != null) {
            nbt.putUUID("SquadUUID", this.getSquad().uuid);
            nbt.putBoolean("SquadLeader", this.getSquad().leader == this);
        }
    }
    default void deserializeSquad(CompoundTag nbt) {
        if (nbt.contains("SquadUUID") && this.asEntity().level() instanceof ServerLevel serverLevel) {
            Squad squad = SquadSystem.get(serverLevel).getOrCreate(nbt.getUUID("SquadUUID"));
            this.setSquad(squad);
            if (nbt.getBoolean("SquadLeader")) squad.setLeader(this);
        }
    }
    default float squadPositionWeight() {
        return 1.0F;
    }
}
