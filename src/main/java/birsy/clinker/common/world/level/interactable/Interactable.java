package birsy.clinker.common.world.level.interactable;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.core.util.rigidbody.Transform;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import com.mojang.math.Quaternion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.StringJoiner;
import java.util.UUID;

public abstract class Interactable {
    public final OBBCollisionShape shape;
    public Transform previousTransform;
    protected boolean shouldBeRemoved;
    public boolean parented;
    public final UUID uuid;

    public Interactable(OBBCollisionShape shape) {
        this.shape = shape;
        this.uuid = UUID.randomUUID();
        this.shouldBeRemoved = false;
        this.previousTransform = this.getTransform().copy();
    }

    public Interactable(OBBCollisionShape shape, UUID id) {
        this.shape = shape;
        this.uuid = id;
        this.shouldBeRemoved = false;
        this.previousTransform = this.getTransform().copy();
    }

    public Interactable() {
        this.shape = null;
        this.uuid = null;
    }

    protected void tick() {
        this.previousTransform = this.getTransform().copy();
    }

    /**
     * Run when an interactable is left-clicked.
     *
     * @param interactionContext The interaction context regarding the player's interaction info.
     * @param entity The entity preforming the interaction.
     * @return Whether the interaction is successful. If true, the player's default interaction (placing on the block behind the interactable, etc) will be cancelled.
     */
    public abstract boolean onInteract(InteractionContext interactionContext, @Nullable Entity entity);

    /**
     * Run when an interactable is right-clicked.
     * @param interactionContext The interaction context regarding the player's interaction info.
     * @param entity The entity preforming the interaction.
     * @return Whether the interaction is successful. If true, the player's default interaction (breaking the block behind the interactable, etc) will be cancelled.
     */
    public abstract boolean onHit(InteractionContext interactionContext, @Nullable Entity entity);

    /**
     * Run when the pick-block key is used on an interactable.
     *
     * @param interactionContext The interaction context regarding the player's interaction info.
     * @param entity The entity preforming the interaction.
     * @return Whether the interaction is successful. If true, the player's default interaction (picking the block behind the interactable, etc) will be cancelled.
     */
    public abstract boolean onPick(InteractionContext interactionContext, @Nullable Entity entity);

    /**
     * Run when an entity's hitbox collides with the interactable.
     *
     * @param touchingEntity The entity that is colliding with the interactable.
     * @return Whether the interaction is successful.
     */
    public abstract boolean onTouch(InteractionContext interactionContext, Entity touchingEntity);

    public boolean run(InteractionInfo info, @Nullable Entity entity) {
        switch (info.interaction()) {
            case INTERACT: return this.onInteract(info.context(), entity);
            case HIT: return this.onHit(info.context(), entity);
            case PICK: return this.onPick(info.context(), entity);
            case TOUCH: return this.onTouch(info.context(), entity);
        }
        return false;
    }

    /**
     * Gets the transform of the interactable, used for rotations and translations.
     *
     * @return the transform of the interactable
     */
    public Transform getTransform() {
        return this.shape.getTransform();
    }

    /**
     * Sets the transform of the interactable, used for rotations and translations.
     *
     * @param transform the new transform
     */
    public void setTransform(Transform transform) {
        this.shape.setTransform(transform);
    }

    /**
     * Sets the position of the interactable.
     *
     * @param position the new position.
     */
    public void setPosition(Vec3 position) {
        this.getTransform().setPosition(position);
    }

    /**
     * Moves the interactable by the specified amount for each direction.
     *
     * @param amount the amount to move
     */
    public void move(Vec3 amount) {
        this.getTransform().addPosition(amount);
    }

    /**
     * Sets the rotation of the interactable.
     *
     * @param rotation the new rotation.
     */
    public void setRotation(Quaternion rotation) {
        this.getTransform().setOrientation(new Quaterniond(rotation));
    }

    /**
     * Sets the rotation of the interactable.
     *
     * @param rotation the new rotation.
     */
    public void setRotation(Quaterniond rotation) {
        this.getTransform().setOrientation(rotation);
    }

    /**
     * Rotates the interactable according to the quaternion.
     *
     * @param rotation the rotation to apply.
     */
    public void rotate(Quaternion rotation) {
        this.getTransform().rotate(new Quaterniond(rotation));
    }

    /**
     * Marks an interactable to be removed next tick.
     */
    public void markForRemoval() {
        this.shouldBeRemoved = true;
    }

    public CompoundTag serialize() {
        return this.serialize(null);
    }

    public CompoundTag serialize(@Nullable CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        tag.putString("name", this.getClass().getName());
        Clinker.LOGGER.info("serializing - serverside!");
        Clinker.LOGGER.info(this.getClass().getName());
        tag.putUUID("uuid", this.uuid);
        this.shape.serialize(tag);

        return tag;
    }

    public <I extends Interactable> I reconstructOnClient(CompoundTag tag) {
        return (I) new ClientDummyInteractable((OBBCollisionShape) (new OBBCollisionShape(0, 0, 0).deserialize(tag)), tag.getUUID("uuid"));
    }

    @Nullable
    public static <I extends Interactable> I deserialize(CompoundTag tag) {
        // deranged reflection bullshit !!!
        try {
            Class clazz =  Class.forName(tag.getString("name"));
            Object dummy = clazz.getConstructor().newInstance();
            return (I) clazz.getMethod("reconstructOnClient", CompoundTag.class).invoke(dummy, tag);
        } catch (Exception e) {
            Clinker.LOGGER.warn("Deserialization of Interactable " + tag.getUUID("uuid") + " failed!");
            Clinker.LOGGER.warn(e);
        }

        return null;
    }
}