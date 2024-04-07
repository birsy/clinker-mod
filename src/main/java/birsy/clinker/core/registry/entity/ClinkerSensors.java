package birsy.clinker.core.registry.entity;

import birsy.clinker.common.world.entity.gnomad.ai.sensors.GnomadSquadSensor;
import birsy.clinker.common.world.entity.gnomad.ai.sensors.TargetSupplyDepotSensor;
import birsy.clinker.common.world.entity.gnomad.ai.sensors.SupplyDepotSensor;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerSensors {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(BuiltInRegistries.SENSOR_TYPE, Clinker.MOD_ID);

    public static final Supplier<SensorType<GnomadSquadSensor<?>>> GNOMAD_SQUAD_SENSOR = SENSOR_TYPES.register("gnomad_squad_sensor", () -> new SensorType<>(GnomadSquadSensor::new));
    public static final Supplier<SensorType<SupplyDepotSensor<?>>> SUPPLY_DEPOT_SENSOR = SENSOR_TYPES.register("supply_depot_sensor", () -> new SensorType<>(SupplyDepotSensor::new));
    public static final Supplier<SensorType<TargetSupplyDepotSensor<?>>> TARGET_SUPPLY_DEPOT_SENSOR = SENSOR_TYPES.register("target_supply_depot_sensor", () -> new SensorType<>(TargetSupplyDepotSensor::new));
}
