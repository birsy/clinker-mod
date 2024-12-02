package birsy.clinker.common.networking.packet.ropeentity;

import birsy.clinker.common.world.entity.rope.RopeEntity;
import birsy.clinker.core.Clinker;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundRopeEntitySegmentAddPacket(int entityId, int segmentType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundRopeEntitySegmentAddPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/rope/add"));
    public static final StreamCodec<ByteBuf, ClientboundRopeEntitySegmentAddPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundRopeEntitySegmentAddPacket::entityId,
            ByteBufCodecs.INT,
            ClientboundRopeEntitySegmentAddPacket::segmentType,
            ClientboundRopeEntitySegmentAddPacket::new
    );

    public ClientboundRopeEntitySegmentAddPacket(RopeEntity ropeEntity, int segmentType) {
        this(ropeEntity.getId(), segmentType);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Entity entity = level.getEntity(this.entityId);
        if (!(entity instanceof RopeEntity ropeEntity)) {
            Clinker.LOGGER.warn("No rope entity with id " + this.entityId + " found on client! Unable to add segment.");
            return;
        }
        ropeEntity.addSegmentToEnd(this.segmentType);
    }
}
