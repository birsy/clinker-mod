package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.block.blockentity.FairyFruitBlockEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundFairyFruitGrowPacket extends ClientboundPacket {
    private final BlockPos blockPos;
    private final boolean beginGrown;
    private final int segmentID;
    private final int jointID;
    public ClientboundFairyFruitGrowPacket(FairyFruitBlockEntity entity, boolean beginGrown, int segmentID, int jointID) {
        this.blockPos = entity.getBlockPos();
        this.beginGrown = beginGrown;
        this.segmentID = segmentID;
        this.jointID = jointID;
    }

    public ClientboundFairyFruitGrowPacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
        this.beginGrown = buffer.readBoolean();
        int[] ints = buffer.readVarIntArray(2);
        this.segmentID = ints[0];
        this.jointID = ints[1];
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeBoolean(beginGrown);
        buffer.writeVarIntArray(new int[]{segmentID, jointID});
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockEntity entity = level.getBlockEntity(this.blockPos);
        if (entity == null) { Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to grow!"); return; }
        if (entity instanceof FairyFruitBlockEntity blockEntity) {
            blockEntity.grow(this.beginGrown, this.jointID, this.segmentID);
        } else {
            Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to grow!");
        }
    }
}
