package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.block.blockentity.FairyFruitBlockEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundFairyFruitBreakPacket extends ClientboundPacket {
    private final BlockPos blockPos;
    private final int index;

    public ClientboundFairyFruitBreakPacket(FairyFruitBlockEntity entity, int index) {
        this.blockPos = entity.getBlockPos();
        this.index = index;
    }

    public ClientboundFairyFruitBreakPacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeInt(index);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockEntity entity = level.getBlockEntity(this.blockPos);
        if (entity == null) { Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to break!"); return; }
        if (entity instanceof FairyFruitBlockEntity blockEntity) {
            FairyFruitBlockEntity.FairyFruitSegment segment = blockEntity.segmentByID.get(index);
            if (segment != null) segment.destroy();
        } else {
            Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to break!");
        }
    }
}
