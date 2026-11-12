package birsy.clinker.common.entity.lungwyrm;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.VectorUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public class LungWyrmMoveControl extends MoveControl {
    private static final Vector3f[] SEARCH_DIRECTIONS = MathUtils.generateSpherePointsF(32);
    private static final double BLOCKS_PER_PATH_SEGMENT = 5.0;
    private static final int DROOP_RADIUS = 2;
    private static final double[] DROOP_KERNEL = new double[DROOP_RADIUS + 1];
    static { for (int k = 0; k <= DROOP_RADIUS; k++) DROOP_KERNEL[k] = 0.5 + 0.5 * Math.cos(Math.PI * k / (DROOP_RADIUS + 1)); }

    final LungWyrmEntity wyrm;
    final Quaternionf orientation = new Quaternionf();
    final Vector3f desiredMovementDirection = new Vector3f(0, 0, 1),
             targetMovementDirection = new Vector3f(0, 0, 1),
             movementDirection = new Vector3f(0, 0, 1),
             jointDirection = new Vector3f();

    private double[] droop = new double[0];

    // weird ring buffer stuff
    double[] path; int pathStart;
    double pathLength, headY, pHeadY, distanceSinceLastUpdate;

    public LungWyrmMoveControl(LungWyrmEntity mob) {
        super(mob);
        this.wyrm = mob;
    }

    @Override
    public void tick() {
        double totalLength = 0;
        for (LungWyrmEntity.LungWyrmJoint joint : this.wyrm.joints) totalLength += joint.length;
        if (path == null || pathLength != totalLength) initPath(totalLength);

        if (this.operation == Operation.MOVE_TO) {
            tickHeading();
            tickSegmentThrust();
        } else {
            this.speedModifier = Mth.approach((float) this.speedModifier, 0, 0.01F);
            this.wantedY = headY;
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
        jointDirection.set(movementDirection).mul(1, 0, 1);
        float desiredSpeed = (float) (wyrm.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.speedModifier),
              acceleration = 0.05F,
              brake = 0.01F;
        for (LungWyrmEntity.LungWyrmJoint joint : wyrm.joints) {
            // only correct along the segment movement direction so that it actually takes knockback
            double along = joint.dX * jointDirection.x + joint.dZ * jointDirection.z;

            double step = along < desiredSpeed ? acceleration : brake,
                    correction = Mth.clamp(desiredSpeed - along, -step, step);

            joint.dX += jointDirection.x * correction;
            joint.dZ += jointDirection.z * correction;

            LungWyrmEntity.LungWyrmJoint next = joint.next();
            if (next != null) {
                double offsetX = joint.x - next.x,
                       offsetZ = joint.z - next.z;
                if (offsetX != 0 || offsetZ != 0)
                    jointDirection.set((float) offsetX, 0, (float) offsetZ).normalize();
            }
        }
    }

    void tickSegmentBuoyancy() {
        updateDroop();

        double waveOffset = wyrm.tickCount / 120.0;
        updatePath(wyrm.joints.getFirst().y - wave(waveOffset, 0));

        double gravity = wyrm.getGravity() * 0.2;
        double tolerance = 5;

        double runningLength = 0;
        for (int i = 0; i < wyrm.joints.size(); i++) {
            LungWyrmEntity.LungWyrmJoint joint = wyrm.joints.get(i);

            // work out the desired y on the path
            double desiredY = i == 0 ? wantedY : samplePath(runningLength);
            desiredY += wave(waveOffset, runningLength);

            if (joint.groundY + tolerance < joint.ceilingY - tolerance) {
                desiredY = Math.clamp(desiredY, joint.groundY + tolerance, joint.ceilingY - tolerance);
            } else {
                desiredY = joint.groundY < joint.ceilingY ? joint.groundY * 0.5 + joint.ceilingY * 0.5 : desiredY;
            }

            desiredY -= droop[i] * 6;

            double acceleration = (desiredY - joint.y) * 0.05;
            acceleration = Math.clamp(acceleration, -gravity, gravity);
            joint.dY += acceleration;
            joint.inflated = acceleration > 0 && !joint.disabled;

            runningLength += joint.length;
        }
    }

    void updateDroop() {
        if (droop.length != wyrm.joints.size())
            droop = new double[wyrm.joints.size()];
        Arrays.fill(droop, 0);

        for (int j = 0; j < wyrm.joints.size(); j++) {
            if (j == 0 || !wyrm.joints.get(j).disabled) continue;
            for (int k = -DROOP_RADIUS; k <= DROOP_RADIUS; k++) {
                int i = j + k;
                if (i < 0 || i >= wyrm.joints.size()) continue;
                droop[i] += DROOP_KERNEL[Math.abs(k)];
            }
        }
        for (int i = 0; i < wyrm.joints.size(); i++)
            droop[i] = Math.clamp(droop[i], 0, 1);
    }

    void initPath(double totalJointsLength) {
        this.pathLength = totalJointsLength;
        pathStart = 0;
        distanceSinceLastUpdate = 0;
        int pathSize = (int) Math.ceil(totalJointsLength / BLOCKS_PER_PATH_SEGMENT) + 2;
        path = new double[pathSize];

        LungWyrmEntity.LungWyrmJoint head = wyrm.joints.getFirst();
        path[0] = head.y;
        headY = head.y;
        int jointIndex = 0;
        double runningLength = 0, lastRunningLength = 0;
        for (int i = 1; i < path.length; i++) {
            double targetLength = i * BLOCKS_PER_PATH_SEGMENT;

            if (targetLength < totalJointsLength && wyrm.joints.size() > 1) {
                while (runningLength < targetLength && jointIndex < wyrm.joints.size() - 1) {
                    lastRunningLength = runningLength;
                    runningLength += wyrm.joints.get(jointIndex).length;
                    jointIndex++;
                }
                LungWyrmEntity.LungWyrmJoint a = wyrm.joints.get(jointIndex - 1),
                        b = wyrm.joints.get(jointIndex - 0);
                double t = Mth.clamp(Mth.inverseLerp(targetLength, lastRunningLength, runningLength), 0, 1);
                path[i] = Mth.lerp(t, a.y, b.y);
            } else {
                path[i] = wyrm.joints.getLast().y;
            }
        }
    }

    void updatePath(double headY) {
        LungWyrmEntity.LungWyrmJoint head = wyrm.joints.getFirst();

        this.pHeadY = this.headY;
        this.headY = headY;
        double step = Mth.length(head.x - head.pX, this.headY - this.pHeadY, head.z - head.pZ);

        if (step > pathLength * 0.5) {
            // moved more than one segment in spacing, so we should just totally rebuild the path
            initPath(pathLength);
            return;
        }

        distanceSinceLastUpdate += step;
        while (distanceSinceLastUpdate >= BLOCKS_PER_PATH_SEGMENT) {
            distanceSinceLastUpdate -= BLOCKS_PER_PATH_SEGMENT;
            pathStart = (pathStart - 1 + path.length) % path.length;
            path[pathStart] = headY;
        }
    }

    double samplePath(double targetLength) {
        if (targetLength < distanceSinceLastUpdate) {
            double t = Mth.inverseLerp(targetLength, 0, distanceSinceLastUpdate);
            return Mth.lerp(t, headY, path[pathStart]);
        } else {
            targetLength -= distanceSinceLastUpdate;
            int index = (int) Math.floor(targetLength / BLOCKS_PER_PATH_SEGMENT);
            double t = (targetLength - (index * BLOCKS_PER_PATH_SEGMENT)) / BLOCKS_PER_PATH_SEGMENT;
            return Mth.lerp(t,
                    path[(pathStart + index + 0) % path.length],
                    path[(pathStart + index + 1) % path.length]);
        }
    }

    double wave(double offset, double length) {
        return Math.sin((length / 32.0 - offset) * Mth.PI) * 5;
    }
}
