package birsy.clinker.common.world.entity.gnomad.ai.behaviors;

import birsy.clinker.common.world.entity.ai.LookTarget;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.function.Function;

public abstract class GatherSupplies {} /*<E extends GnomadEntity> extends DelayedBehaviour<E> {
    protected Function<E, Integer> supplyRadius = (owner) -> 2;
    protected float gatheringRadius;

    public GatherSupplies() {
        super(20);
    }

    public GatherSupplies<E> supplyRadius(Function<E, Integer> supplyRadius) {
        this.supplyRadius = supplyRadius;
        return this;
    }

    protected boolean canGather(E entity) {
        Vec3 depotLocation = BrainUtils.getMemory(entity, ClinkerMemoryModules.TARGETED_SUPPLY_DEPOT.get()).getDepotLocation();

        if (entity.distanceToSqr(depotLocation) > this.gatheringRadius * this.gatheringRadius) return false;
        if (entity.level().clip(new ClipContext(entity.getEyePosition(), depotLocation, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.BLOCK) return false;

        return true;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        if (entity.isCarryingSuppliesForDelivery()) return false;
        this.gatheringRadius = this.supplyRadius.apply(entity);
        if (!this.canGather(entity)) return false;
        return this.checkExtraStartConditions(level, entity);
    }

    @Override
    protected void start(E entity) {
        super.start(entity);

        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new LookTarget<E>(entity).target((e) -> BrainUtils.getMemory(e, ClinkerMemoryModules.NEARBY_SUPPLY_DEPOTS.get()).get(0).getDepotLocation()));
    }

    public abstract void gatherSupplies(E entity);

    @Override
    protected void doDelayedAction(E entity) {
        this.gatherSupplies(entity);
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        if (!canGather(entity)) return false;
        if (entity.isCarryingSuppliesForDelivery()) return false;
        return true;
    }
} */
