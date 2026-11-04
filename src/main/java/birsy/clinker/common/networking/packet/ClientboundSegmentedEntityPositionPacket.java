package birsy.clinker.common.networking.packet;

import birsy.clinker.common.entity.SegmentedEntity;
import birsy.clinker.common.world.SaltpetreFiltrationHandler;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
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

public record ClientboundSegmentedEntityPositionPacket(int entityId, short[] offsets) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundSegmentedEntityPositionPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/segmented_entity_position"));
    public static final StreamCodec<ByteBuf, ClientboundSegmentedEntityPositionPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSegmentedEntityPositionPacket::entityId,
            ExtraByteBufCodecs.SHORT_ARRAY, ClientboundSegmentedEntityPositionPacket::offsets,
            ClientboundSegmentedEntityPositionPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> ClientboundSegmentedEntityPositionPacket.ClientHandler.handle(this, context));
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientHandler {
        public static void handle(ClientboundSegmentedEntityPositionPacket packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                Entity entity = context.player().level().getEntity(packet.entityId());
                if (entity instanceof SegmentedEntity segmentedEntity) {
                    segmentedEntity.updateFromPositionPacket(packet.offsets());
                }
            });
        }
    }
}
