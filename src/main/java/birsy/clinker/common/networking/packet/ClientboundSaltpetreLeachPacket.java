package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.SaltpetreFiltrationHandler;
import birsy.clinker.common.world.entity.mold.MoldCell;
import birsy.clinker.common.world.entity.mold.MoldEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ClientboundSaltpetreLeachPacket(BlockPos origin, List<BlockPos> positions) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundSaltpetreLeachPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/saltpetre_leach"));
    public static final StreamCodec<ByteBuf, ClientboundSaltpetreLeachPacket> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.BLOCK_POS, ClientboundSaltpetreLeachPacket::origin,
            ExtraByteBufCodecs.BLOCK_POS.apply(ByteBufCodecs.list()), ClientboundSaltpetreLeachPacket::positions,
            ClientboundSaltpetreLeachPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        SaltpetreFiltrationHandler.doClientEffects(level, this.origin, this.positions);
    }
}
