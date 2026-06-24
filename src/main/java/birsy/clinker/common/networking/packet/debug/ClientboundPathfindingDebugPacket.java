package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.common.networking.packet.ClientboundMobLocomotionSyncPacket;
import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Path;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundPathfindingDebugPacket(Path path, float maxDistanceToWaypoint, int id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundPathfindingDebugPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("debug/path"));
    public static final StreamCodec<ByteBuf, ClientboundPathfindingDebugPacket> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.PATH, ClientboundPathfindingDebugPacket::path,
            ByteBufCodecs.FLOAT, ClientboundPathfindingDebugPacket::maxDistanceToWaypoint,
            ByteBufCodecs.INT, ClientboundPathfindingDebugPacket::id,
            ClientboundPathfindingDebugPacket::new
    );

    public ClientboundPathfindingDebugPacket(Mob entity, Path path) {
        this(path, entity.getNavigation().getMaxDistanceToWaypoint(), entity.getId());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> ClientHandler.handle(this, context));
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientHandler {
        public static void handle(ClientboundPathfindingDebugPacket packet, final IPayloadContext context) {
            Minecraft mc = Minecraft.getInstance();
            mc.debugRenderer.pathfindingRenderer.addPath(packet.id, packet.path, packet.maxDistanceToWaypoint);
        }
    }
}
