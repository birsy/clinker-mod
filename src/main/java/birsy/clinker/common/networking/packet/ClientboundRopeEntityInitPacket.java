package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.rope.RopeEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientboundRopeEntityInitPacket extends ClientboundPacket {
    final int entityId;
    final int[] segmentTypes;

    public ClientboundRopeEntityInitPacket(RopeEntity ropeEntity) {
        this.entityId = ropeEntity.getId();
        this.segmentTypes = new int[ropeEntity.segments.size()];
        for (int i = 0; i < ropeEntity.segments.size(); i++) {
            segmentTypes[i] = ((RopeEntity.RopeEntitySegment) ropeEntity.segments.get(i)).type;
        }
    }

    public ClientboundRopeEntityInitPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        int count = buffer.readInt();
        this.segmentTypes = new int[count];
        for (int i = 0; i < count; i++) {
            this.segmentTypes[i] = buffer.readInt();
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.segmentTypes.length);
        for (int i = 0; i < this.segmentTypes.length; i++) {
            buffer.writeInt(this.segmentTypes[i]);
        }
    }

    @Override
    public void run(PlayPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(this.entityId);
        if (entity == null || !(entity instanceof RopeEntity)) {
            Clinker.LOGGER.warn("No rope entity with id " + this.entityId + " found on client! Unable to initialize segments.");
            return;
        }
        RopeEntity ropeEntity = (RopeEntity) entity;

        // out with the old
        for (Object segment : ropeEntity.segments) ((RopeEntity.RopeEntitySegment) segment).remove(Entity.RemovalReason.DISCARDED);
        ropeEntity.segments.clear();
        ropeEntity.segmentByCollider.clear();

        // in with the new
        for (int i = 0; i < this.segmentTypes.length; i++) {
            ropeEntity.addSegmentToEnd(this.segmentTypes[i]);
        }
    }
}
