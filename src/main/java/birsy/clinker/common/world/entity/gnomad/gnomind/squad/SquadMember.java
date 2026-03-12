package birsy.clinker.common.world.entity.gnomad.gnomind.squad;

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
        if (this.getSquad() != null) nbt.putUUID("SquadUUID", this.getSquad().uuid);
    }
    default void deserializeSquad(CompoundTag nbt) {
        if (nbt.contains("SquadUUID") && this.asEntity().level() instanceof ServerLevel serverLevel)
            this.setSquad(SquadManager.get(serverLevel).getOrCreate(nbt.getUUID("SquadUUID")));
    }
}
