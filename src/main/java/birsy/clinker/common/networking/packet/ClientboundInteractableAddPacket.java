package birsy.clinker.common.networking.packet;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class ClientboundInteractableAddPacket extends ClientboundPacket {
    private Interactable interactable;
    private UUID uuid;
    public ClientboundInteractableAddPacket(Interactable interactable) {
        this.interactable = interactable;
    }

    public ClientboundInteractableAddPacket(FriendlyByteBuf buffer) {
        CompoundTag NBT = buffer.readNbt();
        this.interactable = Interactable.deserialize(NBT);

        if (this.interactable == null) {
            Clinker.LOGGER.warn("Interactable deserialization failed! Trying again.");
            this.uuid = NBT.getUUID("uuid");
        } else {
            this.interactable.initialized = false;
        }
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(interactable.serialize());
    }

    @Override
    public void run(NetworkEvent.Context context) {
        if (interactable == null) {
            if (uuid == null) throw new RuntimeException("Interactable UUID failed to serialized. If this happens, something has gone terribly, terribly wrong. Sorry!");
            ClinkerPacketHandler.sendToServer(new ServerboundInteractableAddFailurePacket(uuid));
            return;
        }
        InteractableManager clientManager = InteractableManager.clientInteractableManager;
        clientManager.addClientsideInteractable(this.interactable);
    }
}
