package birsy.clinker.common.networking.packet;

import birsy.clinker.common.entity.SegmentedEntity;
import birsy.clinker.core.Clinker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSegmentedEntitySyncPacket(int entityId, byte[] data) implements CustomPacketPayload {
    public static final Type<ClientboundSegmentedEntitySyncPacket> TYPE = new Type<>(Clinker.resource("client/segmented_entity_full_sync"));
    public static final StreamCodec<ByteBuf, ClientboundSegmentedEntitySyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSegmentedEntitySyncPacket::entityId,
            ByteBufCodecs.BYTE_ARRAY, ClientboundSegmentedEntitySyncPacket::data,
            ClientboundSegmentedEntitySyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> ClientboundSegmentedEntitySyncPacket.ClientHandler.handle(this, context));
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientHandler {
        public static void handle(ClientboundSegmentedEntitySyncPacket packet, final IPayloadContext context) {
            RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(
                    Unpooled.wrappedBuffer(packet.data()),
                    context.player().registryAccess(),
                    ConnectionType.NEOFORGE
            );
            context.enqueueWork(() -> {
                Entity entity = context.player().level().getEntity(packet.entityId());
                if (entity instanceof SegmentedEntity segmentedEntity) {
                    segmentedEntity.updateFromSyncPacket(buffer);
                }
            });
        }
    }
}
