package birsy.clinker.common.world.entity.ai.behaviors;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Supplier;

public class AttackOwnerAttackers<E extends Mob & OwnableEntity> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS =
            MemoryTest.builder(1).noMemory(MemoryModuleType.ATTACK_TARGET);

    protected Supplier<Integer> expirationTime = () -> 200;

    protected LivingEntity toTarget = null;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;

        LivingEntity potentialTarget = owner.getLastAttacker();
        if (potentialTarget == null) return false;

        if (entity.isAlliedTo(potentialTarget)) return false;

        int timestamp = owner.getLastHurtByMobTimestamp();
        return timestamp < expirationTime.get() && timestamp > potentialTarget.tickCount;
    }

    protected void start(E entity) {
        BrainUtils.setTargetOfEntity(entity, this.toTarget);
        BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

        this.toTarget = null;
    }

    public AttackOwnerAttackers<E> expirationTime(Supplier<Integer> expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }
}
