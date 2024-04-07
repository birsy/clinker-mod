package birsy.clinker.common.networking.packet.workstation;

import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class ClientboundWorkstationMergePacket extends ClientboundPacket {
    private final UUID id0;
    private final UUID id1;

    public ClientboundWorkstationMergePacket(UUID id0, UUID id1) {
        this.id0 = id0;
        this.id1 = id1;
    }

    public ClientboundWorkstationMergePacket(FriendlyByteBuf buffer) {
        CompoundTag NBT = buffer.readNbt();
        this.id0 = NBT.getUUID("uuid0");
        this.id1 = NBT.getUUID("uuid1");
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        CompoundTag NBT = new CompoundTag();
        NBT.putUUID("uuid0", this.id0);
        NBT.putUUID("uuid1", this.id1);
        buffer.writeNbt(NBT);
    }

    @Override
    public void run(PlayPayloadContext context) {
        WorkstationManager clientManager = WorkstationManager.clientWorkstationManager;
        clientManager.mergeWorkstations(id0, id1);
    }
}
