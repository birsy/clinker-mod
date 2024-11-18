package birsy.clinker.common.world.entity.ai.behaviors;

import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class AttackWithWindup<E extends Mob> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT),
            Pair.of(ClinkerMemoryModules.ATTACK_WINDUP.get(), MemoryStatus.REGISTERED)
    );

    protected Predicate<E> shouldContinueAttack = (entity) ->
                    entity.getTarget() != null &&
                    entity.getSensing().hasLineOfSight(entity.getTarget()) &&
                    entity.isWithinMeleeAttackRange(entity.getTarget());

    protected Consumer<E> beginWindup = entity -> {};

    protected Consumer<E> duringWindup = (entity) ->
            BehaviorUtils.lookAtEntity(entity, entity.getTarget());

    protected Consumer<E> attack = (entity) ->
            entity.doHurtTarget(entity.getTarget());

    protected Function<E, Integer> windup = entity -> 20;

    protected Function<E, Integer> cooldown = entity -> 20;

    public AttackWithWindup<E> shouldContinueAttack(Predicate<E> shouldContinueAttack) {
        this.shouldContinueAttack = shouldContinueAttack;
        return this;
    }

    public AttackWithWindup<E> beginWindup(Consumer<E> beginWindup) {
        this.beginWindup = beginWindup;
        return this;
    }

    public AttackWithWindup<E> duringWindup(Consumer<E> duringWindup) {
        this.duringWindup = duringWindup;
        return this;
    }

    public AttackWithWindup<E> attack(Consumer<E> attack) {
        this.attack = attack;
        return this;
    }

    public AttackWithWindup<E> windup(Function<E, Integer> windup) {
        this.windup = windup;
        return this;
    }

    public AttackWithWindup<E> cooldown(Function<E, Integer> cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return shouldContinueAttack.test(entity);
    }

    @Override
    protected void start(E entity) {
        BrainUtils.setForgettableMemory(entity, ClinkerMemoryModules.ATTACK_WINDUP.get(), Unit.INSTANCE, windup.apply(entity));
        beginWindup.accept(entity);
    }

    @Override
    protected void tick(E entity) {
        duringWindup.accept(entity);
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return BrainUtils.hasMemory(entity, ClinkerMemoryModules.ATTACK_WINDUP.get()) && shouldContinueAttack.test(entity);
    }

    @Override
    protected void stop(E entity) {
        if (shouldContinueAttack.test(entity)) attack.accept(entity);
        BrainUtils.clearMemory(entity, ClinkerMemoryModules.ATTACK_WINDUP.get());
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.cooldown.apply(entity));
    }
}
