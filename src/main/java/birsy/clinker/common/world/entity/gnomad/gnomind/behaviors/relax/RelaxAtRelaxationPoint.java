package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors.relax;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.common.world.entity.ai.LookTargetController;
import birsy.clinker.common.world.entity.ai.Sittable;
import birsy.clinker.common.world.entity.gnomad.gnomind.squadtasks.RelaxWithSquadTask;
import birsy.clinker.common.world.entity.system.squad.SquadMember;
import birsy.clinker.common.world.entity.system.squad.SquadTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.HeldBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.List;

public class RelaxAtRelaxationPoint<E extends LivingEntity & SquadMember<E>> extends HeldBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
            .usesMemory(ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get())
            .usesMemory(ClinkerMemoryModules.IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE.get())
            .usesMemory(MemoryModuleType.WALK_TARGET);
    float minimumDistance = 4.0F, minimumDistanceSqr = minimumDistance * minimumDistance;
    private LookTargetController.LookTargetHandle bodyTargetHandle;

    public RelaxAtRelaxationPoint() {
        super();
        this.onTick(this::onTick);
    }

    public RelaxAtRelaxationPoint<E> minimumDistance(float distance) {
        minimumDistance = distance;
        minimumDistanceSqr = minimumDistance * minimumDistance;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        // check if our current task is the relaxation task and is active
        SquadTask currentTask = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (currentTask instanceof RelaxWithSquadTask relaxationTask && relaxationTask.isActive()) {
            // we have to be in the same dimension
            if (relaxationTask.relaxationPoint.dimension() != level.dimension()) return false;

            BlockPos pos = relaxationTask.relaxationPoint.pos();
            return pos.distToCenterSqr(mob.position()) < minimumDistanceSqr;
        }
        return false;
    }

    @Override
    protected void start(E mob) {
        if (mob instanceof Sittable sittable) sittable.setSitting(true);
    }

    boolean onTick(E mob) {
        BrainUtils.clearMemory(mob, MemoryModuleType.WALK_TARGET);

        // sometimes we just decide to get up and leave
        if (RandomUtil.oneInNChance(30 * 20)) return false;

        // make sure this is still the task at hand
        SquadTask currentTask = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (currentTask instanceof RelaxWithSquadTask relaxationTask && relaxationTask.isActive()) {
            if (relaxationTask.relaxationPoint.dimension() != mob.level().dimension()) return false;

            BlockPos pos = relaxationTask.relaxationPoint.pos();

            // face our body towards the relaxation point
            if (bodyTargetHandle == null && mob instanceof GroundLocomotionEntity gle){
                bodyTargetHandle = gle.getBodyRotationControl().lookTargetController.createHandle(0.1F, 100);
                bodyTargetHandle.fadeIn(0.5F, true);
            }
            if (bodyTargetHandle != null)
                bodyTargetHandle.face(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

            return pos.distToCenterSqr(mob.position()) < minimumDistanceSqr + 1;
        }

        return false;
    }

    @Override
    protected void stop(E mob) {
        if (mob instanceof Sittable sittable) sittable.setSitting(false);
        if (bodyTargetHandle != null) bodyTargetHandle.fadeOut(0.5F, false);
        SquadTask currentTask = BrainUtils.getMemory(mob, ClinkerMemoryModules.CURRENTLY_ASSIGNED_SQUAD_TASK.get());
        if (currentTask instanceof RelaxWithSquadTask relaxationTask) relaxationTask.unassign(mob);
    }
}
