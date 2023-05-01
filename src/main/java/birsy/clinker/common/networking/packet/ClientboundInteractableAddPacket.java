package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundInteractableAddPacket extends ClientboundPacket {
    private Interactable interactable;

    public ClientboundInteractableAddPacket(Interactable interactable) {
        this.interactable = interactable;
    }

    public ClientboundInteractableAddPacket(FriendlyByteBuf buffer) {
        this.interactable = Interactable.deserialize(buffer.readNbt());
        this.interactable.initialized = false;
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(interactable.serialize());
    }

    @Override
    public void run(NetworkEvent.Context context) {
        InteractableManager clientManager = InteractableManager.clientInteractableManager;
        clientManager.addClientsideInteractable(this.interactable);
    }
}
