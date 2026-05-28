package birsy.clinker.common.world.level.weather.types;

import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.registry.ClinkerOthershoreWeatherTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.SimpleWeightedRandomList;

public class StormWeather extends OthershoreWeather {
    public static final MapCodec<OthershoreWeather> CODEC =
            MapCodec.unit(StormWeather::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, OthershoreWeather> STREAM_CODEC =
            StreamCodec.of((buf, weather) -> {}, buf -> new StormWeather());

    public StormWeather() {
        super();
    }

    @Override
    public void tick(OthershoreWeatherSystem system) {
        if (system.isClientSide) return;
        // storms last 5 minutes!
        if (system.getWeatherTicks() < 5 * 60 * 20) return;
        system.setWeather(ClinkerOthershoreWeatherTypes.NORMAL.get());
    }

    @Override public Type type() { return ClinkerOthershoreWeatherTypes.STORM.get(); }
}
