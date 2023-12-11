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

public class ClientboundInteractableUpdatePacket extends ClientboundPacket {
    final Vector3d position;
    final Quaterniond orientation;
    final UUID id;

    public ClientboundInteractableUpdatePacket(Interactable interactable) {
        this.id = interactable.id;
        this.position = interactable.getPosition();
        this.orientation = interactable.getOrientation();
    }

    public ClientboundInteractableUpdatePacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        this.position = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        this.orientation = new Quaterniond(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeDouble(position.x);
        buffer.writeDouble(position.y);
        buffer.writeDouble(position.z);
        buffer.writeDouble(orientation.x);
        buffer.writeDouble(orientation.y);
        buffer.writeDouble(orientation.z);
        buffer.writeDouble(orientation.w);
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        ClientInteractableManager manager = (ClientInteractableManager) InteractableAttachment.getInteractableManagerForLevel(level);
        Interactable interactable = manager.getInteractable(id);
        if (interactable != null) {
            interactable.setPosition(this.position);
            interactable.setOrientation(this.orientation);
        } else {
            // todo: request for interactable creation packet.
        }
    }
}
