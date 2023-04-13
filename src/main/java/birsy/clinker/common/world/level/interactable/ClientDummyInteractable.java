package birsy.clinker.common.world.level.interactable;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ServerboundInteractableInteractionPacket;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Dummy interactable, used to take inputs from the client for processing on the server.
 */
public class ClientDummyInteractable extends Interactable {
    public ClientDummyInteractable(OBBCollisionShape shape, UUID id) {
        super(shape, id);
    }

    @Override
    public boolean onInteract(InteractionContext interactionContext, @Nullable Entity entity) {
        ClinkerPacketHandler.NETWORK.sendToServer(new ServerboundInteractableInteractionPacket(new InteractionInfo(this.uuid, InteractionInfo.Interaction.INTERACT, interactionContext)));
        return true;
    }

    @Override
    public boolean onHit(InteractionContext interactionContext, @Nullable Entity entity) {
        ClinkerPacketHandler.NETWORK.sendToServer(new ServerboundInteractableInteractionPacket(new InteractionInfo(this.uuid, InteractionInfo.Interaction.HIT, interactionContext)));
        return true;
    }

    @Override
    public boolean onPick(InteractionContext interactionContext, @Nullable Entity entity) {
        ClinkerPacketHandler.NETWORK.sendToServer(new ServerboundInteractableInteractionPacket(new InteractionInfo(this.uuid, InteractionInfo.Interaction.PICK, interactionContext)));
        return true;
    }

    //this never runs
    public boolean onTouch(Entity touchingEntity) {
        return false;
    }
}
