package birsy.clinker.core.util.rigidbody;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.rigidbody.colliders.ICollisionShape;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;

public class ColliderBody implements ICollidable, IBody {
    public Transform transform;
    public ICollisionShape collisionShape;

    public ColliderBody(Vec3 initialPosition, Quaternion initialOrientation, ICollisionShape collisionShape) {
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
