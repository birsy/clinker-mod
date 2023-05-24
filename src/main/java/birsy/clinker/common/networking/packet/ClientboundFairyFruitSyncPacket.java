package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundFairyFruitSyncPacket extends ClientboundPacket {
    private final BlockPos blockPos;
    private final CompoundTag tag;

    public ClientboundFairyFruitSyncPacket(FairyFruitBlockEntity entity) {
        this.blockPos = entity.getBlockPos();
        this.tag = entity.serializeNBT();
    }

    public ClientboundFairyFruitSyncPacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
        this.tag = buffer.readNbt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeNbt(this.tag);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockEntity entity = level.getBlockEntity(this.blockPos);
        if (entity == null) { Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to sync!"); return; }
        if (entity instanceof FairyFruitBlockEntity blockEntity) {
            blockEntity.syncFromNBT(this.tag);
        } else {
            Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to sync!");
        }
    }
}
