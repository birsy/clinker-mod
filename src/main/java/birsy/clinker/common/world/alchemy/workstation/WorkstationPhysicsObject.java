package birsy.clinker.common.world.alchemy.workstation;

import birsy.clinker.common.world.physics.rigidbody.colliders.ICollisionShape;
import birsy.clinker.core.util.Quaterniond;
import net.minecraft.world.phys.Vec3;

public class WorkstationPhysicsObject {
    public Quaterniond rotation, pRotation;
    public Vec3 position, pPosition;
    public ICollisionShape shape;

    public WorkstationPhysicsObject(Vec3 position, ICollisionShape shape) {
        this.position = position;
        this.pPosition = position;
        this.rotation = new Quaterniond(new Vec3(0, 1, 0), Math.PI / 6);
        this.pRotation = this.rotation.clone();

        this.shape = shape;
    }

    public void tick() {}

    public Vec3 getPosition(float partialTick) {
        return pPosition.lerp(position, partialTick);
    }

    public Quaterniond getRotation(float partialTick) {
        return pRotation.slerp(rotation, partialTick);
    }
}
