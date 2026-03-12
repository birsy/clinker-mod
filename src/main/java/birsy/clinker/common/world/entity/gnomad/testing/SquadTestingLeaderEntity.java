package birsy.clinker.common.world.entity.gnomad.testing;

import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadManager;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadMember;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

import java.util.List;
import java.util.UUID;

public class SquadTestingLeaderEntity extends SquadTestingEntity<SquadTestingLeaderEntity> {
    public SquadTestingLeaderEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.level() instanceof ServerLevel serverLevel) {
            // leaders create new squads
            if (this.getSquad() == null) this.setSquad(SquadManager.get(serverLevel).getOrCreate(UUID.randomUUID()));
            List<SquadMember<?>> nearbyPotentialRecruits = EntityRetrievalUtil.getEntities(
                    this,
                    10, 10, 10,
                    entity -> {
                        if (entity instanceof SquadMember<?> squadMember) return squadMember.getSquad() == null;
                        return false;
                    }
            );
            for (SquadMember<?> nearbyPotentialRecruit : nearbyPotentialRecruits) {
                nearbyPotentialRecruit.setSquad(this.getSquad());
            }
        }
    }
}
