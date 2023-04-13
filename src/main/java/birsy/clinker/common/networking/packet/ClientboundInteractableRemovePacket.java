package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class ClientboundInteractableRemovePacket extends ClientboundPacket {
    private UUID interactableID;
    public ClientboundInteractableRemovePacket(UUID interactableID) {
        this.interactableID = interactableID;
    }

    public ClientboundInteractableRemovePacket(FriendlyByteBuf buffer) {
        long[] bits = buffer.readLongArray();
        this.interactableID = new UUID(bits[0], bits[1]);
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        long[] bits = {interactableID.getMostSignificantBits(), interactableID.getLeastSignificantBits()};
        buffer.writeLongArray(bits);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        InteractableManager clientManager = InteractableManager.clientInteractableManager;
        clientManager.interactableMap.get(this.interactableID).markForRemoval();
        Clinker.LOGGER.info("removed interactable " + this.interactableID.toString());
    }
}
