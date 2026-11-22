package birsy.clinker.common.networking.packet;

import birsy.clinker.common.entity.lungwyrm.LungWyrmEntity;
import birsy.clinker.core.Clinker;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundLungwyrmAirborneHitPacket(int entityId, byte segmentId, short offset, short angle) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundLungwyrmAirborneHitPacket> TYPE =
            new CustomPacketPayload.Type<>(Clinker.resource("client/mob/lungwyrm_arrow"));
    public static final StreamCodec<ByteBuf, ClientboundLungwyrmAirborneHitPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundLungwyrmAirborneHitPacket::entityId,
            ByteBufCodecs.BYTE, ClientboundLungwyrmAirborneHitPacket::segmentId,
            ByteBufCodecs.SHORT, ClientboundLungwyrmAirborneHitPacket::offset,
            ByteBufCodecs.SHORT, ClientboundLungwyrmAirborneHitPacket::angle,
            ClientboundLungwyrmAirborneHitPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handle(this, context));
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientHandler {
        public static void handle(ClientboundLungwyrmAirborneHitPacket packet, final IPayloadContext context) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            Entity entity = level.getEntity(packet.entityId);
            if (!(entity instanceof LungWyrmEntity validEntity)) return;
            validEntity.receiveAirborneHitEffectPacket(packet.segmentId(), packet.offset, packet.angle);
        }
    }
}
