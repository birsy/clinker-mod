package birsy.clinker.core.util.rigidbody;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.rigidbody.gjkepa.GJKEPA;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class PhysicsEnvironment {
    public List<IBody> bodies;
    public float gravity;
    public int subSteps;

    public PhysicsEnvironment() {
        this.gravity = 9.81F;
        this.subSteps = 1;
        this.bodies = new ArrayList<>();
    }

    public PhysicsEnvironment(IBody... objects) {
        super();
        this.bodies = Arrays.stream(objects).toList();
    }

    public void tick(float deltaTime) {
        for (IBody body : bodies) {
            if (body instanceof ITickableBody tBody) tBody.tick(deltaTime);
        }
        for (int i = 0; i < subSteps; i++) {
            update(deltaTime / subSteps);
        }
    }

    private void update(float deltaTime) {
        this.gravity = 5.0F;
        for (IBody body : bodies) {
            if (body instanceof IPhysicsBody pBody) {
                pBody.accelerate(new Vec3(0, -this.gravity, 0).scale(deltaTime), pBody.getCenterOfMass());
                pBody.integrate(deltaTime);
            }
        }

        Set<PotentialCollisionPair> potentialCollisions = this.collideBroadPhase();
        Set<CollisionPair> collisions = this.collideNarrowPhase(potentialCollisions);
        this.collisionResponse(collisions);
    }

    private Set<PotentialCollisionPair> collideBroadPhase() {
        Set<PotentialCollisionPair> set = new HashSet<>();

        for (IBody body1 : bodies) {
            if (!(body1 instanceof ICollidable)) continue;

            for (IBody body2 : bodies) {
                if (!(body2 instanceof ICollidable) || body1 == body2) continue;

                AABB shape1 = ((ICollidable) body1).getCollisionShape().getBounds();
                AABB shape2 = ((ICollidable) body2).getCollisionShape().getBounds();

                if (shape1.intersects(shape2)) {
                    set.add(new PotentialCollisionPair((ICollidable) body1, (ICollidable) body2));
                    //Clinker.LOGGER.info("potential collision between " + body1 + " and " + body2);
                }
            }
        }

        return set;
    }

    private Set<CollisionPair> collideNarrowPhase(Set<PotentialCollisionPair> broadPhaseSet) {
        Set<CollisionPair> set = new HashSet<>();

        for (PotentialCollisionPair potentialCollisionPair : broadPhaseSet) {
            GJKEPA.Manifold collisionManifold = GJKEPA.collisionTest(potentialCollisionPair.body1().getCollisionShape(), potentialCollisionPair.body2().getCollisionShape(), 64);
            if (collisionManifold != null) {
                set.add(new CollisionPair(potentialCollisionPair.body1(), potentialCollisionPair.body2(), collisionManifold));
            }
        }

        return set;
    }

    private void collisionResponse(Set<CollisionPair> collisionPairs) {
        //TODO: conservation of momentum
        float inelasticity = 1.0F;

        for (CollisionPair collisionPair : collisionPairs) {
            Vec3 collisionVector = collisionPair.manifold.normal().scale(collisionPair.manifold.depth());

            if (collisionPair.body1() instanceof IPhysicsBody pBody1 && collisionPair.body2() instanceof IPhysicsBody pBody2) {
                float totalMass = (pBody1.mass() + pBody2.mass());
                pBody1.applyImpulse(collisionVector.scale(-pBody1.mass() / totalMass), collisionPair.manifold.contactPointA());
                pBody2.applyImpulse(collisionVector.scale(pBody2.mass() / totalMass), collisionPair.manifold.contactPointB());
            } else if (collisionPair.body1() instanceof IPhysicsBody pBody1) {
                pBody1.applyImpulse(collisionVector.scale(-1.0), collisionPair.manifold.contactPointA());
            } else if (collisionPair.body2() instanceof IPhysicsBody pBody2) {
                pBody2.applyImpulse(collisionVector.scale(1.0), collisionPair.manifold.contactPointB());
            }
        }
    }


    // made this way more complicated than it needs to be because the pair should be unordered :P
    // need this for the sets to work like they should and filter out duplicate collisions
    private record PotentialCollisionPair(Set<ICollidable> set) {
        PotentialCollisionPair(ICollidable body1, ICollidable body2) {
            this(new HashSet<>());
            set.add(body1);
            set.add(body2);
        }
        ICollidable body1() {
            return (ICollidable) set.toArray()[0];
        }
        ICollidable body2() {
            return (ICollidable) set.toArray()[1];
        }
    }
    private record CollisionPair(Set<ICollidable> set, GJKEPA.Manifold manifold) {
        CollisionPair(ICollidable body1, ICollidable body2, GJKEPA.Manifold manifold) {
            this(new HashSet<>(), manifold);
            set.add(body1);
            set.add(body2);
        }
        ICollidable body1() {
            return (ICollidable) set.toArray()[0];
        }
        ICollidable body2() {
            return (ICollidable) set.toArray()[1];
        }
    }
}
