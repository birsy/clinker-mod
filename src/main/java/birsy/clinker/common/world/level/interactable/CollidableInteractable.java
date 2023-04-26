package birsy.clinker.common.world.level.interactable;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import birsy.clinker.core.util.rigidbody.gjkepa.GJKEPA;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public CollidableInteractable() {
        super();
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

    public static Vec3 collideWithEntities(Vec3 velocityVector, Entity entity) {
        InteractableManager manager = entity.getLevel().isClientSide ? InteractableManager.clientInteractableManager : InteractableManager.serverInteractableManagers.get(entity.getLevel());
        Collection<Interactable> interactables = manager.interactableMap.values();
        // this wasnt working so i yeeted it
        // todo: actually get the interactables in your particular chunk(s) instead of just looping through all of them.
        //  + issue with walking along borders? get ryan to help...
        //interactables.addAll(manager.getInteractablesInChunk(new ChunkPos((int) Math.floor(entity.getX() / 16.0D), (int) Math.floor(entity.getZ() / 16.0D))));
        //interactables.addAll(manager.getInteractablesInChunk(new ChunkPos((int) Math.ceil(entity.getX() / 16.0D),  (int) Math.floor(entity.getZ() / 16.0D))));
        //interactables.addAll(manager.getInteractablesInChunk(new ChunkPos((int) Math.floor(entity.getX() / 16.0D), (int) Math.ceil(entity.getZ() / 16.0D))));
        //interactables.addAll(manager.getInteractablesInChunk(new ChunkPos((int) Math.ceil(entity.getX() / 16.0D), ( int) Math.ceil(entity.getZ() / 16.0D))));

        boolean changed = false;
        for (Interactable interactable : interactables) {
            if (interactable instanceof CollidableInteractable cI) {
                AABB entityAABB = entity.getBoundingBox().move(velocityVector);
                Vec3 center = entityAABB.getCenter();
                OBBCollisionShape entityBB = new OBBCollisionShape(entityAABB.maxX - center.x(), entityAABB.maxY - center.y(), entityAABB.maxZ - center.z());
                entityBB.transform.setPosition(center);

                GJKEPA.Manifold m = GJKEPA.collisionTest(cI.shape, entityBB, 16);
                if (m != null) {
                    velocityVector = velocityVector.add(m.normal().scale(m.depth()));
                    changed = true;
                }
            }
        }

        return changed ? velocityVector : null;
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
        tag.putString("name", this.getClass().getName());
        this.shape.serialize(tag);

        return tag;
    }

    @Override
    public <I extends Interactable> I reconstructOnClient(CompoundTag tag) {
        return (I) new CollidableInteractable((OBBCollisionShape) new OBBCollisionShape(0, 0, 0).deserialize(tag), tag.getBoolean("reacts"), tag.getDouble("mass"), tag.getUUID("uuid"));
    }
}
