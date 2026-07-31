package birsy.clinker.common.entity.gnomad.gnomind.squadtasks;

import birsy.clinker.common.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.entity.system.squad.SquadMember;
import birsy.clinker.common.entity.system.squad.SquadTask;
import birsy.clinker.core.registry.entity.ClinkerActivities;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.smartbrainlib.util.BrainUtils;

public class ResupplyTask extends SquadTask {
    public ResupplyTask(SquadMember<? extends SuppliesHolder> taskmaster) {
        super(taskmaster, 1, 1, 160, 600, 0);
    }

    @Override
    protected void onBegin() {
        if (this.assignees.isEmpty()) return;
        if (this.assignees.getFirst() instanceof LivingEntity entity){
            BrainUtils.setMemory(entity, ClinkerMemoryModules.DELIVERY_TARGET.get(), this.taskMaster().asEntity());
            entity.getBrain().setActiveActivityIfPossible(ClinkerActivities.DELIVER_SUPPLIES.get());
        }
    }

    @Override
    protected void tick() {
        super.tick();
    }

    @Override
    protected void onStop() {
        if (this.assignees.isEmpty()) return;
        if (this.assignees.getFirst() instanceof LivingEntity entity)
            BrainUtils.clearMemory(entity, ClinkerMemoryModules.DELIVERY_TARGET.get());
    }

    @Override
    public boolean shouldSucceed() {
        SuppliesHolder taskMaster = (SuppliesHolder) this.taskMaster;
        return !taskMaster.outOfSupplies();
    }

    public SquadMember<? extends SuppliesHolder> taskMaster() {
        return (SquadMember<? extends SuppliesHolder>) this.taskMaster;
    }
}
