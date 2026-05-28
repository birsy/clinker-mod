package birsy.clinker.core.registry;

import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.world.level.weather.types.NormalWeather;
import birsy.clinker.common.world.level.weather.types.StormApproachingWeather;
import birsy.clinker.common.world.level.weather.types.StormWeather;
import birsy.clinker.core.Clinker;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerOthershoreWeatherTypes {
    public static final DeferredRegister<OthershoreWeather.Type<?>> OTHERSHORE_WEATHER_TYPES =
            DeferredRegister.create(ClinkerRegistries.OTHERSHORE_WEATHER_TYPE_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<OthershoreWeather.Type<?>> NORMAL =
            OTHERSHORE_WEATHER_TYPES.register("normal", () -> new OthershoreWeather.Type<>(
                    (system) -> new NormalWeather(),
                    NormalWeather.CODEC,
                    NormalWeather.STREAM_CODEC
            ));

    public static final Supplier<OthershoreWeather.Type<?>> STORM_APPROACHING =
            OTHERSHORE_WEATHER_TYPES.register("storm_approaching", () -> new OthershoreWeather.Type<>(
                    (system) -> new StormApproachingWeather(),
                    StormApproachingWeather.CODEC,
                    StormApproachingWeather.STREAM_CODEC
            ));

    public static final Supplier<OthershoreWeather.Type<?>> STORM =
            OTHERSHORE_WEATHER_TYPES.register("storm", () -> new OthershoreWeather.Type<>(
                    StormWeather::new,
                    StormWeather.CODEC,
                    StormWeather.STREAM_CODEC
            ));
}
