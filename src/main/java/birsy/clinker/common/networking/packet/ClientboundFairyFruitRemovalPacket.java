package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitJoint;
import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitSegment;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.NetworkEvent;

public class ClientboundFairyFruitRemovalPacket extends ClientboundPacket {
    private final BlockPos blockPos;
    private final int index;
    private final boolean isSegment;

    public ClientboundFairyFruitRemovalPacket(Object segmentOrJoint) {
        if (segmentOrJoint instanceof FairyFruitSegment seg) {
            isSegment = true;
            index = seg.index;
            blockPos = seg.getParent().getBlockPos();
        } else if (segmentOrJoint instanceof FairyFruitJoint joint) {
            isSegment = false;
            index = joint.index;
            blockPos = joint.getParent().getBlockPos();
        } else {
            throw new UnsupportedOperationException("Object " + segmentOrJoint.getClass() + " not fairy fruit segment or joint!");
        }
    }

    public ClientboundFairyFruitRemovalPacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
        this.index = buffer.readInt();
        this.isSegment = buffer.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeInt(index);
        buffer.writeBoolean(isSegment);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockEntity entity = level.getBlockEntity(this.blockPos);
        if (entity == null) { Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to break!"); return; }
        if (entity instanceof FairyFruitBlockEntity blockEntity) {
            if (isSegment) {
                blockEntity.segments.get(index).shouldBeRemoved = true;
            } else {
                blockEntity.joints.get(index).shouldBeRemoved = true;
            }
        } else {
            Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to break!");
        }
    }
}
