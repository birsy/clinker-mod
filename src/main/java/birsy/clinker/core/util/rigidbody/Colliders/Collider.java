package birsy.clinker.core.util.rigidbody.Colliders;

import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;

public abstract class Collider {
    public Vec3 position;
    public Quaternion orientation;

    public Collider(Vec3 position, Quaternion orientation) {
        this.position = position;
        this.orientation = orientation;
    }

    public Collider(Vec3 position) {
        this.position = position;
        this.orientation = Quaternion.ONE;
    }

    public abstract boolean intersects(BoxCollider collider);
    public abstract boolean intersects(SphereCollider collider);

}
