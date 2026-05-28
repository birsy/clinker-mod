package birsy.clinker.common.networking.packet.weather;

import birsy.clinker.common.world.level.weather.ClientOthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.Clinker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundOthershoreWeatherInitPacket(OthershoreWeather weather, int weatherTickCount) implements CustomPacketPayload {
    public static final Type<ClientboundOthershoreWeatherInitPacket> TYPE = new Type<>(Clinker.resource("client/weather/init"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOthershoreWeatherInitPacket> STREAM_CODEC = StreamCodec.composite(
            OthershoreWeather.STREAM_CODEC, ClientboundOthershoreWeatherInitPacket::weather,
            ByteBufCodecs.INT, ClientboundOthershoreWeatherInitPacket::weatherTickCount,
            ClientboundOthershoreWeatherInitPacket::new
    );


    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> ClientOthershoreWeatherSystem.updateFromInitPacket(this));
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
