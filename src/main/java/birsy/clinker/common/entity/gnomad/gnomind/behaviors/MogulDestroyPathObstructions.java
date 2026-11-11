package birsy.clinker.common.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.entity.gnomad.mogul.GnomadMogulEntity;
import birsy.clinker.common.entity.gnomad.mogul.MogulAttackHandler;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerTags;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class MogulDestroyPathObstructions<E extends GnomadMogulEntity> extends DelayedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1)
            .hasMemory(ClinkerMemoryModules.PATH_OBSTRUCTED.get());
    private Vec3 direction;

    public MogulDestroyPathObstructions() {
        // terrible terrible hack
        super((MogulAttackHandler.SWING_LEFT.hitboxTick + MogulAttackHandler.SWING_RIGHT.hitboxTick) / 2);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        Path path = entity.getNavigation().getPath();
        return EventHooks.canEntityGrief(level, entity) &&
                path != null && !path.isDone() &&
                entity.attackHandler.isAttacking();
    }

    @Override
    protected void start(E entity) {
        super.start(entity);

        PathNavigation navigation = entity.getNavigation();
        Path currentPath = navigation.getPath();
        Node nextNode = currentPath.getNextNode();

        Vec3 nodePos = nextNode.asVec3(),
             entityPos = entity.position();
        this.direction = nodePos.subtract(entityPos)
                .normalize().scale(1.5);

        entity.attackHandler.beginAttack(entity.getRandom().nextBoolean() ? MogulAttackHandler.SWING_LEFT : MogulAttackHandler.SWING_RIGHT);
    }

    @Override
    protected void tick(E entity) {
        super.tick(entity);
        // look at what we're hitting
    }

    @Override
    protected void doDelayedAction(E entity) {
        PathNavigation navigation = entity.getNavigation();
        Path currentPath = navigation.getPath();
        if (currentPath == null) return;

        AABB aabb = entity.getBoundingBox().expandTowards(direction).inflate(0.5F, 0, 0.5F);
        BlockPos.betweenClosedStream(aabb).forEach(pos -> {
            BlockState state = entity.level().getBlockState(pos);
            if (state.is(ClinkerTags.Blocks.MOGUL_DESTRUCTIBLE))
                entity.level().destroyBlock(pos, entity.getRandom().nextInt(5) == 0, entity);
        });

    }

    @Override
    protected void stop(E entity) {
        BrainUtils.clearMemory(entity, ClinkerMemoryModules.PATH_OBSTRUCTED.get());
        super.stop(entity);
    }
}
