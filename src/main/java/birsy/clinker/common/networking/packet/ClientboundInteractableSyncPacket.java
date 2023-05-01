package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.rigidbody.Transform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class ClientboundInteractableSyncPacket extends ClientboundPacket {
    private UUID interactableID;
    private Transform interactableTransform;

    public ClientboundInteractableSyncPacket(Interactable interactable) {
        this.interactableID = interactable.uuid;
        this.interactableTransform = interactable.getTransform();
    }

    public ClientboundInteractableSyncPacket(FriendlyByteBuf buffer) {
        long[] bits = buffer.readLongArray();
        this.interactableID = new UUID(bits[0], bits[1]);

        CompoundTag tag = buffer.readNbt();
        this.interactableTransform = Transform.fromNBT(tag);
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        long[] bits = {interactableID.getMostSignificantBits(), interactableID.getLeastSignificantBits()};
        buffer.writeLongArray(bits);

        buffer.writeNbt(interactableTransform.serialize());
    }

    @Override
    public void run(NetworkEvent.Context context) {
        InteractableManager clientManager = InteractableManager.clientInteractableManager;
        Interactable interactable = clientManager.storage.getInteractable(interactableID);
        if (interactable == null) {
            Clinker.LOGGER.warn("No interactable with UUID " + this.interactableID + " found on client!");
            return;
        }
        interactable.setTransform(this.interactableTransform);
    }
}
