package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.client.render.debug.MetaChunkDebugRenderer;
import birsy.clinker.common.world.level.gen.metachunk.MetaChunk;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ClientboundMetaChunkDebugPacket(int size, int depth, int x, int z) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundMetaChunkDebugPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("debug/metachunk"));
    public static final StreamCodec<ByteBuf, ClientboundMetaChunkDebugPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundMetaChunkDebugPacket::size,
            ByteBufCodecs.INT, ClientboundMetaChunkDebugPacket::depth,
            ByteBufCodecs.INT, ClientboundMetaChunkDebugPacket::x,
            ByteBufCodecs.INT, ClientboundMetaChunkDebugPacket::z,
            ClientboundMetaChunkDebugPacket::new
    );

    public ClientboundMetaChunkDebugPacket(MetaChunk metaChunk) {
        this(metaChunk.size * 16, metaChunk.depth, metaChunk.minimumX * 16, metaChunk.minimumZ * 16);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClinkerDebugRenderers.metaChunkDebugRenderer.addMetaChunk(new MetaChunkDebugRenderer.MetaChunkDebugInfo(size, depth, x, z));
    }
}
