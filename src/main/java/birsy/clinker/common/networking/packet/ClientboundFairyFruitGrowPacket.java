package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.NetworkEvent;

public class ClientboundFairyFruitGrowPacket extends ClientboundPacket {
    private final BlockPos blockPos;
    private final boolean fromBoneMeal;

    public ClientboundFairyFruitGrowPacket(FairyFruitBlockEntity entity, boolean fromBoneMeal) {
        this.blockPos = entity.getBlockPos();
        this.fromBoneMeal = fromBoneMeal;
    }

    public ClientboundFairyFruitGrowPacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
        this.fromBoneMeal = buffer.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeBoolean(fromBoneMeal);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockEntity entity = level.getBlockEntity(this.blockPos);
        if (entity == null) { Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to grow!"); return; }
        if (entity instanceof FairyFruitBlockEntity blockEntity) {
            blockEntity.grow(false, fromBoneMeal);
        } else {
            Clinker.LOGGER.warn("No matching Fairy Fruit at" + blockPos + " found to grow!");
        }
    }
}
