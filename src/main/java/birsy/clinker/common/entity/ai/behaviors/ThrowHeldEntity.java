package birsy.clinker.common.entity.ai.behaviors;

import birsy.clinker.common.entity.module.ModuleHolder;
import birsy.clinker.common.entity.module.modules.EntityTosser;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class ThrowHeldEntity<E extends LivingEntity & ModuleHolder<E> & EntityTosser<E>> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .hasMemory(ClinkerMemoryModules.THROW_TARGET.get());

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return entity.tossModule().isHoldingEntity();
    }

    @Override
    protected void start(E entity) {
        Entity heldEntity = entity.tossModule().getHeldEntity();

        Vec3 startPos = heldEntity.position();
        Vec3 targetPos = BrainUtils.getMemory(entity, ClinkerMemoryModules.THROW_TARGET.get()).currentPosition();
        Vec3 delta = targetPos.subtract(startPos);

        // they're bad at throwing and that's ok.
        double hLength = Mth.length(delta.x(), delta.z());
        double hSpeed = 0.1, vy = hLength * 0.05 + delta.y() * 0.1;
        entity.tossModule().toss(new Vec3(delta.x() * hSpeed, vy, delta.z() * hSpeed));
    }
}
