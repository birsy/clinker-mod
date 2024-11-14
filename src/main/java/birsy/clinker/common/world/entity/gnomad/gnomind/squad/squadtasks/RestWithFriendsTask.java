package birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.CancelCurrentSquadTask;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.SitAndRelaxWithSquad;
import birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.WalkToSquadRelaxationPoint;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.Optional;

public class RestWithFriendsTask extends GnomadSquadTask {
    public final BlockPos restPos;
    public RestWithFriendsTask(BlockPos restPos, GnomadEntity taskmaster) {
        super(0, 5, RandomUtil.randomNumberBetween(20 * 10, 20 * 120), taskmaster);
        this.ticksPerUpdate = 5;
        this.restPos = restPos;
        this.volunteer(taskmaster);
    }

    @Override
    public boolean shouldBegin() {
        return true;
    }

    @Override
    public Optional<FailureType> shouldFail() {
        if (!this.hasBegun) return Optional.empty();
        if (this.remainingTime() <= 0) return Optional.of(FailureType.TIMED_OUT);
        if (this.volunteers.isEmpty()) return Optional.of(FailureType.NO_VOLUNTEERS);
        return Optional.empty();
    }

    @Override
    public boolean shouldSucceed() {
        return false;
    }

    @Override
    public boolean canVolunteer(GnomadEntity volunteer) {
        return true;
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
        }
        return volunteered;
    }

    public static ExtendedBehaviour<GnomadEntity> createBehavior() {
        int radius = 5;
        return new FirstApplicableBehaviour<>(
                new WalkToSquadRelaxationPoint<>().sitRadius(mob -> radius).speedMod(mob -> 0.5F),
                new CancelCurrentSquadTask<>() // try to walk away, occasionally.
                        .whenStarting(mob -> BrainUtils.clearMemory(mob, ClinkerMemoryModules.RELAXATION_SPOT.get()))
                        .startCondition(mob -> RandomUtil.oneInNChance(60 * 20)),
                new SitAndRelaxWithSquad<>().sitRadius(mob -> radius)
        ).startCondition(mob -> BrainUtils.getMemory(mob, ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get()) instanceof RestWithFriendsTask
                && BrainUtils.hasMemory(mob, ClinkerMemoryModules.RELAXATION_SPOT.get()));
    }
}
