package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.client.render.debug.MetaChunkDebugRenderer;
import birsy.clinker.common.world.level.gen.metachunk.MetaChunk;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundMetaChunkBeginGenDebugPacket(int size, int depth, int x, int z, int chunkX, int chunkZ, long threadID) implements CustomPacketPayload {
    public static final Type<ClientboundMetaChunkBeginGenDebugPacket> TYPE = new Type<>(Clinker.resource("debug/metachunk_begin"));
    public static final StreamCodec<ByteBuf, ClientboundMetaChunkBeginGenDebugPacket> STREAM_CODEC = ExtraStreamCodecs.composite(
            ByteBufCodecs.INT, ClientboundMetaChunkBeginGenDebugPacket::size,
            ByteBufCodecs.INT, ClientboundMetaChunkBeginGenDebugPacket::depth,
            ByteBufCodecs.INT, ClientboundMetaChunkBeginGenDebugPacket::x,
            ByteBufCodecs.INT, ClientboundMetaChunkBeginGenDebugPacket::z,
            ByteBufCodecs.INT, ClientboundMetaChunkBeginGenDebugPacket::chunkX,
            ByteBufCodecs.INT, ClientboundMetaChunkBeginGenDebugPacket::chunkZ,
            ByteBufCodecs.VAR_LONG, ClientboundMetaChunkBeginGenDebugPacket::threadID,
            ClientboundMetaChunkBeginGenDebugPacket::new
    );

    public ClientboundMetaChunkBeginGenDebugPacket(MetaChunk metaChunk, ChunkPos ownerPos) {
        this(metaChunk.size * 16, metaChunk.depth, metaChunk.minimumX * 16, metaChunk.minimumZ * 16, ownerPos.x, ownerPos.z, Thread.currentThread().threadId());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClinkerDebugRenderers.metaChunkDebugRenderer.startMetaChunkGenerationDebug(
                new MetaChunkDebugRenderer.MetaChunkDebugPos(this.x, this.z, this.size, this.depth),
                this.chunkX, this.chunkZ, this.threadID
        );
    }
}
