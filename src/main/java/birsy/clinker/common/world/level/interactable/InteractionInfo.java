package birsy.clinker.common.world.level.interactable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ClipContext;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * stores info from an interaction to be sent across the server-client boundary.
 * @param interactionUUID
 * @param interaction
 * @param context
 */
public record InteractionInfo(UUID interactionUUID, Interaction interaction, InteractionContext context) {
    public CompoundTag serialize() {
        return this.serialize(null);
    }

    public CompoundTag serialize(@Nullable CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        tag.putUUID("uuid", interactionUUID);
        tag.putByte("interaction", interaction.getId());
        context.serialize(tag);
        return tag;
    }

    public static InteractionInfo deserialize(CompoundTag tag) {
        UUID uuid = tag.getUUID("uuid");
        Interaction inter = Interaction.fromID(tag.getByte("interaction"));
        InteractionContext interactionContext = InteractionContext.deserialize(tag);
        return new InteractionInfo(uuid, inter, interactionContext);
    }
    public enum Interaction {
        HIT((byte) 0), INTERACT((byte) 1), PICK((byte) 2), TOUCH((byte) 3);

        public static Interaction[] interactions = {HIT, INTERACT, PICK, TOUCH};
        private byte id;

        Interaction(byte id) {
            this.id = id;
        }

        public byte getId() {
            return id;
        }

        public static Interaction fromID(byte id) {
            return interactions[id];
        }
    }
}
