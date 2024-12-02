package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record GnomadSquadRemovalDebugPacket(UUID uuid) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GnomadSquadRemovalDebugPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("debug/gnomadsquadremove"));
    public static final StreamCodec<ByteBuf, GnomadSquadRemovalDebugPacket> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.UUID,
            GnomadSquadRemovalDebugPacket::uuid,
            GnomadSquadRemovalDebugPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClinkerDebugRenderers.gnomadSquadDebugRenderer.removeSquad(this.uuid);
    }
}
