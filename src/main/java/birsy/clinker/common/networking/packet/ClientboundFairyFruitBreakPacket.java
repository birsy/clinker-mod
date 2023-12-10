package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.NetworkEvent;

import javax.annotation.Nullable;

public class ClientboundFairyFruitBreakPacket extends ClientboundPacket {
    private final BlockPos blockPos;
    private final int index;
    private int entityID = Integer.MAX_VALUE;
    public ClientboundFairyFruitBreakPacket(FairyFruitBlockEntity blockEntity, int index, @Nullable Entity entity) {
        this.blockPos = blockEntity.getBlockPos();
        this.index = index;
        if (blockEntity != null) entityID = entity.getId();
    }

    public ClientboundFairyFruitBreakPacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
        this.index = buffer.readInt();
        this.entityID = buffer.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeInt(index);
        buffer.writeInt(this.entityID);

    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockEntity entity = level.getBlockEntity(this.blockPos);
        if (entity == null) { Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to break!"); return; }
        if (entity instanceof FairyFruitBlockEntity blockEntity) {
            blockEntity.breakAt(entityID == Integer.MAX_VALUE ? null : level.getEntity(entityID), index);
        } else {
            Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to break!");
        }
    }
}
