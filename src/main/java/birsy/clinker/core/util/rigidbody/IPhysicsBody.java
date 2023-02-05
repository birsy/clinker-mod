package birsy.clinker.core.util.rigidbody;

import net.minecraft.world.phys.Vec3;

public interface IPhysicsBody {
    float mass();
    void integrate(float deltaTime);
    void applyImpulse(Vec3 force, Vec3 point);
    void accelerate(Vec3 force, Vec3 point);
    Vec3 getCenterOfMass();
}
