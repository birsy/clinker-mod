package birsy.clinker.common.world.level.interactableOLD;

import birsy.clinker.core.Clinker;
import birsy.clinker.common.world.physics.rigidbody.Transform;
import birsy.clinker.common.world.physics.rigidbody.colliders.OBBCollisionShape;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class Interactable {
    public OBBCollisionShape shape;
    public Transform previousTransform;
    protected boolean shouldBeRemoved;
    protected boolean shouldUpdateShape;
    public boolean initialized = true;
    public Optional<InteractableParent> parent = Optional.empty();
    public final UUID uuid;
    public boolean hasOutline;

    public Interactable(OBBCollisionShape shape, boolean hasOutline) {
        this.shape = shape;
        this.uuid = UUID.randomUUID();
        this.shouldBeRemoved = false;
        this.previousTransform = this.getTransform().copy();
        this.hasOutline = hasOutline;
    }

    public Interactable(OBBCollisionShape shape, UUID id, boolean hasOutline) {
        this.shape = shape;
        this.uuid = id;
        this.shouldBeRemoved = false;
        this.previousTransform = this.getTransform().copy();
        this.hasOutline = hasOutline;
    }

    public Interactable() {
        this.shape = null;
        this.uuid = null;
    }

    protected void tick(boolean clientside) {
        this.previousTransform = this.getTransform().copy();
    }

    /**
     * Run when an interactable is left-clicked.
     *
     * @param interactionContext The interaction context regarding the player's interaction info.
     * @param entity The entity preforming the interaction.
     * @param clientSide Whether the interaction is being run on the client.
     * @return Whether the interaction is successful. If true, the player's default interaction (placing on the block behind the interactable, etc) will be cancelled.
     */
    public abstract boolean onInteract(InteractionContext interactionContext, @Nullable Entity entity, boolean clientSide);

    /**
     * Run when an interactable is right-clicked.
     *
     * @param interactionContext The interaction context regarding the player's interaction info.
     * @param entity The entity preforming the interaction.
     * @param clientSide Whether the interaction is being run on the client.
     * @return Whether the interaction is successful. If true, the player's default interaction (breaking the block behind the interactable, etc) will be cancelled.
     */
    public abstract boolean onHit(InteractionContext interactionContext, @Nullable Entity entity, boolean clientSide);

    /**
     * Run when the pick-block key is used on an interactable.
     *
     * @param interactionContext The interaction context regarding the player's interaction info.
     * @param entity The entity preforming the interaction.
     * @param clientSide Whether the interaction is being run on the client.
     * @return Whether the interaction is successful. If true, the player's default interaction (picking the block behind the interactable, etc) will be cancelled.
     */
    public abstract boolean onPick(InteractionContext interactionContext, @Nullable Entity entity, boolean clientSide);

    /**
     * Run when an entity's hitbox collides with the interactable.
     *
     * @param touchingEntity The entity that is colliding with the interactable.
     * @param clientSide Whether the interaction is being run on the client.
     * @return Whether the interaction is successful.
     */
    public abstract boolean onTouch(InteractionContext interactionContext, Entity touchingEntity, boolean clientSide);

    public boolean run(InteractionInfo info, @Nullable Entity entity, boolean clientSide) {
        switch (info.interaction()) {
            case INTERACT: return this.onInteract(info.context(), entity, clientSide);
            case HIT: return this.onHit(info.context(), entity, clientSide);
            case PICK: return this.onPick(info.context(), entity, clientSide);
            case TOUCH: return this.onTouch(info.context(), entity, clientSide);
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
     * Gets the position of the interactable.
     *
     * @return the position of the interactable
     */
    public Vec3 getPosition() {
        return this.getTransform().getPosition();
    }

    /**
     * Gets the section position of the interactable.
     *
     * @return the section position of the interactable
     */
    public SectionPos getSectionPosition() {
        return SectionPos.of(this.getTransform().getPosition());
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
    public void setRotation(Quaterniond rotation) {
        this.getTransform().setOrientation(rotation);
    }

    /**
     * Marks an interactable to be removed next tick.
     */
    public void markForRemoval() {
        this.shouldBeRemoved = true;
    }

    /**
     * Replicates changes to the interactable's shape across the server-client boundary.
     */
    public void updateShape() {
        shouldUpdateShape = true;
    }

    public void setParent(InteractableParent parent) {
        this.parent = Optional.of(parent);
    }

    public boolean isParented() {
        return !this.parent.isEmpty();
    }

    public CompoundTag serialize() {
        return this.serialize(null);
    }

    public CompoundTag serialize(@Nullable CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        tag.putString("name", this.getClass().getName());
        tag.putUUID("uuid", this.uuid);
        tag.putBoolean("outline", this.hasOutline);
        this.shape.serialize(tag);

        return tag;
    }

    public <I extends Interactable> I reconstructOnClient(CompoundTag tag) {
        return (I) new ClientDummyInteractable((OBBCollisionShape) (new OBBCollisionShape(0, 0, 0).deserialize(tag)), tag.getUUID("uuid"), tag.getBoolean("outline"));
    }

    /*
     * TODO: i was a fucking idiot, this could just be done with a constructor.
     *  do that.
     */
    @Nullable
    public static <I extends Interactable> I deserialize(CompoundTag tag) {
        // deranged reflection bullshit !!!
        try {
            Class clazz =  Class.forName(tag.getString("name"));
            Object dummy = clazz.getConstructor().newInstance();
            Clinker.LOGGER.info(clazz);
            Clinker.LOGGER.info(clazz.getMethod("reconstructOnClient", CompoundTag.class));
            return (I) clazz.getMethod("reconstructOnClient", CompoundTag.class).invoke(dummy, tag);
        } catch (Exception e) {
            Clinker.LOGGER.warn("Deserialization of Interactable " + tag.getUUID("uuid") + " failed!");
            Clinker.LOGGER.warn(e.getMessage());
        }

        return null;
    }
}