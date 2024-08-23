package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.RopeEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientboundRopeEntitySegmentAddPacket extends ClientboundPacket {
    final int entityId, segmentType;

    public ClientboundRopeEntitySegmentAddPacket(RopeEntity ropeEntity, int segmentType) {
        this.entityId = ropeEntity.getId();
        this.segmentType = segmentType;
    }

    public ClientboundRopeEntitySegmentAddPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.segmentType = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.segmentType);
    }

    @Override
    public void run(PlayPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(this.entityId);
        if (entity == null || !(entity instanceof RopeEntity)) { Clinker.LOGGER.warn("No rope entity with id " + this.entityId + " found on client! Unable to add segment."); return; }

        RopeEntity ropeEntity = (RopeEntity) entity;
        ropeEntity.addSegmentToEnd(this.segmentType);
    }
}
