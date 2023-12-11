package birsy.clinker.common.networking.packet.interactable;

import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableAttachment;
import birsy.clinker.common.world.level.interactable.manager.ClientInteractableManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.UUID;

public class ClientboundInteractableAddPacket extends ClientboundPacket {
    final Interactable interactable;

    public ClientboundInteractableAddPacket(Interactable interactable) {
        this.interactable = interactable;
    }

    public ClientboundInteractableAddPacket(FriendlyByteBuf buffer) {
        this.interactable = Interactable.deserializeNBT(buffer.readNbt());
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.interactable.serializeNBT());
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        ClientInteractableManager manager = (ClientInteractableManager) InteractableAttachment.getInteractableManagerForLevel(level);
        manager.addInteractable(interactable);
    }
}
