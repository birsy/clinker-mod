package birsy.clinker.common.entity.lungwyrm;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LungWyrmMoveControl extends MoveControl {
    private static final Vector3f[] SEARCH_DIRECTIONS = MathUtils.generateSpherePointsF(32);
    final LungWyrmEntity wyrm;
    final Quaternionf orientation = new Quaternionf();
    final Vector3f desiredMovementDirection = new Vector3f(0, 0, 1),
             targetMovementDirection = new Vector3f(0, 0, 1),
             movementDirection = new Vector3f(0, 0, 1);

    public LungWyrmMoveControl(LungWyrmEntity mob) {
        super(mob);
        this.wyrm = mob;
    }

    @Override
    public void tick() {
        float targetDistance = 16F;
        // work out movement direction
        if (this.operation == Operation.MOVE_TO) {
            desiredMovementDirection.set(
                    this.wantedX - wyrm.getX(),
                    this.wantedY - wyrm.getY(),
                    this.wantedZ - wyrm.getZ()
            );
            targetDistance = desiredMovementDirection.length();
        }
        if (desiredMovementDirection.lengthSquared() < 0.00001) {
            this.operation = Operation.WAIT;
            return;
        }
        desiredMovementDirection.normalize();

        float searchDistance = Math.min(targetDistance, 16.0F);
        orientation.rotationTo(
                0, 0, 1,
                desiredMovementDirection.x, desiredMovementDirection.y, desiredMovementDirection.z
        );
        for (Vector3f searchDirection : SEARCH_DIRECTIONS) {
            orientation.transform(searchDirection, targetMovementDirection);
            BlockHitResult raycastResult = wyrm.level().clip(new ClipContext(
                    wyrm.position(),
                    new Vec3(wyrm.getX() + targetMovementDirection.x() * searchDistance,
                             wyrm.getY() + targetMovementDirection.y() * searchDistance,
                             wyrm.getZ() + targetMovementDirection.z() * searchDistance),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.ANY,
                    CollisionContext.empty()
            ));
            if (raycastResult.getType() != HitResult.Type.BLOCK) break;
        }

        VectorUtils.approachSphere(movementDirection, targetMovementDirection, 1, movementDirection);

        // move each segment
        float desiredSpeed = (float) this.speedModifier, acceleration = 0.2F;
        for (LungWyrmEntity.LungWyrmJoint joint : wyrm.joints) {
            float desiredVelocityX = movementDirection.x * desiredSpeed,
                  desiredVelocityY = movementDirection.y * desiredSpeed,
                  desiredVelocityZ = movementDirection.z * desiredSpeed;
            joint.dX = Mth.approach((float) joint.dX, desiredVelocityX, acceleration);
            joint.dY = Mth.approach((float) joint.dY, desiredVelocityY, acceleration);
            joint.dZ = Mth.approach((float) joint.dZ, desiredVelocityZ, acceleration);

            LungWyrmEntity.LungWyrmJoint next = joint.next();
            if (next != null) {
                double offsetX = joint.x - next.x, offsetY = joint.y - next.y, offsetZ = joint.z - next.z;
                if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
                    movementDirection.set(
                            joint.x - next.x,
                            joint.y - next.y,
                            joint.z - next.z
                    ).normalize();
                }
            }
        }
    }
}
