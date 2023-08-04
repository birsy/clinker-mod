package birsy.clinker.common.world.physics.rigidbody;

import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.common.world.physics.rigidbody.colliders.ICollisionShape;
import net.minecraft.world.phys.Vec3;

public class ColliderBody implements ICollidable, IBody {
    public Transform transform;
    public ICollisionShape collisionShape;

    public ColliderBody(Vec3 initialPosition, Quaterniond initialOrientation, ICollisionShape collisionShape) {
        this.transform = new Transform(initialPosition, initialOrientation);
        this.collisionShape = collisionShape;
        this.collisionShape.setTransform(this.transform);
    }

    public ICollisionShape getCollisionShape() {
        return this.collisionShape;
    }

    public Transform getTransform() {
        return this.transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}