package birsy.clinker.common.networking.packet.workstation;

import birsy.clinker.common.networking.packet.ServerboundPacket;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class ServerboundWorkstationLoadRequestPacket extends ServerboundPacket {
    private final UUID id;
    public ServerboundWorkstationLoadRequestPacket(UUID id, Level level) {
        this.id = id;
    }

    public ServerboundWorkstationLoadRequestPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(id);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Entity sender = context.getSender();
        if (sender.level instanceof ServerLevel level) {
            WorkstationManager manager = WorkstationManager.managerByLevel.get(level);
            manager.loadWorkstationToClient(id, context.getSender());
        }
    }
}
