package birsy.clinker.common.entity.ai.sensor;

import birsy.clinker.core.registry.ClinkerTags;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiPredicate;

public class PathObstructionSensor<E extends PathfinderMob> extends ExtendedSensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES =
            ObjectArrayList.of(ClinkerMemoryModules.PATH_OBSTRUCTED.get());
    private BiPredicate<E, BlockState> validObstruction = (e, state) -> true;

    public PathObstructionSensor<E> validObstructions(BiPredicate<E, BlockState> predicate) {
        this.validObstruction = predicate;
        return this;
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() { return MEMORIES; }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() { return ClinkerSensors.PATH_OBSTRUCTION.get(); }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        super.doTick(level, entity);

        PathNavigation navigation = entity.getNavigation();
        Path currentPath = navigation.getPath();
        if (currentPath == null || currentPath.isDone()) {
            BrainUtils.clearMemory(entity, ClinkerMemoryModules.PATH_OBSTRUCTED.get());
            return;
        }

        Node nextNode = currentPath.getNextNode();

        Vec3 nodePos = nextNode.asVec3(),
             entityPos = entity.position();
        Vec3 direction = nodePos.subtract(entityPos)
                .normalize().scale(2);
        AABB aabb = entity.getBoundingBox().expandTowards(direction).inflate(0.5F, 0, 0.5F);;
        aabb = aabb.setMinY(aabb.minY + entity.maxUpStep()); // account for step up height not blocking movement
        boolean obstructed = BlockPos.betweenClosedStream(aabb)
                .anyMatch(pos -> validObstruction.test(entity, entity.level().getBlockState(pos)));
        if (obstructed) BrainUtils.setForgettableMemory(entity, ClinkerMemoryModules.PATH_OBSTRUCTED.get(), Unit.INSTANCE, 40);
        else BrainUtils.clearMemory(entity, ClinkerMemoryModules.PATH_OBSTRUCTED.get());
    }
}
