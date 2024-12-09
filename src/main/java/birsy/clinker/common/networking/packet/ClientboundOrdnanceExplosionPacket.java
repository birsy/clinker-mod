package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.item.components.OrdnanceEffects;
import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundOrdnanceExplosionPacket(Vec3 location, Tag serializedEffects) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundOrdnanceExplosionPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/ordnance/explode"));
    public static final StreamCodec<ByteBuf, ClientboundOrdnanceExplosionPacket> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.VEC3, ClientboundOrdnanceExplosionPacket::location,
            ByteBufCodecs.TAG, ClientboundOrdnanceExplosionPacket::serializedEffects,
            ClientboundOrdnanceExplosionPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        OrdnanceEntity.createOrdnanceExplosion(
                location, level, null, null,
                OrdnanceEffects.deserialize(serializedEffects, level.registryAccess())
        );
    }
}
