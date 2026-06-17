package birsy.clinker.common.world.entity.ai.behaviors;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class SetLookTargetToRememberedPos<E extends LivingEntity> extends ExtendedBehaviour<E> {
    final MemoryModuleType<GlobalPos> posMemory;
    public static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .usesMemory(MemoryModuleType.LOOK_TARGET);

    public SetLookTargetToRememberedPos(MemoryModuleType<GlobalPos> posMemory) {
        this.posMemory = posMemory;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        if (!BrainUtils.hasMemory(entity, posMemory)) return false;
        GlobalPos pos = BrainUtils.getMemory(entity, posMemory);
        return pos != null && pos.dimension() == level.dimension();
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        GlobalPos pos = BrainUtils.getMemory(entity, posMemory);
        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET,
                new BlockPosTracker(pos.pos())
        );
    }
}
