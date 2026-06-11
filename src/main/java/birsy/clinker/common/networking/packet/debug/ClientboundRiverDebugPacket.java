package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.client.render.debug.RiverDebugRenderer;
import birsy.clinker.core.Clinker;
import net.minecraft.SharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ClientboundRiverDebugPacket(List<RiverDebugRenderer.RiverDebugPoint> dump) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundRiverDebugPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("debug/river"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundRiverDebugPacket> STREAM_CODEC =
            RiverDebugRenderer.RiverDebugPoint.STREAM_CODEC.apply(ByteBufCodecs.list())
                    .map(ClientboundRiverDebugPacket::new, ClientboundRiverDebugPacket::dump);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        if (!SharedConstants.IS_RUNNING_IN_IDE) return;
        context.enqueueWork(() -> ClinkerDebugRenderers.riverDebugRenderer.handlePacket(dump));
    }
}
