package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.mold.MoldCell;
import birsy.clinker.common.world.entity.mold.MoldEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundMoldGrowthPacket(int entityId, int parentId, int childId, BlockPos childPos, int attachmentPointOrdinal, int directionOrdinal) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundMoldGrowthPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("client/mold/grow"));
    public static final StreamCodec<ByteBuf, ClientboundMoldGrowthPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundMoldGrowthPacket::entityId,
            ByteBufCodecs.INT, ClientboundMoldGrowthPacket::parentId,
            ByteBufCodecs.INT, ClientboundMoldGrowthPacket::childId,
            ExtraByteBufCodecs.BLOCK_POS, ClientboundMoldGrowthPacket::childPos,
            ByteBufCodecs.INT, ClientboundMoldGrowthPacket::attachmentPointOrdinal, ByteBufCodecs.INT,
            ClientboundMoldGrowthPacket::directionOrdinal,
            ClientboundMoldGrowthPacket::new
    );

    public ClientboundMoldGrowthPacket(MoldCell child) {
        this(child.entity.getId(), child.parent.get().id, child.id, child.pos, child.attachmentPoint.ordinal(), child.parentDirection.ordinal());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        Entity entity = level.getEntity(entityId);
        if (entity instanceof MoldEntity mold) {
            MoldCell parent = mold.parts.get(this.parentId);
            if (parent == null) {
                Clinker.LOGGER.warn("No parent for mold cell found! Queueing...");
            } else {
                // create a new cell.
                new MoldCell(
                        parent,
                        MoldCell.AttachmentPoint.fromOrdinal(this.attachmentPointOrdinal),
                        this.childPos,
                        Direction.from3DDataValue(this.directionOrdinal),
                        this.childId);
            }
        } else {
            Clinker.LOGGER.warn("No mold instance with id {} found! This is not good.", this.entityId);
        }
    }
}
