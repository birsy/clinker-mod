package birsy.clinker.core.registry.entity;

import birsy.clinker.common.entity.gnomad.gnomind.sensors.LastKnownEnemyPositionSensor;
import birsy.clinker.common.entity.gnomad.gnomind.sensors.NearbyCoverSensor;
import birsy.clinker.common.entity.gnomad.gnomind.sensors.NearestSupplyDepotSensor;
import birsy.clinker.common.entity.gnomad.gnomind.sensors.SquadSensor;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerSensors {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(BuiltInRegistries.SENSOR_TYPE, Clinker.MOD_ID);

    public static final Supplier<SensorType<SquadSensor<?>>> GNOMAD_SQUAD =
            SENSOR_TYPES.register("gnomad_squad", () -> new SensorType<>(SquadSensor::new));
    public static final Supplier<SensorType<NearestSupplyDepotSensor<?>>> NEAREST_SUPPLY_DEPOT =
            SENSOR_TYPES.register("nearest_supply_depot", () -> new SensorType<>(NearestSupplyDepotSensor::new));
    public static final Supplier<SensorType<NearbyCoverSensor<?>>> NEARBY_COVER =
            SENSOR_TYPES.register("nearby_cover", () -> new SensorType<>(NearbyCoverSensor::new));
    public static final Supplier<SensorType<LastKnownEnemyPositionSensor<?>>> LAST_KNOWN_ENEMY_POSITIONS =
            SENSOR_TYPES.register("last_known_enemy_positions", () -> new SensorType<>(LastKnownEnemyPositionSensor::new));
}
