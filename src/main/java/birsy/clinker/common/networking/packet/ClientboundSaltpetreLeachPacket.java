package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.SaltpetreFiltrationHandler;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
        context.enqueueWork(() -> ClientHandler.handle(this, context));
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientHandler {
        public static void handle(ClientboundSaltpetreLeachPacket packet, final IPayloadContext context) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            SaltpetreFiltrationHandler.doClientEffects(level, packet.origin, packet.positions);
        }
    }
}
