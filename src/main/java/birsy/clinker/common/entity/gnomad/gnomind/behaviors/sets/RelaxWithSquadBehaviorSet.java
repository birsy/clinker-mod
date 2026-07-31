package birsy.clinker.common.entity.gnomad.gnomind.behaviors.sets;

import birsy.clinker.common.entity.ai.Sitter;
import birsy.clinker.common.entity.ai.behaviors.DecisionBehaviour;
import birsy.clinker.common.entity.ai.behaviors.SetWalkTargetToPos;
import birsy.clinker.common.entity.ai.behaviors.ClaimSquadTask;
import birsy.clinker.common.entity.ai.behaviors.PostSquadTask;
import birsy.clinker.common.entity.gnomad.gnomind.squadtasks.RelaxWithSquadTask;
import birsy.clinker.common.entity.system.squad.SquadMember;
import birsy.clinker.core.registry.entity.ClinkerActivities;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.AllApplicableBehaviours;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomDelayedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.CustomHeldBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RelaxWithSquadBehaviorSet {
    private static final int DISTANCE_THRESHOLD = 4;
    public static <E extends Mob & Sitter & SquadMember<E> & SmartBrainOwner<E>> BrainActivityGroup<E> createActivity(int minTime, int maxTime) {
        return new BrainActivityGroup<E>(ClinkerActivities.RELAX.get())
                .behaviours(
                        SharedGnomadBehaviorSets.setIdleLookTargets(),
                        new DecisionBehaviour<E>(
                                (entity) -> {
                                    GlobalPos relaxPoint = getRelaxationPoint(entity);
                                    if (relaxPoint == null || relaxPoint.dimension() != entity.level().dimension()) return DecisionBehaviour.CANCEL; // invalid relaxation pos
                                    boolean tooFar = entity.distanceToSqr(relaxPoint.pos().getCenter()) >
                                            DISTANCE_THRESHOLD * DISTANCE_THRESHOLD;
                                    return tooFar ? 0 : 1;
                                },
                                new AllApplicableBehaviours<>(
                                        new SetWalkTargetToPos<>((entity) -> {
                                            GlobalPos relaxPoint = getRelaxationPoint(entity);
                                            return relaxPoint == null ? null : relaxPoint.pos().getCenter();
                                        }).closeEnoughWhen(DISTANCE_THRESHOLD - 1)
                                          .speedMod(0.5F)
                                          .lookAtTarget(false),
                                        // cancel the task if we can't reach it after ten seconds
                                        new CustomDelayedBehaviour<E>(200)
                                                .whenActivating(RelaxWithSquadBehaviorSet::stopRelaxing)
                                ),
                                new CustomHeldBehaviour<E>(RelaxWithSquadBehaviorSet::tickRelaxation)
                                        .whenStarting(RelaxWithSquadBehaviorSet::startRelaxing)
                                        .whenStopping(RelaxWithSquadBehaviorSet::stopRelaxing)
                                        .runForBetween(minTime, maxTime)
                        ).shouldInterrupt(true)
                         .stopIf((entity) -> entity.getLastDamageSource() != null)
                ).requireAndWipeMemoriesOnUse(ClinkerMemoryModules.RELAXATION_POSITION.get());
    }
    public static <E extends LivingEntity & Sitter & SquadMember<E>> ExtendedBehaviour<E> tryInitiate(int initiationChance, int joinChance) {
        if (initiationChance <= 0)
            return new ClaimSquadTask<E>(task -> task instanceof RelaxWithSquadTask)
                .startCondition(entity -> RandomUtil.oneInNChance(joinChance) && !BrainUtils.hasMemory(entity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get()))
                .cooldownFor(entity -> 600);

        return new FirstApplicableBehaviour<>(
                new ClaimSquadTask<E>(task -> task instanceof RelaxWithSquadTask)
                        .startCondition(entity -> RandomUtil.oneInNChance(joinChance))
                        .cooldownFor(entity -> 1200),
                new PostSquadTask<E, RelaxWithSquadTask>(
                        RelaxWithSquadTask.class,
                        (entity) -> {
                            BlockPos entityBlockPos = entity.blockPosition();
                            Level level = entity.level();
                            if (level instanceof ServerLevel serverLevel) {
                                Optional<BlockPos> attempt = serverLevel.getPoiManager().findClosest(
                                        typeHolder -> typeHolder.is(PoiTypes.LODESTONE),
                                        pos -> true, entityBlockPos, 32, PoiManager.Occupancy.ANY
                                );
                                if (attempt.isPresent()) return new RelaxWithSquadTask(entity, new GlobalPos(serverLevel.dimension(), attempt.get()));
                            }
                            return null;
                        }
                ).onStart((entity, task) -> task.assign(entity)) // assign myself!
                 .startCondition(entity -> RandomUtil.oneInNChance(initiationChance))
                 .cooldownFor(entity -> 3000)
        ).startCondition(entity -> !BrainUtils.hasMemory(entity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get()));
    }

    private static <E extends LivingEntity> @Nullable GlobalPos getRelaxationPoint(E entity) {
        return BrainUtils.getMemory(entity, ClinkerMemoryModules.RELAXATION_POSITION.get());
    }
    private static <E extends LivingEntity & Sitter> void startRelaxing(E entity) {
        entity.setSitting(true);
    }
    private static <E extends LivingEntity & Sitter & SquadMember<E>> void stopRelaxing(E entity) {
        entity.setSitting(false);
        if (BrainUtils.getMemory(entity, ClinkerMemoryModules.ASSIGNED_SQUAD_TASK.get()) instanceof RelaxWithSquadTask relaxTask)
            relaxTask.unassign(entity);
    }
    private static <E extends LivingEntity> void tickRelaxation(E entity) {
        // dont walk while relaxing!
        BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
    }
}
