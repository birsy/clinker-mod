package birsy.clinker.common.networking.packet.workstation;

import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ClientboundWorkstationMergePacket(UUID uuid0, UUID uuid1) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundWorkstationMergePacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/workstation/merge"));
    public static final StreamCodec<ByteBuf, ClientboundWorkstationMergePacket> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.UUID,
            ClientboundWorkstationMergePacket::uuid0,
            ExtraByteBufCodecs.UUID,
            ClientboundWorkstationMergePacket::uuid1,
            ClientboundWorkstationMergePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        WorkstationManager clientManager = WorkstationManager.clientWorkstationManager;
        clientManager.mergeWorkstations(uuid0, uuid1);
    }
}
