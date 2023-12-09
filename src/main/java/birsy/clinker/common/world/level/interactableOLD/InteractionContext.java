package birsy.clinker.common.world.level.interactableOLD;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * basically just stores a ray.
 * @param from the start position of the ray
 * @param to the end position of the ray
 */
public record InteractionContext(Vec3 from, Vec3 to, @Nullable InteractionHand hand) {
    public CompoundTag serialize() {
        return this.serialize(null);
    }

    public CompoundTag serialize(@Nullable CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();

        tag.putDouble("fromX", from.x());
        tag.putDouble("fromY", from.y());
        tag.putDouble("fromY", from.z());

        tag.putDouble("toX", to.x());
        tag.putDouble("toY", to.y());
        tag.putDouble("toY", to.z());

        tag.putBoolean("hand", hand == InteractionHand.MAIN_HAND);
        return tag;
    }

    public static InteractionContext deserialize(CompoundTag tag) {
        Vec3 from = new Vec3(tag.getDouble("fromX"), tag.getDouble("fromY"), tag.getDouble("fromZ"));
        Vec3 to = new Vec3(tag.getDouble("toX"), tag.getDouble("toY"), tag.getDouble("toZ"));
        InteractionHand hand = tag.getBoolean("hand") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new InteractionContext(from, to, hand);
    }
}
