package birsy.clinker.common.world.level.weather;

import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public abstract class OthershoreWeather {
    public static final Codec<OthershoreWeather> CODEC =
            ClinkerRegistries.OTHERSHORE_WEATHER_TYPE_REGISTRY.byNameCodec()
                    .dispatch(OthershoreWeather::type, OthershoreWeather.Type::codec);
    public static final StreamCodec<RegistryFriendlyByteBuf, OthershoreWeather> STREAM_CODEC =
            ByteBufCodecs.registry(ClinkerRegistries.OTHERSHORE_WEATHER_TYPE_REGISTRY_KEY)
                    .dispatch(OthershoreWeather::type, OthershoreWeather.Type::streamCodec);

    public OthershoreWeather() {}

    public abstract OthershoreWeather.Type type();

    // this is run when the weather event begins, but NOT when it is synced to a newly joining client!
    // use the constructor for that :P
    public void begin(OthershoreWeatherSystem system) {}
    public void tick(OthershoreWeatherSystem system) {}
    public void end(OthershoreWeatherSystem system) {}

    // sync packets are sent out every 5 ticks!
    public boolean hasSyncPacket() { return false; }
    public void uploadToSyncPacket(RegistryFriendlyByteBuf buffer) {}
    public void updateFromSyncPacket(RegistryFriendlyByteBuf buffer) {}

    public record Type(
            Function<OthershoreWeatherSystem, OthershoreWeather> factory,
            MapCodec<OthershoreWeather> codec,
            StreamCodec<RegistryFriendlyByteBuf, OthershoreWeather> streamCodec) {
        public static final Codec<OthershoreWeather.Type> CODEC =
                ClinkerRegistries.OTHERSHORE_WEATHER_TYPE_REGISTRY.byNameCodec();
        public static final StreamCodec<RegistryFriendlyByteBuf, OthershoreWeather.Type> STREAM_CODEC =
                ByteBufCodecs.registry(ClinkerRegistries.OTHERSHORE_WEATHER_TYPE_REGISTRY_KEY);
    }
}
