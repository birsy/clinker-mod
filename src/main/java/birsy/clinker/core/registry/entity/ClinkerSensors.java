package birsy.clinker.core.registry.entity;

import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.ActiveSquadTasksSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.SquadSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.NearestSupplyDepotSensor;
import birsy.clinker.common.world.entity.gnomad.gnomind.sensors.PostedSquadTasksSensor;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerSensors {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(BuiltInRegistries.SENSOR_TYPE, Clinker.MOD_ID);

    public static final Supplier<SensorType<PostedSquadTasksSensor<?>>> POSTED_SQUAD_TASKS =
            SENSOR_TYPES.register("posted_squad_tasks", () -> new SensorType<>(PostedSquadTasksSensor::new));
    public static final Supplier<SensorType<ActiveSquadTasksSensor<?>>> ACTIVE_SQUAD_TASK =
            SENSOR_TYPES.register("active_squad_task", () -> new SensorType<>(ActiveSquadTasksSensor::new));

    public static final Supplier<SensorType<SquadSensor<?>>> GNOMAD_SQUAD =
            SENSOR_TYPES.register("gnomad_squad", () -> new SensorType<>(SquadSensor::new));
    public static final Supplier<SensorType<NearestSupplyDepotSensor<?>>> NEAREST_SUPPLY_DEPOT =
            SENSOR_TYPES.register("nearest_supply_depot", () -> new SensorType<>(NearestSupplyDepotSensor::new));
}
