package birsy.clinker.common.world.level.weather.types;

import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.registry.ClinkerOthershoreWeatherTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedRandomList;

import java.util.function.Supplier;

public class NormalWeather extends OthershoreWeather {
    public static final MapCodec<NormalWeather> CODEC =
            MapCodec.unit(NormalWeather::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, NormalWeather> STREAM_CODEC =
            StreamCodec.of((buf, weather) -> {}, buf -> new NormalWeather());

    private static final int MINIMUM_TICKS_BETWEEN_STORMS = 10 * 60 * 20; // 10 minutes
    private static final SimpleWeightedRandomList<Supplier<Type<?>>> NEXT_TYPE_CHANCES = SimpleWeightedRandomList.<Supplier<Type<?>>>builder()
            .add(ClinkerOthershoreWeatherTypes.NORMAL, 5 * 60 * 20)
            .add(ClinkerOthershoreWeatherTypes.STORM_APPROACHING, 1)
            .build();

    public NormalWeather() {
        super();
    }

    @Override
    public void tick(OthershoreWeatherSystem system) {
        if (system.isClientSide) return;
        // wait some time before we try to transition
        if (system.getWeatherTicks() < MINIMUM_TICKS_BETWEEN_STORMS) return;

        Type nextType = NEXT_TYPE_CHANCES.getRandomValue(system.random).orElse(this::type).get();
        if (nextType != this.type()) system.setWeather(nextType);
    }

    @Override public Type type() { return ClinkerOthershoreWeatherTypes.NORMAL.get(); }
}
