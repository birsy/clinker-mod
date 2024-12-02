package birsy.clinker.common.networking.packet.workstation;

import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ClientboundWorkstationChangeBlockPacket(BlockPos pos, boolean add, UUID uuid) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundWorkstationChangeBlockPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/workstation/changeblock"));
    public static final StreamCodec<ByteBuf, ClientboundWorkstationChangeBlockPacket> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.BLOCK_POS,
            ClientboundWorkstationChangeBlockPacket::pos,
            ByteBufCodecs.BOOL,
            ClientboundWorkstationChangeBlockPacket::add,
            ExtraByteBufCodecs.UUID,
            ClientboundWorkstationChangeBlockPacket::uuid,
            ClientboundWorkstationChangeBlockPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        WorkstationManager clientManager = WorkstationManager.clientWorkstationManager;
        if (this.add) {
            clientManager.addWorkstationBlockToUUID(pos, uuid);
        } else {
            clientManager.removeWorkstationBlockFromUUID(pos, uuid);
        }
    }
}
