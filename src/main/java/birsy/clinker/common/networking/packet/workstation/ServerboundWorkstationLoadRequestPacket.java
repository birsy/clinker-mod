package birsy.clinker.common.networking.packet.workstation;

import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ServerboundWorkstationLoadRequestPacket(UUID uuid) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundWorkstationLoadRequestPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("server/workstation/load"));
    public static final StreamCodec<ByteBuf, ServerboundWorkstationLoadRequestPacket> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.UUID,
            ServerboundWorkstationLoadRequestPacket::uuid,
            ServerboundWorkstationLoadRequestPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        Entity sender = context.player();
        if (sender.level() instanceof ServerLevel level) {
            WorkstationManager manager = WorkstationManager.managerByLevel.get(level);
            manager.loadWorkstationToClient(uuid, (ServerPlayer) context.player());
        }
    }
}
