package birsy.clinker.common.networking.packet.interactable;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ServerboundPacket;
import birsy.clinker.common.world.level.interactableOLD.Interactable;
import birsy.clinker.common.world.level.interactableOLD.InteractableManager;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.UUID;

public class ServerboundInteractableAddFailurePacket extends ServerboundPacket {
    private final UUID uuid;

    public ServerboundInteractableAddFailurePacket(UUID uuid) {
        this.uuid = uuid;
    }

    public ServerboundInteractableAddFailurePacket(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
    }


    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Interactable i = InteractableManager.serverInteractableManagers.get(context.getSender().level()).storage.getInteractable(uuid);
        ClinkerPacketHandler.sendToClient(context.getSender(), new ClientboundInteractableAddPacket(i));
    }
}
