package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.common.world.ordnance.OrdnanceHelper;
import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

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
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        OrdnanceEntity bomb = null;
        if (bombId < 0 && level.getEntity(bombId) instanceof OrdnanceEntity ordnance) bomb = ordnance;
        Entity throwerOrHolder = throwerOrHolderId < 0 ? null : level.getEntity(throwerOrHolderId);
        OrdnanceHelper.detonate(modifierSet, x, y, z, level, bomb, throwerOrHolder);
    }
}
