package birsy.clinker.common.entity.gnomad.gnomind.squadtasks;

import birsy.clinker.common.entity.system.squad.SquadMember;
import birsy.clinker.common.entity.system.squad.SquadTask;
import birsy.clinker.core.registry.entity.ClinkerActivities;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;

public class RelaxWithSquadTask extends SquadTask {
    public final GlobalPos relaxationPoint;

    public RelaxWithSquadTask(SquadMember<?> taskmaster, GlobalPos relaxationPoint) {
        super(taskmaster, 1, RandomUtil.randomNumberBetween(1, 5), 20 * 60, 20 * 120, Integer.MIN_VALUE);
        this.relaxationPoint = relaxationPoint;
    }

    @Override
    public boolean assign(SquadMember<?> assignee) {
        if (assignee instanceof LivingEntity entity) {
            BrainUtils.setMemory(entity, ClinkerMemoryModules.RELAXATION_POSITION.get(), relaxationPoint);
            entity.getBrain().setActiveActivityIfPossible(ClinkerActivities.RELAX.get());
        }
        return super.assign(assignee);
    }

    @Override
    public boolean unassign(SquadMember<?> assignee) {
        if (assignee instanceof LivingEntity entity) {
            BrainUtils.clearMemory(entity, ClinkerMemoryModules.RELAXATION_POSITION.get());
        }
        return super.unassign(assignee);
    }

    @Override
    public boolean shouldSucceed() { return false; }
}
