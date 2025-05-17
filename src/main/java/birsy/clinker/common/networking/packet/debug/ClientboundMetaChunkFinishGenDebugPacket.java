package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.client.render.debug.MetaChunkDebugRenderer;
import birsy.clinker.common.world.level.gen.metachunk.MetaChunk;
import birsy.clinker.common.world.level.gen.metachunk.feature.MetaChunkFeature;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;

public record ClientboundMetaChunkFinishGenDebugPacket(int size, int depth, int x, int z, ArrayList<BlockPos> featurePositions) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundMetaChunkFinishGenDebugPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("debug/metachunk_end"));
    public static final StreamCodec<ByteBuf, ClientboundMetaChunkFinishGenDebugPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundMetaChunkFinishGenDebugPacket::size,
            ByteBufCodecs.INT, ClientboundMetaChunkFinishGenDebugPacket::depth,
            ByteBufCodecs.INT, ClientboundMetaChunkFinishGenDebugPacket::x,
            ByteBufCodecs.INT, ClientboundMetaChunkFinishGenDebugPacket::z,
            ExtraByteBufCodecs.BLOCK_POS.apply(ByteBufCodecs.collection(ArrayList::new)), ClientboundMetaChunkFinishGenDebugPacket::featurePositions,
            ClientboundMetaChunkFinishGenDebugPacket::new
    );

    public ClientboundMetaChunkFinishGenDebugPacket(MetaChunk metaChunk) {
        this(metaChunk.size * 16, metaChunk.depth, metaChunk.minimumX * 16, metaChunk.minimumZ * 16, Util.make(() -> {
            ArrayList<BlockPos> list = new ArrayList<>(metaChunk.features.size());
            for (MetaChunkFeature feature : metaChunk.features)
                list.add(new BlockPos(feature.originX, 0, feature.originZ));
            return list;
        }));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClinkerDebugRenderers.metaChunkDebugRenderer.completeMetaChunkGenerationDebug(
                new MetaChunkDebugRenderer.MetaChunkDebugPos(this.x, this.z, this.size, this.depth),
                this.featurePositions
        );
    }
}
