package birsy.clinker.common.networking.packet;

import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.common.world.level.interactable.InteractableManager;
import birsy.clinker.common.world.level.interactable.InteractionInfo;
import birsy.clinker.core.Clinker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
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
        InteractableManager manager = InteractableManager.serverInteractableManagers.get(sender.getLevel());

        // uh oh! interactable does not exist.
        if (!manager.interactableMap.containsKey(interactionInfo.interactionUUID())) {
            Clinker.LOGGER.warn("Unknown interaction UUID " + interactionInfo.interactionUUID().toString() + " sent from client " + sender.getStringUUID() + "!");
            return;
        }

        Interactable interactable = manager.interactableMap.get(interactionInfo.interactionUUID());
        if (isValidInteraction(this.interactionInfo, interactable, sender)) interactable.run(this.interactionInfo, sender);
    }

    private static boolean isValidInteraction(InteractionInfo interactionInfo, Interactable interactable, Entity sender) {
        if (sender instanceof ServerPlayer player) {
            // sanity check to ensure that the player can actually interact with the interactable.
            // given a 1.5 block buffer to hopefully resolve any network chicanery.
            // disregard any touch interactions, as those are handled entirely serverside.
            Vec3 to = interactionInfo.context().to().subtract(player.getEyePosition()).normalize().scale(player.getReachDistance()).add(player.getEyePosition());
            return interactable.shape.raycast(player.getEyePosition(), to, 1.5).isPresent() && interactionInfo.interaction() != InteractionInfo.Interaction.TOUCH;
        } else {
            return true;
        }
    }
}
