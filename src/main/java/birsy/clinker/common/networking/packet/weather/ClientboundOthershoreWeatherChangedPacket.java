package birsy.clinker.common.networking.packet.weather;

import birsy.clinker.common.world.level.weather.ClientOthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.core.Clinker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundOthershoreWeatherChangedPacket(OthershoreWeather weather) implements CustomPacketPayload {
    public static final Type<ClientboundOthershoreWeatherChangedPacket> TYPE = new Type<>(Clinker.resource("client/weather/changed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOthershoreWeatherChangedPacket> STREAM_CODEC = StreamCodec.composite(
            OthershoreWeather.STREAM_CODEC, ClientboundOthershoreWeatherChangedPacket::weather,
            ClientboundOthershoreWeatherChangedPacket::new
    );


    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> ClientOthershoreWeatherSystem.updateFromChangedPacket(this));
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
