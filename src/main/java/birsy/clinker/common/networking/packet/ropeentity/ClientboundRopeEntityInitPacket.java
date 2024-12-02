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

public record ClientboundRopeEntityInitPacket(int entityId, Integer[] segmentTypes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundRopeEntityInitPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/rope/init"));
    public static final StreamCodec<ByteBuf, ClientboundRopeEntityInitPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundRopeEntityInitPacket::entityId,
            ExtraByteBufCodecs.array(ByteBufCodecs.INT),
            ClientboundRopeEntityInitPacket::segmentTypes,
            ClientboundRopeEntityInitPacket::new
    );

    public ClientboundRopeEntityInitPacket(RopeEntity ropeEntity) {
        this(ropeEntity.getId(), Util.make(() -> {
            Integer[] segmentTypes = new Integer[ropeEntity.segments.size()];
            for (int i = 0; i < ropeEntity.segments.size(); i++) segmentTypes[i] = ((RopeEntitySegment) ropeEntity.segments.get(i)).type;
            return segmentTypes;
        }));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(this.entityId);
        if (entity == null || !(entity instanceof RopeEntity)) {
            Clinker.LOGGER.warn("No rope entity with id " + this.entityId + " found on client! Unable to initialize segments.");
            return;
        }
        RopeEntity ropeEntity = (RopeEntity) entity;

        // out with the old
        for (Object segment : ropeEntity.segments) ((RopeEntitySegment) segment).remove(Entity.RemovalReason.DISCARDED);
        ropeEntity.segments.clear();
        ropeEntity.segmentByCollider.clear();

        // in with the new
        for (int i = 0; i < this.segmentTypes.length; i++) {
            ropeEntity.addSegmentToEnd(this.segmentTypes[i]);
        }
    }
}
