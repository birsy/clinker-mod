package birsy.clinker.common.world.level.interactable;

import birsy.clinker.common.world.level.interactable.manager.InteractableManager;
import birsy.clinker.common.world.physics.rigidbody.Transform;
import birsy.clinker.common.world.physics.rigidbody.colliders.OBBCollisionShape;
import birsy.clinker.common.world.physics.rigidbody.gjkepa.GJKEPA;
import birsy.clinker.core.util.VectorUtils;
import birsy.clinker.core.util.collision.GJK;
import birsy.clinker.core.util.collision.colliders.BoxCollider;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles identifying and processing interactions with interactables.
 */
public class InteractionHandler {
    final protected InteractableManager manager;

    public InteractionHandler(InteractableManager manager) {
        this.manager = manager;
    }

    public void tick() {
        for (Interactable interactable : manager.storage.getInteractables()) {
            interactable.colliding = false;
        }

        this.handleCollisions(this.fineCollisionSearch(this.coarseCollisionSearch()));
    }

    private Set<CollisionCandidate> coarseCollisionSearch() {
        Set<CollisionCandidate> potentialCollisions = new HashSet<>();

        for (SectionPos section : this.manager.storage.getSectionsWithInteractables()) {
            Collection<Interactable> interactables = manager.storage.getInteractablesInSection(section);
            for (Entity entity : this.manager.level.getEntities(null, new AABB(section.minBlockX(), section.minBlockY(), section.minBlockZ(), section.maxBlockX(), section.maxBlockY(), section.maxBlockZ()))) {
                for (Interactable interactable : interactables) {
                    if (entity.getBoundingBox().intersects(interactable.getBounds())) potentialCollisions.add(new CollisionCandidate(entity, interactable));
                }
            }
        }

        return potentialCollisions;
    }

    private Set<Collision> fineCollisionSearch(Set<CollisionCandidate> potentialCollisions) {
        Set<Collision> collisions = new HashSet<>();

        for (CollisionCandidate potentialCollision : potentialCollisions) {
            Vec3 entityCenter = potentialCollision.entity.position().add(0, potentialCollision.entity.getBbHeight() * 0.5, 0);
            Vector3d entityBBSize = new Vector3d(potentialCollision.entity.getBbWidth() * 0.5, potentialCollision.entity.getBbHeight() * 0.5, potentialCollision.entity.getBbWidth() * 0.5);
            BoxCollider entityCollider = new BoxCollider(new Vector3d(entityCenter.x, entityCenter.y, entityCenter.z), new Quaterniond(), entityBBSize);

            BoxCollider interactableCollider = potentialCollision.interactable.collider;

            if (GJK.checkCollision(entityCollider, interactableCollider)) collisions.add(new Collision(potentialCollision.entity, potentialCollision.interactable, new Vector3d(), new Vector3d()));
//            OBBCollisionShape entityShape = new OBBCollisionShape(potentialCollision.entity.getBbWidth() * 0.5, potentialCollision.entity.getBbHeight() * 0.5, potentialCollision.entity.getBbWidth() * 0.5);
//            entityShape.setTransform(new Transform(new Vec3(0, 0, 0), new Quaterniond()));
//
//            OBBCollisionShape interactableShape = new OBBCollisionShape(potentialCollision.interactable.size.x() * 0.5, potentialCollision.interactable.size.y() * 0.5, potentialCollision.interactable.size.z() * 0.5);
//            Vector3d iPos = potentialCollision.interactable.getPosition();
//            entityShape.setTransform(new Transform(entityCenter.subtract(iPos.x, iPos.y, iPos.z).scale(-1), potentialCollision.interactable.getOrientation()));
//
//            GJKEPA.Manifold manifold = GJKEPA.collisionTest(entityShape, interactableShape, 32);
//            if (manifold != null) collisions.add(new Collision(potentialCollision.entity, potentialCollision.interactable,
//                    new Vector3d(VectorUtils.toArray(manifold.contactPointA())), new Vector3d(VectorUtils.toArray(manifold.contactPointB()))));
        }

        return collisions;
    }

    private void handleCollisions(Set<Collision> collisions) {
        for (Collision collision : collisions) {
            Interactable interactable = collision.interactable;
            interactable.colliding = true;
        }
    }

    private record CollisionCandidate(Entity entity, Interactable interactable) {}
    private record Collision(Entity entity, Interactable interactable, Vector3d pointA, Vector3d pointB) {}
}
