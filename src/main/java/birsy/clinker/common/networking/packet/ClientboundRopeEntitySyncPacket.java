package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.rope.RopeEntity;
import birsy.clinker.common.world.entity.rope.RopeEntitySegment;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientboundRopeEntitySyncPacket extends ClientboundPacket {
    final int entityId;
    final double[] segmentAttributes;
    static final int sizePerSegment = 6;

    public ClientboundRopeEntitySyncPacket(RopeEntity ropeEntity) {
        this.entityId = ropeEntity.getId();
        this.segmentAttributes = new double[ropeEntity.segments.size() * sizePerSegment];
        for (int i = 0; i < ropeEntity.segments.size(); i++) {
            int j = i * sizePerSegment;
            RopeEntitySegment segment = ((RopeEntitySegment) ropeEntity.segments.get(i));
            this.segmentAttributes[j + 0] = segment.getPosition().x();
            this.segmentAttributes[j + 1] = segment.getPosition().y();
            this.segmentAttributes[j + 2] = segment.getPosition().z();

            this.segmentAttributes[j + 3] = segment.getPrevWalkVector().x();
            this.segmentAttributes[j + 4] = segment.getPrevWalkVector().y();
            this.segmentAttributes[j + 5] = segment.getPrevWalkVector().z();
        }
    }

    public ClientboundRopeEntitySyncPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        int count = buffer.readInt();
        this.segmentAttributes = new double[count];
        for (int i = 0; i < count; i++) {
            this.segmentAttributes[i] = buffer.readDouble();
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.segmentAttributes.length);
        for (int i = 0; i < this.segmentAttributes.length; i++) {
            buffer.writeDouble(this.segmentAttributes[i]);
        }
    }

    @Override
    public void run(PlayPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(this.entityId);
        if (!(entity instanceof RopeEntity ropeEntity)) {
            Clinker.LOGGER.warn("No rope entity with id {} found on client! Unable to synchronize segments!", this.entityId);
            return;
        }

        if (ropeEntity.segments.size()*sizePerSegment != this.segmentAttributes.length) {
            Clinker.LOGGER.warn("Rope entity with id {} has mismatched segment count! Unable to synchronize segments!", this.entityId);
            return;
        }

        for (int i = 0; i < ropeEntity.segments.size(); i++) {
            int j = i * sizePerSegment;
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
