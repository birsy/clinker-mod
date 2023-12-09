package birsy.clinker.common.networking.packet.interactable;

import birsy.clinker.common.networking.packet.ServerboundPacket;
import birsy.clinker.common.world.level.interactableOLD.Interactable;
import birsy.clinker.common.world.level.interactableOLD.InteractableManager;
import birsy.clinker.common.world.level.interactableOLD.InteractionInfo;
import birsy.clinker.core.Clinker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundInteractableInteractionPacket extends ServerboundPacket {
    private InteractionInfo interactionInfo;
    public ServerboundInteractableInteractionPacket(InteractionInfo interactionInfo) {
        this.interactionInfo = interactionInfo;
    }

    public ServerboundInteractableInteractionPacket(FriendlyByteBuf buffer) {
        this.interactionInfo = InteractionInfo.deserialize(buffer.readNbt());
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.interactionInfo.serialize());
    }

    @Override
    public void run(NetworkEvent.Context context) {
        Entity sender = context.getSender();
        InteractableManager manager = InteractableManager.serverInteractableManagers.get(sender.level());
        Interactable interactable = manager.storage.getInteractable(interactionInfo.interactionUUID());

        // uh oh! interactable does not exist.
        if (interactable == null) {
            Clinker.LOGGER.warn("Unknown interaction UUID " + interactionInfo.interactionUUID().toString() + " sent from client " + sender.getStringUUID() + "!");
            return;
        }

        if (isValidInteraction(this.interactionInfo, interactable, sender)) interactable.run(this.interactionInfo, sender, false);
    }

    private static boolean isValidInteraction(InteractionInfo interactionInfo, Interactable interactable, Entity sender) {
        if (sender instanceof ServerPlayer player) {
            // sanity check to ensure that the player can actually interact with the interactable.
            // given a 1.5 block buffer to hopefully resolve any network chicanery.
            // disregard any touch interactions, as those are handled entirely serverside.
            return (interactable.getPosition().distanceTo(player.getEyePosition()) - interactable.shape.getRadius() - 1.5) < player.getBlockReach();
        } else {
            return true;
        }
    }
}
