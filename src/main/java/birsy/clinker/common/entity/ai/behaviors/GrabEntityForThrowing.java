package birsy.clinker.common.entity.ai.behaviors;

import birsy.clinker.common.entity.module.ModuleHolder;
import birsy.clinker.common.entity.module.modules.EntityTosser;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Function;

public class GrabEntityForThrowing<E extends LivingEntity & ModuleHolder<E> & EntityTosser<E>> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .hasMemory(ClinkerMemoryModules.GRAB_TARGET.get());
    protected Function<E, Double> closeEnoughDist = (e) -> e.getBbWidth() * 2.0;

    public GrabEntityForThrowing<E> closeEnoughDist(Function<E, Double> func) {
        this.closeEnoughDist = func;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Entity target = BrainUtils.getMemory(entity, ClinkerMemoryModules.GRAB_TARGET.get());
        double dist = entity.distanceTo(target);
        double threshold = closeEnoughDist.apply(entity);
        return dist < threshold;
    }

    @Override
    protected void start(E entity) {
        Entity target = BrainUtils.getMemory(entity, ClinkerMemoryModules.GRAB_TARGET.get());
        entity.tossModule().grab(target);
        BrainUtils.clearMemory(entity, ClinkerMemoryModules.GRAB_TARGET.get());
    }
}
