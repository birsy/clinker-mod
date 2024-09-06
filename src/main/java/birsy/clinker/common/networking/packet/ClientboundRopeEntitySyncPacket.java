package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.rope.RopeEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientboundRopeEntitySyncPacket extends ClientboundPacket {
    final int entityId;
    final double[] segmentPositions;

    public ClientboundRopeEntitySyncPacket(RopeEntity ropeEntity) {
        this.entityId = ropeEntity.getId();
        this.segmentPositions = new double[ropeEntity.segments.size() * 3];
        for (int i = 0; i < ropeEntity.segments.size(); i++) {
            int j = i * 3;
            RopeEntity.RopeEntitySegment segment = ((RopeEntity.RopeEntitySegment) ropeEntity.segments.get(i));
            this.segmentPositions[j + 0] = segment.getPosition().x();
            this.segmentPositions[j + 1] = segment.getPosition().y();
            this.segmentPositions[j + 2] = segment.getPosition().z();
        }
    }

    public ClientboundRopeEntitySyncPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        int count = buffer.readInt();
        this.segmentPositions = new double[count];
        for (int i = 0; i < count; i++) {
            this.segmentPositions[i] = buffer.readDouble();
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.segmentPositions.length);
        for (int i = 0; i < this.segmentPositions.length; i++) {
            buffer.writeDouble(this.segmentPositions[i]);
        }
    }

    @Override
    public void run(PlayPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(this.entityId);
        if (entity == null || !(entity instanceof RopeEntity)) {
            Clinker.LOGGER.warn("No rope entity with id {} found on client! Unable to synchronize segments!", this.entityId);
            return;
        }

        RopeEntity ropeEntity = (RopeEntity) entity;
        if (ropeEntity.segments.size()*3 != this.segmentPositions.length) {
            Clinker.LOGGER.warn("Rope entity with id {} has mismatched segment count! Unable to synchronize segments!", this.entityId);
            return;
        }

        for (int i = 0; i < ropeEntity.segments.size(); i++) {
            int j = i * 3;
            RopeEntity.RopeEntitySegment segment = ((RopeEntity.RopeEntitySegment) ropeEntity.segments.get(i));
            segment.setNextPosition(
                    this.segmentPositions[j + 0],
                    this.segmentPositions[j + 1],
                    this.segmentPositions[j + 2]);
        }
    }
}
