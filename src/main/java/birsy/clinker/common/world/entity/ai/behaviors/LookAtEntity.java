package birsy.clinker.common.world.entity.ai.behaviors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class LookAtEntity<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT),
            Pair.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT)
    );
    private Function<LivingEntity, Integer> entityToPriority = (entity -> entity instanceof Player ? 1 : -1);
    private Function<E, Integer> expirationTime = (entity -> 50);
    protected Entity target = null;

    public LookAtEntity<E> entityToPriority(Function<LivingEntity, Integer> function) {
        this.entityToPriority = function;
        return this;
    }

    public LookAtEntity<E> expirationTime(Function<E, Integer> expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public LookAtEntity<E> expirationTime(int expirationTime) {
        return this.expirationTime((entity) -> expirationTime);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E me) {
        NearestVisibleLivingEntities nearestVisibleLivingEntities = BrainUtils.getMemory(me, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        List<LivingEntity> potentialTargets = new ArrayList<>(nearestVisibleLivingEntities.find(entity -> entityToPriority.apply(entity) > 0).toList());
        if (potentialTargets.isEmpty()) return false;

        potentialTargets.sort(Comparator.comparingInt(entity -> -entityToPriority.apply(entity)));
        this.target = potentialTargets.get(0);
        return true;
    }

    @Override
    protected void start(E entity) {
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(this.target, true), expirationTime.apply(entity));
    }

    @Override
    protected void stop(E entity) {
        this.target = null;
    }


}
