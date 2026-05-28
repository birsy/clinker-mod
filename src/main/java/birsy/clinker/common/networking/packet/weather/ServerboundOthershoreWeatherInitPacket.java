package birsy.clinker.common.networking.packet.weather;

import birsy.clinker.common.world.level.weather.ClientOthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.world.level.weather.OthershoreWeatherSystem;
import birsy.clinker.common.world.level.weather.ServerOthershoreWeatherSystem;
import birsy.clinker.core.Clinker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundOthershoreWeatherInitPacket() implements CustomPacketPayload {
    public static final Type<ServerboundOthershoreWeatherInitPacket> TYPE = new Type<>(Clinker.resource("server/weather/init"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundOthershoreWeatherInitPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundOthershoreWeatherInitPacket());

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {
                ServerOthershoreWeatherSystem system = ServerOthershoreWeatherSystem.getServerSystem(level);
                if (system == null) return;
                system.distributeInitPacket(player);
            }
        });
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
