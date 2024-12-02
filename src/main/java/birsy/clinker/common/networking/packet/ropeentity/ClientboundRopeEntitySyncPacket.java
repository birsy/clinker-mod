package birsy.clinker.common.networking.packet.ropeentity;

import birsy.clinker.common.world.entity.rope.RopeEntity;
import birsy.clinker.common.world.entity.rope.RopeEntitySegment;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundRopeEntitySyncPacket(int entityId, Double[] segmentAttributes) implements CustomPacketPayload {
    static final int SIZE_PER_SEGMENT = 6;
    public static final CustomPacketPayload.Type<ClientboundRopeEntitySyncPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/rope/sync"));
    public static final StreamCodec<ByteBuf, ClientboundRopeEntitySyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundRopeEntitySyncPacket::entityId,
            ExtraByteBufCodecs.array(ByteBufCodecs.DOUBLE),
            ClientboundRopeEntitySyncPacket::segmentAttributes,
            ClientboundRopeEntitySyncPacket::new
    );

    public ClientboundRopeEntitySyncPacket(RopeEntity ropeEntity) {
        this(ropeEntity.getId(), Util.make(() -> {
            Double[] segmentAttributes = new Double[ropeEntity.segments.size() * SIZE_PER_SEGMENT];
            for (int i = 0; i < ropeEntity.segments.size(); i++) {
                int j = i * SIZE_PER_SEGMENT;
                RopeEntitySegment segment = ((RopeEntitySegment) ropeEntity.segments.get(i));
                segmentAttributes[j + 0] = segment.getPosition().x();
                segmentAttributes[j + 1] = segment.getPosition().y();
                segmentAttributes[j + 2] = segment.getPosition().z();

                segmentAttributes[j + 3] = segment.getPrevWalkVector().x();
                segmentAttributes[j + 4] = segment.getPrevWalkVector().y();
                segmentAttributes[j + 5] = segment.getPrevWalkVector().z();
            }
            return segmentAttributes;
        }));
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
            Clinker.LOGGER.warn("No rope entity with id {} found on client! Unable to synchronize segments!", this.entityId);
            return;
        }

        if (ropeEntity.segments.size()* SIZE_PER_SEGMENT != this.segmentAttributes.length) {
            Clinker.LOGGER.warn("Rope entity with id {} has mismatched segment count! Unable to synchronize segments!", this.entityId);
            return;
        }

        for (int i = 0; i < ropeEntity.segments.size(); i++) {
            int j = i * SIZE_PER_SEGMENT;
            RopeEntitySegment segment = ((RopeEntitySegment) ropeEntity.segments.get(i));
            segment.setNextPosition(
                    this.segmentAttributes[j + 0],
                    this.segmentAttributes[j + 1],
                    this.segmentAttributes[j + 2]);
            segment.walkRaw(
                    this.segmentAttributes[j + 3],
                    this.segmentAttributes[j + 4],
                    this.segmentAttributes[j + 5]);
        }
    }
}
