package birsy.clinker.common.world.level.interactable;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ServerboundInteractableInteractionPacket;
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

    public CollidableInteractable(OBBCollisionShape shape, boolean reacts, double mass, boolean outline) {
        super(shape, outline);
        this.mass = mass;
        this.reacts = reacts;
    }

    public CollidableInteractable(OBBCollisionShape shape, boolean reacts, double mass, UUID uuid, boolean outline) {
        super(shape, uuid, outline);
        this.mass = mass;
        this.reacts = reacts;
    }

    public CollidableInteractable() {
        super();
    }

    @Override
    public boolean onInteract(InteractionContext interactionContext, @javax.annotation.Nullable Entity entity, boolean clientSide) {
        if (clientSide) ClinkerPacketHandler.NETWORK.sendToServer(new ServerboundInteractableInteractionPacket(new InteractionInfo(this.uuid, InteractionInfo.Interaction.INTERACT, interactionContext)));
        return true;
    }

    @Override
    public boolean onHit(InteractionContext interactionContext, @javax.annotation.Nullable Entity entity, boolean clientSide) {
        if (clientSide) ClinkerPacketHandler.NETWORK.sendToServer(new ServerboundInteractableInteractionPacket(new InteractionInfo(this.uuid, InteractionInfo.Interaction.HIT, interactionContext)));
        return true;
    }

    @Override
    public boolean onPick(InteractionContext interactionContext, @javax.annotation.Nullable Entity entity, boolean clientSide) {
        if (clientSide) ClinkerPacketHandler.NETWORK.sendToServer(new ServerboundInteractableInteractionPacket(new InteractionInfo(this.uuid, InteractionInfo.Interaction.PICK, interactionContext)));
        return true;
    }

    @Override
    public boolean onTouch(InteractionContext context, Entity touchingEntity, boolean clientSide) {
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
        Collection<Interactable> interactables = manager.storage.getInteractablesInBounds(entity.getBoundingBox().inflate(1.0));
        interactables = interactables.stream().filter(interactable -> {
            boolean isEntityParent = false;
            if (!interactable.parent.isEmpty()) if (interactable.parent.get() == entity) isEntityParent = true;
            return !interactable.shouldBeRemoved && !isEntityParent && (interactable instanceof CollidableInteractable);
        }).toList();

        // todo: issue with walking along borders? get ryan to help...
        
        boolean changed = false;
        for (int iteration = 0; iteration < 16; iteration++) {
            for (Interactable interactable : interactables) {
                if (interactable instanceof CollidableInteractable cI) {
                    AABB entityAABB = entity.getBoundingBox().move(velocityVector);
                    Vec3 center = entityAABB.getCenter();
                    OBBCollisionShape entityBB = new OBBCollisionShape(entityAABB.maxX - center.x(), entityAABB.maxY - center.y(), entityAABB.maxZ - center.z());
                    entityBB.transform.setPosition(center);

                    GJKEPA.Manifold m = GJKEPA.collisionTest(cI.shape, entityBB, 256);
                    if (m != null) {
                        if (!cI.reacts) {
                            velocityVector = velocityVector.add(m.normal().scale(m.depth()));
                        } else {
                            double interactableMass = 8.0;
                            double entityMass = entity.getBbHeight() * entity.getBbWidth() * entity.getBbWidth();
                            double totalMass = entityMass + interactableMass;

                            double collisionDepth = m.depth();
                            Vec3 collisionDirection = m.normal();

                            Vec3 entityMovement = collisionDirection.scale(collisionDepth * (interactableMass / totalMass));
                            Vec3 thisMovement = collisionDirection.scale(-collisionDepth * (entityMass / totalMass));

                            velocityVector = velocityVector.add(entityMovement);
                            cI.push(thisMovement);
                        }
                        changed = true;
                    }
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
        tag.putBoolean("outline", this.hasOutline);
        tag.putString("name", this.getClass().getName());
        this.shape.serialize(tag);

        return tag;
    }

    @Override
    public <I extends Interactable> I reconstructOnClient(CompoundTag tag) {
        return (I) new CollidableInteractable((OBBCollisionShape) new OBBCollisionShape(0, 0, 0).deserialize(tag), tag.getBoolean("reacts"), tag.getDouble("mass"), tag.getUUID("uuid"), tag.getBoolean("outline"));
    }
}
