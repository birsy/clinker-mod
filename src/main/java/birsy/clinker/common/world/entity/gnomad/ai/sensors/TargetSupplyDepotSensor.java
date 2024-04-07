package birsy.clinker.common.world.entity.gnomad.ai.sensors;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.GnomadSupplyDepot;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.level.pathfinder.Path;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

public class TargetSupplyDepotSensor<E extends GnomadEntity> extends NearbyLivingEntitySensor<E> {
    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(ClinkerMemoryModules.NEARBY_SUPPLY_DEPOTS.get(), ClinkerMemoryModules.TARGETED_SUPPLY_DEPOT.get());

    public TargetSupplyDepotSensor() {
        setScanRate(E -> 200);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.TARGET_SUPPLY_DEPOT_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        List<GnomadSupplyDepot> depots = BrainUtils.getMemory(entity, ClinkerMemoryModules.NEARBY_SUPPLY_DEPOTS.get());
        for (GnomadSupplyDepot depot : depots) {
            // try to create a path to the depots
            // if one succeeds, set that to be the target
            // if they all fail... there's no valid paths nearby
            Path path = entity.getNavigation().createPath(BlockPos.containing(depot.getDepotLocation()), 0);
            if (path.canReach()) {
                BrainUtils.setMemory(entity, ClinkerMemoryModules.TARGETED_SUPPLY_DEPOT.get(), depot);
                return;
            }
        }

        BrainUtils.clearMemory(entity, ClinkerMemoryModules.TARGETED_SUPPLY_DEPOT.get());
    }
}
