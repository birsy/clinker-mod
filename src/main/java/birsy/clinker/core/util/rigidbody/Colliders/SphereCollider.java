package birsy.clinker.core.util.rigidbody.Colliders;

import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;

public class SphereCollider extends Collider {
    public float radius;

    public SphereCollider(Vec3 position, Quaternion orientation, float radius) {
        super(position, orientation);
        radius = radius;
    }

    public SphereCollider(Vec3 position, float radius) {
        super(position);
        radius = radius;
    }

    @Override
    public boolean intersects(BoxCollider collider) {
        return Collisions.BoxSphereIntersection(collider, this);
    }

    @Override
    public boolean intersects(SphereCollider collider) {
        return Collisions.SphereSphereIntersection(this, collider);
    }
}
