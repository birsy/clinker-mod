package birsy.clinker.common.world.entity.gnomad.squad;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.schedule.Activity;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.Optional;

public class RestWithFriendsTask extends GnomadSquadTask {
    final BlockPos restPos;
    public RestWithFriendsTask(BlockPos restPos, GnomadEntity taskmaster) {
        super(0, 5, RandomUtil.randomNumberBetween(20 * 10, 20 * 120), taskmaster);
        this.restPos = restPos;
        this.volunteer(taskmaster);
    }

    @Override
    protected boolean canVolunteer(GnomadEntity volunteer) {
        return true;
    }

    @Override
    protected Optional<FailureType> shouldFail() {
        if (this.remainingTime() <= 0) return Optional.of(FailureType.TIMED_OUT);
        if (this.volunteers.isEmpty()) return Optional.of(FailureType.NO_VOLUNTEERS);
        return Optional.empty();
    }

    @Override
    public boolean volunteer(GnomadEntity volunteer) {
        boolean volunteered = super.volunteer(volunteer);
        if (volunteered) {
            BrainUtils.setForgettableMemory(volunteer,
                    ClinkerMemoryModules.RELAXATION_SPOT.get(),
                    GlobalPos.of(volunteer.level().dimension(), this.restPos),
                    this.remainingTime()
            );
            Clinker.LOGGER.info(volunteer.getBrain().getActiveActivities().toString());
        }
        return volunteered;
    }
}
