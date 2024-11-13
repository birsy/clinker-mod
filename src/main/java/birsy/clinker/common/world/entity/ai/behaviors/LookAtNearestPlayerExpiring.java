package birsy.clinker.common.world.entity.ai.behaviors;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class LookAtNearestPlayerExpiring<E extends LivingEntity> extends SetPlayerLookTarget<E> {
    protected Function<E, Integer> expirationTime = (me) -> 80;

    public LookAtNearestPlayerExpiring<E> expirationTime(Function<E, Integer> expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    @Override
    protected void start(E entity) {
        BrainUtils.setForgettableMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(this.target, true), expirationTime.apply(entity));
    }
}
