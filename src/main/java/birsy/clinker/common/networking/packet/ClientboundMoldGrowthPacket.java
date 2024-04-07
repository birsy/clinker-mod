package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.entity.mold.MoldCell;
import birsy.clinker.common.world.entity.mold.MoldEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientboundMoldGrowthPacket extends ClientboundPacket {
    private final int entityId;
    private final int parentId;
    private final int childId;
    private final BlockPos childPosition;
    private final MoldCell.AttachmentPoint attachmentPoint;
    private final Direction parentDirection;

    public ClientboundMoldGrowthPacket(MoldCell child) {
        this.entityId = child.entity.getId();
        this.parentId = child.parent.get().id;
        this.childId = child.id;
        this.childPosition = child.pos;
        this.attachmentPoint = child.attachmentPoint;
        this.parentDirection = child.parentDirection;
    }

    public ClientboundMoldGrowthPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.parentId = buffer.readInt();
        this.childId = buffer.readInt();
        this.childPosition = buffer.readBlockPos();
        this.attachmentPoint = MoldCell.AttachmentPoint.valueOf(buffer.readUtf());
        this.parentDirection = Direction.from3DDataValue(buffer.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.parentId);
        buffer.writeInt(this.childId);

        buffer.writeBlockPos(this.childPosition);
        buffer.writeUtf(this.attachmentPoint.name());
        buffer.writeInt(this.parentDirection.get3DDataValue());
    }

    @Override
    public void run(PlayPayloadContext context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        Entity entity = level.getEntity(entityId);
        if (entity instanceof MoldEntity mold) {
            MoldCell parent = mold.parts.get(this.parentId);
            if (parent == null) {
                Clinker.LOGGER.warn("No parent for mold cell found! Queueing...");
            } else {
                MoldCell child = new MoldCell(parent, this.attachmentPoint, this.childPosition, this.parentDirection, this.childId);
            }
        } else {
            Clinker.LOGGER.warn("No mold instance with id " + this.entityId + " found! This is not good.");
        }
    }
}
