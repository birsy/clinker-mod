package birsy.clinker.common.world.entity.gnomad.gnomind.sensors;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerPOIs;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.registry.entity.ClinkerSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.Comparator;
import java.util.List;

public class NearestSupplyDepotSensor<E extends PathfinderMob> extends ExtendedSensor<E> {
    private static final int SCAN_INTERVAL = 20 * 10; // ten seconds
    private static final List<MemoryModuleType<?>> MEMORIES =
            ObjectArrayList.of(ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get());

    public NearestSupplyDepotSensor() {
        this.setScanRate(entity -> SCAN_INTERVAL);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ClinkerSensors.NEAREST_SUPPLY_DEPOT.get();
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        BlockPos entityBlockPos = entity.blockPosition();
        level.getPoiManager()
                .findClosest(
                        typeHolder -> typeHolder.is(PoiTypes.FISHERMAN),
                        pos -> true,
                        entityBlockPos,
                        48,
                        PoiManager.Occupancy.ANY)
                .ifPresent(pos ->
                        BrainUtils.setMemory(
                                entity,
                                ClinkerMemoryModules.NEAREST_SUPPLY_DEPOT.get(),
                                GlobalPos.of(level.dimension(), pos)
                        )
                );
    }
}
