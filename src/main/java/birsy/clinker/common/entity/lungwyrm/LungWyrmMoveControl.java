package birsy.clinker.common.entity.lungwyrm;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
             movementDirection = new Vector3f(0, 0, 1),
             jointDirection = new Vector3f();

    public LungWyrmMoveControl(LungWyrmEntity mob) {
        super(mob);
        this.wyrm = mob;
    }

    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            tickHeading();
            tickSegmentThrust();
        } else {
            this.speedModifier = Mth.approach((float) this.speedModifier, 0, 0.01F);
        }
        tickSegmentBuoyancy();
    }

    void tickHeading() {
        float targetDistance = 16F;
        if (this.operation == Operation.MOVE_TO) {
            desiredMovementDirection.set(
                    this.wantedX - wyrm.getX(),
                    this.wantedY - wyrm.getY(),
                    this.wantedZ - wyrm.getZ()
            );
            targetDistance = desiredMovementDirection.length();
        }
        if (targetDistance < 2) {
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
                    CollisionContext.of(wyrm)
            ));
            if (raycastResult.getType() != HitResult.Type.BLOCK) break;
        }

        VectorUtils.approachSphere(movementDirection, targetMovementDirection, 0.5F, movementDirection);

        wyrm.getLookControl().setLookAt(
                wyrm.getX() + movementDirection.x,
                wyrm.getEyeY() + movementDirection.y,
                wyrm.getZ() + movementDirection.z
        );
    }

    void tickSegmentThrust() {
        // move each segment
        jointDirection.set(movementDirection);
        float desiredSpeed = (float) this.speedModifier, acceleration = 0.05F, brake = 0.01F;
        for (LungWyrmEntity.LungWyrmJoint joint : wyrm.joints) {
            // only correct along the segment movement direction so that it actually takes knockback
            double along = joint.dX * jointDirection.x + joint.dY * jointDirection.y + joint.dZ * jointDirection.z;

            double step = along < desiredSpeed ? acceleration : brake,
                    correction = Mth.clamp(desiredSpeed - along, -step, step);

            joint.dX += jointDirection.x * correction;
            joint.dY += jointDirection.y * correction;
            joint.dZ += jointDirection.z * correction;

            LungWyrmEntity.LungWyrmJoint next = joint.next();
            if (next != null) {
                double offsetX = joint.x - next.x,
                        offsetY = joint.y - next.y,
                        offsetZ = joint.z - next.z;
                if (offsetX != 0 || offsetY != 0 || offsetZ != 0)
                    jointDirection.set((float) offsetX, (float) offsetY, (float) offsetZ).normalize();
            }
        }
    }

    void tickSegmentBuoyancy() {
        // todo...
    }
}
