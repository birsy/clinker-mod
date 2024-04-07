package birsy.clinker.common.networking.packet.workstation;

import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class ClientboundWorkstationChangeBlockPacket extends ClientboundPacket {
    private final BlockPos pos;
    private final UUID id;
    private final boolean add;

    public ClientboundWorkstationChangeBlockPacket(BlockPos pos, boolean add, UUID id) {
        this.pos = pos;
        this.id = id;
        this.add = add;
    }

    public ClientboundWorkstationChangeBlockPacket(FriendlyByteBuf buffer) {
        CompoundTag NBT = buffer.readNbt();
        this.id = NBT.getUUID("uuid");
        this.add = NBT.getBoolean("add");
        int[] positions =  NBT.getIntArray("pos");
        this.pos = new BlockPos(positions[0], positions[1], positions[2]);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        CompoundTag NBT = new CompoundTag();
        NBT.putUUID("uuid", this.id);
        NBT.putBoolean("add", this.add);
        NBT.putIntArray("pos", new int[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()});

        buffer.writeNbt(NBT);
    }

    @Override
    public void run(PlayPayloadContext context) {
        WorkstationManager clientManager = WorkstationManager.clientWorkstationManager;
        if (this.add) {
            clientManager.addWorkstationBlockToUUID(pos, id);
        } else {
            clientManager.removeWorkstationBlockFromUUID(pos, id);
        }
    }
}
