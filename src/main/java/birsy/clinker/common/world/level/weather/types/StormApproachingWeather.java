package birsy.clinker.common.world.level.weather.types;

import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.registry.ClinkerOthershoreWeatherTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.SimpleWeightedRandomList;

public class StormApproachingWeather extends OthershoreWeather {
    public static final int APPROACH_TIME = 60 * 20; // one minute
    public static final MapCodec<OthershoreWeather> CODEC =
            MapCodec.unit(StormApproachingWeather::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, OthershoreWeather> STREAM_CODEC =
            StreamCodec.of((buf, weather) -> {}, buf -> new StormApproachingWeather());

    public StormApproachingWeather() { super(); }

    @Override
    public void tick(OthershoreWeatherSystem system) {
        if (system.isClientSide) return;
        if (system.getWeatherTicks() > APPROACH_TIME) system.setWeather(ClinkerOthershoreWeatherTypes.STORM.get());
    }

    @Override public Type type() { return ClinkerOthershoreWeatherTypes.STORM_APPROACHING.get(); }

    public static float getStormApproachProgress(OthershoreWeatherSystem system, float partialTicks) {
        return (system.getWeatherTicks() + partialTicks) / (APPROACH_TIME + 1.0F);
    }
}
