package birsy.clinker.common.networking.packet.interactable;

import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableAttachment;
import birsy.clinker.common.world.level.interactable.manager.ClientInteractableManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.UUID;

public class ClientboundInteractableRemovePacket extends ClientboundPacket {
    private UUID id;
    public ClientboundInteractableRemovePacket(Interactable interactable) {
        this.id = interactable.id;
    }

    public ClientboundInteractableRemovePacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        ClientInteractableManager manager = (ClientInteractableManager) InteractableAttachment.getInteractableManagerForLevel(level);
        Interactable interactable = manager.getInteractable(id);
        if (interactable != null) manager.removeInteractable(interactable);
    }
}
