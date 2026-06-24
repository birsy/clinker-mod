package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.common.world.ordnance.OrdnanceHelper;
import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundOrdnanceExplosionPacket(OrdnanceModifierSet modifierSet, double x, double y, double z, int bombId, int throwerOrHolderId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundOrdnanceExplosionPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/ordnance/explode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOrdnanceExplosionPacket> STREAM_CODEC = StreamCodec.composite(
            OrdnanceModifierSet.STREAM_CODEC, ClientboundOrdnanceExplosionPacket::modifierSet,
            ByteBufCodecs.DOUBLE, ClientboundOrdnanceExplosionPacket::x,
            ByteBufCodecs.DOUBLE, ClientboundOrdnanceExplosionPacket::y,
            ByteBufCodecs.DOUBLE, ClientboundOrdnanceExplosionPacket::z,
            ByteBufCodecs.INT, ClientboundOrdnanceExplosionPacket::bombId,
            ByteBufCodecs.INT, ClientboundOrdnanceExplosionPacket::throwerOrHolderId,
            ClientboundOrdnanceExplosionPacket::new
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
        public static void handle(ClientboundOrdnanceExplosionPacket packet, IPayloadContext context) {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;
            OrdnanceEntity bomb = packet.bombId >= 0 && level.getEntity(packet.bombId) instanceof OrdnanceEntity ordnance ? ordnance : null;
            Entity throwerOrHolder = packet.throwerOrHolderId >= 0 ? level.getEntity(packet.throwerOrHolderId) : null;
            OrdnanceHelper.detonate(packet.modifierSet, packet.x, packet.y, packet.z, level, bomb, throwerOrHolder);
        }
    }
}
