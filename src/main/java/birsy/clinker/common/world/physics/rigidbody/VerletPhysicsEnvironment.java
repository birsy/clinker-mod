package birsy.clinker.common.world.physics.rigidbody;

import birsy.clinker.common.world.physics.rigidbody.gjkepa.GJKEPA;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class VerletPhysicsEnvironment {
    public List<IBody> bodies;
    public float gravity;
    public int subSteps;

    public VerletPhysicsEnvironment() {
        this.gravity = 2.0F;
        this.subSteps = 2;
        this.bodies = new ArrayList<>();
    }

    public VerletPhysicsEnvironment(IBody... objects) {
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
        Set<PotentialCollisionPair> potentialCollisions = this.collideBroadPhase();
        Set<CollisionPair> collisions = this.collideNarrowPhase(potentialCollisions);
        this.collisionResponse(collisions, deltaTime);

        for (IBody body : bodies) {
            if (body instanceof IPhysicsBody pBody) {
                pBody.accelerate(new Vec3(0, -this.gravity, 0), pBody.getCenterOfMass());

                pBody.integrate(deltaTime);
            }
        }
    }

    private Set<PotentialCollisionPair> collideBroadPhase() {
        Set<PotentialCollisionPair> set = new HashSet<>();

        for (IBody body1 : bodies) {
            if (body1 instanceof ICollidable cb1) {
                for (IBody body2 : bodies) {
                    if (body2 instanceof ICollidable cb2) {
                        if (body1 == body2) continue;
                        double minDist = cb1.getCollisionShape().getRadius() + cb2.getCollisionShape().getRadius();
                        if (body1.getTransform().getPosition().distanceToSqr(body2.getTransform().getPosition()) > minDist * minDist) continue;
                        AABB shape1 = cb1.getCollisionShape().getBounds();
                        AABB shape2 = cb2.getCollisionShape().getBounds();

                        if (shape1.intersects(shape2)) {
                            set.add(new PotentialCollisionPair((ICollidable) body1, (ICollidable) body2));
                        }
                    }

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

    private void collisionResponse(Set<CollisionPair> collisionPairs, float deltaTime) {
        for (CollisionPair collisionPair : collisionPairs) {
            Vec3 collisionVector = collisionPair.manifold.normal().scale(collisionPair.manifold.depth());
            if (collisionPair.body1() instanceof IPhysicsBody pBody1 && collisionPair.body2() instanceof IPhysicsBody pBody2) {
                float totalMass = (pBody1.mass() + pBody2.mass());
                pBody1.applyImpulse(
                        collisionVector.scale(-pBody1.mass() / totalMass),
                        collisionPair.manifold.contactPointA());
                pBody2.applyImpulse(
                        collisionVector.scale( pBody2.mass() / totalMass),
                        collisionPair.manifold.contactPointB());
            } else if (collisionPair.body1() instanceof IPhysicsBody pBody1) {
                pBody1.applyImpulse(
                        collisionVector.scale(-1.0),
                        collisionPair.manifold.contactPointA());
            } else if (collisionPair.body2() instanceof IPhysicsBody pBody2) {
                pBody2.applyImpulse(
                        collisionVector.scale(1.0),
                        collisionPair.manifold.contactPointB());

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
