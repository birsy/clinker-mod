package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.proceduralanimation.IKLocomotionEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.joml.Vector3d;

public class ClientboundInverseKinematicsStepPacket<E extends LivingEntity & IKLocomotionEntity> extends ClientboundPacket {
    private final int entityId;
    private final int legIndex;
    private final double stepX, stepY, stepZ;

    public ClientboundInverseKinematicsStepPacket(E entity, int legIndex, Vector3d stepPosition) {
        this.entityId = entity.getId();
        this.legIndex = legIndex;
        this.stepX = stepPosition.x;
        this.stepY = stepPosition.y;
        this.stepZ = stepPosition.z;

    }

    public ClientboundInverseKinematicsStepPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.legIndex = buffer.readInt();
        this.stepX = buffer.readDouble();
        this.stepY = buffer.readDouble();
        this.stepZ = buffer.readDouble();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.legIndex);
        buffer.writeDouble(this.stepX);
        buffer.writeDouble(this.stepY);
        buffer.writeDouble(this.stepZ);
    }

    @Override
    public void run(PlayPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        Entity entity = level.getEntity(entityId);
        if (entity instanceof LivingEntity liver && entity instanceof IKLocomotionEntity critter) {
            critter.getLeg(this.legIndex).step(true, liver, this.stepX, this.stepY, this.stepZ);
        } else {
            Clinker.LOGGER.warn("No IKLocomotionEntity instance with id " + this.entityId + " found! This is not good. If something's legs seem broken and you see this message in your logs, put it in the Clinker discord server. If you see this message but nothing looks broken then it's probably fine.");
        }
    }
}
