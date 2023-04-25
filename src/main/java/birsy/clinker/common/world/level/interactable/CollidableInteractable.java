package birsy.clinker.common.world.level.interactable;

import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CollidableInteractable extends Interactable {
    private double mass;
    private boolean reacts;

    public CollidableInteractable(OBBCollisionShape shape, boolean reacts, double mass) {
        super(shape);
        this.mass = mass;
        this.reacts = reacts;
    }

    public CollidableInteractable(OBBCollisionShape shape, boolean reacts, double mass, UUID uuid) {
        super(shape, uuid);
        this.mass = mass;
        this.reacts = reacts;
    }

    @Override
    public boolean onInteract(InteractionContext interactionContext, @Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean onHit(InteractionContext interactionContext, @Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean onPick(InteractionContext interactionContext, @Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean onTouch(InteractionContext context, Entity touchingEntity) {
//        if (!this.reacts) {
//            Vec3 collisionVector = context.from().subtract(context.to());
//            touchingEntity.push(collisionVector.x, collisionVector.y, collisionVector.z);
//            return true;
//        }
//        double entityMass = touchingEntity.getBbHeight() * touchingEntity.getBbWidth() * touchingEntity.getBbWidth();
//        double totalMass = entityMass + this.mass;
//
//        double collisionDepth = context.from().distanceTo(context.to());
//        Vec3 collisionDirection = context.from().subtract(context.to()).normalize();
//
//        Vec3 entityMovement = collisionDirection.scale(collisionDepth * (this.mass / totalMass));
//        Vec3 thisMovement = collisionDirection.scale(-collisionDepth * (entityMass / totalMass));
//
//        touchingEntity.push(entityMovement.x, entityMovement.y, entityMovement.z);
//        this.push(thisMovement);
        return true;
    }
    
    public void push(Vec3 movement) {
        if (this.reacts) this.move(movement);
    }

    @Override
    public CompoundTag serialize(@Nullable CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        tag.putUUID("uuid", this.uuid);
        tag.putDouble("mass", this.mass);
        tag.putBoolean("reacts", this.reacts);
        this.shape.serialize(tag);

        return tag;
    }

//    @Override
//    public <I extends Interactable> I reconstructOnClient(CompoundTag tag) {
//        return (I) new CollidableInteractable((OBBCollisionShape) new OBBCollisionShape(0, 0, 0).deserialize(tag), tag.getBoolean("reacts"), tag.getDouble("mass"), tag.getUUID("uuid"));
//    }
}
