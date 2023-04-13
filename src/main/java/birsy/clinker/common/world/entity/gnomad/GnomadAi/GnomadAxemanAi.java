package birsy.clinker.common.world.entity.gnomad.GnomadAi;

import birsy.clinker.common.world.entity.gnomad.GnomadAxemanEntity;
import birsy.clinker.core.registry.ClinkerEntities;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.*;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Optional;

public class GnomadAxemanAi {

    public static Brain<?> makeBrain(GnomadAxemanEntity entity, Brain<GnomadAxemanEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        //initFightActivity(entity, brain);
        //initRetreatActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }
    
    public static void updateActivity(GnomadAxemanEntity gnomad) {
        Brain<GnomadAxemanEntity> brain = gnomad.getBrain();
        
        Activity activity = brain.getActiveNonCoreActivity().orElse(null);
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
        Activity activity1 = brain.getActiveNonCoreActivity().orElse(null);
        if (activity != activity1) {
            getSoundForCurrentActivity(gnomad).ifPresent(gnomad::playSound);
        }

        gnomad.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
        if (!brain.hasMemoryValue(MemoryModuleType.CELEBRATE_LOCATION)) {
            brain.eraseMemory(MemoryModuleType.DANCING);
        }
    }

    public static Optional<SoundEvent> getSoundForCurrentActivity(GnomadAxemanEntity gnomad) {
        return gnomad.getBrain().getActiveNonCoreActivity().map((activity) -> getSoundForActivity(gnomad, activity));
    }

    private static SoundEvent getSoundForActivity(GnomadAxemanEntity gnomad, Activity activity) {
        if (activity == Activity.FIGHT) {
            return SoundEvents.PIGLIN_ANGRY;
        } else if (activity == Activity.AVOID) {
            return SoundEvents.PIGLIN_RETREAT;
        } else if (activity == Activity.ADMIRE_ITEM) {
            return SoundEvents.PIGLIN_ADMIRING_ITEM;
        } else if (activity == Activity.CELEBRATE) {
            return SoundEvents.PIGLIN_CELEBRATE;
        } else {
            return SoundEvents.PIGLIN_AMBIENT;
        }
    }
    
    private static void initCoreActivity(Brain<GnomadAxemanEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new InteractWithDoor(),
                new StopBeingAngryIfTargetDead<>()));
    }

    private static void initIdleActivity(Brain<GnomadAxemanEntity> brain) {
        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                new SetEntityLookTarget(PiglinAi::isPlayerHoldingLovedItem, 14.0F),
                createIdleLookBehaviors(),
                createIdleMovementBehaviors(),
                new SetLookAndInteract(EntityType.PLAYER, 4)));
    }

    private static RunOne<GnomadAxemanEntity> createIdleLookBehaviors() {
        return new RunOne<>(ImmutableList.of(
                Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 1),
                Pair.of(new SetEntityLookTarget(ClinkerEntities.GNOMAD_AXEMAN.get(), 8.0F), 1),
                Pair.of(new SetEntityLookTarget(8.0F), 1),
                Pair.of(new DoNothing(30, 60), 1)));
    }

    private static RunOne<GnomadAxemanEntity> createIdleMovementBehaviors() {
        return new RunOne<>(ImmutableList.of(
                Pair.of(new RandomStroll(0.6F), 2),
                Pair.of(new DoNothing(30, 60), 1)));
    }
}
