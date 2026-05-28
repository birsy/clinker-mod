package birsy.clinker.common.world.level.weather.types;

import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.registry.ClinkerOthershoreWeatherTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.SimpleWeightedRandomList;

public class StormWeather extends OthershoreWeather {
    public static final MapCodec<StormWeather> CODEC =
            MapCodec.unit(StormWeather::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, StormWeather> STREAM_CODEC =
            StreamCodec.of(
                    (buf, weather) -> buf.writeInt(weather.lifetime),
                    buf -> {
                        StormWeather instance = new StormWeather();
                        instance.lifetime = buf.readInt();
                        return instance;
                    }
            );
    int lifetime = 0;

    public StormWeather() {
        super();
    }

    public StormWeather(OthershoreWeatherSystem weatherSystem) {
        super();
        this.lifetime = weatherSystem.random.nextIntBetweenInclusive(4 * 60 * 20, 6 * 60 * 20);
    }

    @Override
    public void tick(OthershoreWeatherSystem system) {
        if (system.isClientSide) return;
        // storms last 5 minutes!
        if (system.getWeatherTicks() < lifetime) return;
        system.setWeather(ClinkerOthershoreWeatherTypes.NORMAL.get());
    }

    @Override public Type type() { return ClinkerOthershoreWeatherTypes.STORM.get(); }

    public float getStormProgress(OthershoreWeatherSystem weatherSystem, float partialTicks) {
        return (weatherSystem.getWeatherTicks() + partialTicks) / (lifetime + 1.0F);
    }
}
