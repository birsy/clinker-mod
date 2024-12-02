package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.proceduralanimation.IKLocomotionEntity;
import birsy.clinker.core.Clinker;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3d;

public record ClientboundInverseKinematicsStepPacket<E extends LivingEntity & IKLocomotionEntity>
        (int entityId, int legIndex, double stepX, double stepY, double stepZ) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundInverseKinematicsStepPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/ik/step"));
    public static final StreamCodec<ByteBuf, ClientboundInverseKinematicsStepPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundInverseKinematicsStepPacket::entityId,
            ByteBufCodecs.INT, ClientboundInverseKinematicsStepPacket::legIndex,
            ByteBufCodecs.DOUBLE, ClientboundInverseKinematicsStepPacket::stepX,
            ByteBufCodecs.DOUBLE, ClientboundInverseKinematicsStepPacket::stepY,
            ByteBufCodecs.DOUBLE, ClientboundInverseKinematicsStepPacket::stepZ,
            ClientboundInverseKinematicsStepPacket::new
    );

    public ClientboundInverseKinematicsStepPacket(E entity, int legIndex, Vector3d stepPosition) {
        this(entity.getId(), legIndex, stepPosition.x, stepPosition.y, stepPosition.z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Entity entity = level.getEntity(entityId);
        if (entity instanceof LivingEntity liver && entity instanceof IKLocomotionEntity critter) {
            critter.getLeg(this.legIndex).step(true, liver, this.stepX, this.stepY, this.stepZ);
        } else {
            Clinker.LOGGER.warn("No IKLocomotionEntity instance with id {} found! This is not good. If something's legs seem broken and you see this message in your logs, put it in the Clinker discord server. If you see this message but nothing looks broken then it's probably fine.", this.entityId);
        }
    }
}
