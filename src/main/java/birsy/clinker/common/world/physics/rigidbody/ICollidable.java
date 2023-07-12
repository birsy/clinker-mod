package birsy.clinker.common.world.physics.rigidbody;

import birsy.clinker.common.world.physics.rigidbody.colliders.ICollisionShape;

public interface ICollidable {
    ICollisionShape getCollisionShape();
}
