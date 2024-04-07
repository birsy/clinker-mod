package birsy.clinker.common.world.entity.gnomad.ai.behaviors;

import birsy.clinker.common.world.entity.ai.SetWalkTarget;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.GnomadSupplyDepot;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.pathfinder.Path;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.Function;

public class WalkToNearbySupplyDepot<E extends GnomadEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED),
            Pair.of(ClinkerMemoryModules.SUPPLY_TARGET.get(), MemoryStatus.VALUE_PRESENT),
            Pair.of(ClinkerMemoryModules.NEARBY_SUPPLY_DEPOTS.get(), MemoryStatus.VALUE_PRESENT),
            Pair.of(ClinkerMemoryModules.TARGETED_SUPPLY_DEPOT.get(), MemoryStatus.REGISTERED));
    protected Function<E, Float> speedMod = (owner) -> 1f;
    protected Function<E, Integer> closeEnoughWhen = (owner) -> 1;

    public WalkToNearbySupplyDepot<E> speedMod(Function<E, Float> speedModifier) {
        this.speedMod = speedModifier;
        return this;
    }

    public WalkToNearbySupplyDepot<E> closeEnoughWhen(Function<E, Integer> closeEnoughWhen) {
        this.closeEnoughWhen = closeEnoughWhen;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    protected boolean setTargetedSupplyDepot(E entity) {
        List<GnomadSupplyDepot> depots = BrainUtils.getMemory(entity, ClinkerMemoryModules.NEARBY_SUPPLY_DEPOTS.get());
        for (GnomadSupplyDepot depot : depots) {
            // try to create a path to the depots
            // if one succeeds, set that to be the target
            // if they all fail... fuck. return false.
            Path path = entity.getNavigation().createPath(BlockPos.containing(depot.getDepotLocation()), 0);
            if (path.canReach()) {
                BrainUtils.setMemory(entity, ClinkerMemoryModules.TARGETED_SUPPLY_DEPOT.get(), depot);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        boolean hasTarget = true;
        if (!BrainUtils.hasMemory(entity, ClinkerMemoryModules.TARGETED_SUPPLY_DEPOT.get())) hasTarget = this.setTargetedSupplyDepot(entity);
        if (hasTarget) BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(BrainUtils.getMemory(entity, ClinkerMemoryModules.TARGETED_SUPPLY_DEPOT.get()).getDepotLocation(), this.speedMod.apply(entity), this.closeEnoughWhen.apply(entity)));
    }
}
