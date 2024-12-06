package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
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
import org.joml.Vector3f;

public record ClientboundGroundLocomotorSyncPacket(int entityId, Vector3f walkVector, float cumulativeDistance) implements CustomPacketPayload {
    public static final Type<ClientboundGroundLocomotorSyncPacket> TYPE = new Type<>(Clinker.resource("client/entity/walk"));
    public static final StreamCodec<ByteBuf, ClientboundGroundLocomotorSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundGroundLocomotorSyncPacket::entityId,
            ByteBufCodecs.VECTOR3F, ClientboundGroundLocomotorSyncPacket::walkVector,
            ByteBufCodecs.FLOAT, ClientboundGroundLocomotorSyncPacket::cumulativeDistance,
            ClientboundGroundLocomotorSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        Entity entity = level.getEntity(this.entityId);
        if (!(entity instanceof GroundLocomoteEntity validEntity)) return;

        validEntity.walk.set(this.walkVector);
        validEntity.setCumulativeWalk(this.cumulativeDistance);
    }
}
