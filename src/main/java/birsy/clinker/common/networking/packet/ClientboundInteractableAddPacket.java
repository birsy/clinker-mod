package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundInteractableAddPacket extends ClientboundPacket {
    private Interactable interactable;

    public ClientboundInteractableAddPacket(Interactable interactable) {
        this.interactable = interactable;
    }

    public ClientboundInteractableAddPacket(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        this.interactable = Interactable.deserialize(tag);
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(interactable.serialize());
    }

    @Override
    public void run(NetworkEvent.Context context) {
        InteractableManager clientManager = InteractableManager.clientInteractableManager;
        Clinker.LOGGER.info(this.interactable.getTransform().position.toString());
        clientManager.addClientsideInteractable(this.interactable);
    }
}
