package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.common.entity.system.squad.Squad;
import birsy.clinker.common.entity.system.squad.SquadDebugDataDump;
import birsy.clinker.core.Clinker;
import net.minecraft.SharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Collection;
import java.util.List;

public record ClientboundSquadDebugPacket(List<SquadDebugDataDump> dump) implements CustomPacketPayload {
    public static final Type<ClientboundSquadDebugPacket> TYPE = new Type<>(Clinker.resource("debug/squad"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundSquadDebugPacket> STREAM_CODEC =
            SquadDebugDataDump.STREAM_CODEC.apply(ByteBufCodecs.list())
                    .map(ClientboundSquadDebugPacket::new, ClientboundSquadDebugPacket::dump);

    @Override
    public Type<ClientboundSquadDebugPacket> type() { return TYPE; }

    public static ClientboundSquadDebugPacket of(Collection<Squad> squads) {
        return new ClientboundSquadDebugPacket(squads.stream().map(SquadDebugDataDump::of).toList());
    }

    public void handle(final IPayloadContext context) {
        if (!SharedConstants.IS_RUNNING_IN_IDE) return;
        context.enqueueWork(() -> ClinkerDebugRenderers.squadDebugRenderer.handlePacket(dump));
    }
}