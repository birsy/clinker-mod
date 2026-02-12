package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.core.Clinker;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;

public record ClientboundMobLocomotionSyncPacket(int entityId, Vector3f walkVector, float cumulativeDistance) implements CustomPacketPayload {
    public static final Type<ClientboundMobLocomotionSyncPacket> TYPE = new Type<>(Clinker.resource("client/mob/sync_locomotion"));
    public static final StreamCodec<ByteBuf, ClientboundMobLocomotionSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundMobLocomotionSyncPacket::entityId,
            ByteBufCodecs.VECTOR3F, ClientboundMobLocomotionSyncPacket::walkVector,
            ByteBufCodecs.FLOAT, ClientboundMobLocomotionSyncPacket::cumulativeDistance,
            ClientboundMobLocomotionSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        Entity entity = level.getEntity(this.entityId);
        if (!(entity instanceof GroundLocomotionEntity validEntity)) return;

        validEntity.smoothedLocomotionGoalVector.set(this.walkVector);
        validEntity.setCumulativeLocomotionAmount(this.cumulativeDistance);
    }
}
