package birsy.clinker.common.entity.module.modules;

import birsy.clinker.common.entity.gnomad.BaseGnomadEntity;
import birsy.clinker.common.entity.module.Module;
import birsy.clinker.common.entity.module.ModuleHolder;
import birsy.clinker.common.entity.system.squad.Squad;
import birsy.clinker.common.entity.system.squad.SquadSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface SquadMember<E extends LivingEntity & ModuleHolder<E> & SquadMember<E>> {
    SquadMember.SquadModule<E> squadModule();
    default E asEntity() { return (E) this; }

    class SquadModule<E extends LivingEntity & ModuleHolder<E> & SquadMember<E>> extends Module<E> {
        public final float positionWeight;
        @Nullable Squad squad;

        public SquadModule(E self, float positionWeight) {
            super(self);
            this.positionWeight = positionWeight;
        }

        public @Nullable Squad getSquad() { return squad; }

        public void setSquad(@Nullable Squad newSquad) {
            if (this.squad != null) this.squad.removeMember(self);
            if (newSquad != null) newSquad.addMember(self);
            this.squad = newSquad;
        }

        @Override
        public void save(CompoundTag nbt) {
            if (this.getSquad() != null) {
                nbt.putUUID("SquadUUID", this.getSquad().uuid);
                nbt.putBoolean("SquadLeader", this.getSquad().getLeader() == self);
            }
        }
        @Override
        public void load(CompoundTag nbt) {
            if (self.level() instanceof ServerLevel serverLevel) {
                Squad squad = SquadSystem.get(serverLevel).getOrCreate(nbt.getUUID("SquadUUID"));
                this.setSquad(squad);
                if (nbt.getBoolean("SquadLeader")) squad.setLeader(self);
            }
        }
    }
}
