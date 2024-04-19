package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

public class DeliverSupplies {} /*<E extends GnomadEntity & GnomadSupplyDeliverer> extends DelayedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(ClinkerMemoryModules.SUPPLY_TARGET.get(), MemoryStatus.VALUE_PRESENT),
            Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED),
            Pair.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
    protected Function<E, Integer> deliveryRadius = (owner) -> 2;
    protected int deliveringRadius = 0;

    public DeliverSupplies() {
        super(20);
    }

    public DeliverSupplies<E> deliveryRadius(Function<E, Integer> deliveryRadius) {
        this.deliveryRadius = deliveryRadius;
        return this;
    }

    @Override
    public List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    protected boolean canDeliver(E entity) {
        GnomadEntity supplyDeliveryTarget = BrainUtils.getMemory(entity, ClinkerMemoryModules.SUPPLY_TARGET.get());

        if (supplyDeliveryTarget == null || supplyDeliveryTarget.isDeadOrDying()) return false;
        if (entity.distanceToSqr(supplyDeliveryTarget) > this.deliveringRadius * this.deliveringRadius) return false;
        if (entity.level().clip(new ClipContext(entity.getEyePosition(), supplyDeliveryTarget.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.BLOCK) return false;
        return true;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        if (!entity.isCarryingSuppliesForDelivery()) return false;
        if (!this.canDeliver(entity)) return false;
        return this.checkExtraStartConditions(level, entity);
    }

    @Override
    protected void start(E entity) {
        super.start(entity);
        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(BrainUtils.getMemory(entity, ClinkerMemoryModules.SUPPLY_TARGET.get()), true));
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        if (!canDeliver(entity)) return false;
        if (!entity.isCarryingSuppliesForDelivery()) return false;
        return true;
    }

    @Override
    protected void doDelayedAction(E entity) {
        super.doDelayedAction(entity);
        entity.setCarryingSuppliesForDelivery(false);
        BrainUtils.getMemory(entity, ClinkerMemoryModules.SUPPLY_TARGET.get()).resupply();
    }

    @Override
    protected void stop(E entity) {
        super.stop(entity);
    }
}*/
