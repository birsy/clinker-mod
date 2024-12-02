package birsy.clinker.common.networking.packet;

import birsy.clinker.core.Clinker;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundPushPacket(double x, double y, double z) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundPushPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/push"));
    public static final StreamCodec<ByteBuf, ClientboundPushPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            ClientboundPushPacket::x,
            ByteBufCodecs.DOUBLE,
            ClientboundPushPacket::y,
            ByteBufCodecs.DOUBLE,
            ClientboundPushPacket::z,
            ClientboundPushPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.player().push(this.x, this.y, this.z);
    }
}