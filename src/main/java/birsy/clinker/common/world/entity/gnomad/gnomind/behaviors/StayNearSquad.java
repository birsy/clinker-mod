package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class StayNearSquad<E extends GnomadEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
    );
    protected SquareRadius radius = new SquareRadius(24, 16);

    public StayNearSquad<E> radius(double xz, double y) {
        this.radius = new SquareRadius(xz, y);
        return this;
    }

    protected boolean withinDistance(E entity, float distOffset) {
        Vec3 center = entity.squad.getCenter();
        Vec3 position = entity.position();
        double horizontalDistance = Math.sqrt((center.x - position.x)*(center.x - position.x) + (center.z - position.z)*(center.z - position.z));
        double verticalDistance = Math.abs(center.y - position.y);
        return horizontalDistance < (radius.xzRadius() + distOffset) && (verticalDistance < radius.yRadius() + distOffset);
    }

    protected void setWalkTarget(E entity) {
        Vec3 center = entity.squad.getCenter();
        Vec3 pos = LandRandomPos.getPosTowards(entity, 5, 5, center);
        if (pos == null) return;
        BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.5F, (int) Math.min(radius.xzRadius(), radius.yRadius())));
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return !withinDistance(entity, 10);
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return !withinDistance(entity, 0);
    }

    @Override
    protected void start(E entity) {
        setWalkTarget(entity);
    }

    @Override
    protected void tick(E entity) {
        if (entity.getNavigation().isDone()) setWalkTarget(entity);
    }

    @Override
    protected void stop(E entity) {
        //BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
    }
}
