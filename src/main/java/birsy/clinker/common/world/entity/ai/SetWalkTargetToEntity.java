package birsy.clinker.common.world.entity.ai;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SetWalkTargetToEntity<E extends Mob> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED),
            Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));

    protected Function<E, LivingEntity> entitySelector = (owner) -> owner.getTarget();
    protected BiFunction<E, LivingEntity, Float> speedMod = (owner, target) -> 1f;
    protected BiFunction<E, LivingEntity, Integer> closeEnoughWhen = (owner, target) -> 0;

    protected LivingEntity target;

    public SetWalkTargetToEntity<E> entitySelector(Function<E, LivingEntity> entitySelector) {
        this.entitySelector = entitySelector;
        return this;
    }

    public SetWalkTargetToEntity<E> speedMod(BiFunction<E, LivingEntity, Float> speedModifier) {
        this.speedMod = speedModifier;
        return this;
    }

    public SetWalkTargetToEntity<E> closeEnoughDist(BiFunction<E, LivingEntity, Integer> closeEnoughMod) {
        this.closeEnoughWhen = closeEnoughMod;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = entitySelector.apply(entity);
        if (this.target == null) return false;
        if (this.target.isDeadOrDying()) return false;
        return super.checkExtraStartConditions(level, entity);
    }

    @Override
    protected void start(E entity) {
        Brain<?> brain = entity.getBrain();
        BrainUtils.setMemory(brain, MemoryModuleType.LOOK_TARGET, new EntityTracker(this.target, true));
        BrainUtils.setMemory(brain, MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(this.target, false), this.speedMod.apply(entity, this.target), this.closeEnoughWhen.apply(entity, this.target)));
    }
}
