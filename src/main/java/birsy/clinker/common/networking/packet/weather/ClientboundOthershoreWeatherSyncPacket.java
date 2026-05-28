package birsy.clinker.common.networking.packet.weather;

import birsy.clinker.common.world.level.weather.ClientOthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.core.Clinker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundOthershoreWeatherSyncPacket(OthershoreWeather.Type weatherType, byte[] data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundOthershoreWeatherSyncPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/weather/sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOthershoreWeatherSyncPacket> STREAM_CODEC = StreamCodec.composite(
            OthershoreWeather.Type.STREAM_CODEC, ClientboundOthershoreWeatherSyncPacket::weatherType,
            ByteBufCodecs.byteArray(Float.BYTES * 64), ClientboundOthershoreWeatherSyncPacket::data,
            ClientboundOthershoreWeatherSyncPacket::new
    );

    public ClientboundOthershoreWeatherSyncPacket(OthershoreWeather.Type weatherType, ByteBuf buf) {
        this(weatherType, ByteBufUtil.getBytes(buf));
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> ClientOthershoreWeatherSystem.updateFromSyncPacket(this));
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
