package birsy.clinker.common.networking.packet.workstation;

import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class ClientboundWorkstationLoadPacket extends ClientboundPacket {
    private final UUID id;
    private final CompoundTag workstationTag;

    public ClientboundWorkstationLoadPacket(UUID id, Workstation workstation) {
        this.id = id;
        this.workstationTag = workstation.serialize();
    }

    public ClientboundWorkstationLoadPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        this.workstationTag = buffer.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(id);
    }

    @Override
    public void run(PlayPayloadContext context) {
        WorkstationManager clientManager = WorkstationManager.clientWorkstationManager;
        Workstation workstation = Workstation.deserialize(this.workstationTag, clientManager.level);
        clientManager.workstationStorage.remove(id);
        clientManager.workstationStorage.put(id, workstation);
    }
}
