package birsy.clinker.client.entity.leggy;

import birsy.clinker.common.entity.GiantLeggyCritterEntity;
import birsy.clinker.common.entity.LegManager;
import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class LeggyAnimator extends Animator<GiantLeggyCritterEntity, LeggySkeleton> {
    final Vector3f socket = new Vector3f(), foot = new Vector3f(), relative = new Vector3f();

    protected LeggyAnimator(GiantLeggyCritterEntity parent, LeggySkeleton skeleton) {
        super(parent, skeleton);
    }

    @Override
    public void animate() {
        super.animate();
        LegManager legManager = parent.legManager;

        skeleton.root.rotate((float) legManager.getParentRotation(), Direction.Axis.Y);

        final float upperLength = 24f, lowerLength = 24f;

        skeleton.head.offsetY(12);

        int legCount = legManager.legCount();
        float averageLegY = 0;
        for (int i = 0; i < legCount; i++) {
            LegManager.Leg leg = legManager.getLeg(i);
            averageLegY += (float) leg.getRelativeFootPos().y();
        }
        averageLegY /= legCount;
        skeleton.root.offsetY(averageLegY);

        for (int i = 0; i < legCount; i++) {
            int legIndex = Math.floorMod(-i, legCount);
            LegManager.Leg leg = legManager.getLeg(legIndex);
            Bone upper = skeleton.upperLegs[i];
            Bone lower = skeleton.lowerLegs[i];

            socket.set(leg.relativeSocketPos).mul(16);
            socket.add(0, averageLegY, 0);
            foot.set(leg.getRelativeFootPos()).mul(16);
            foot.sub(socket, relative);

            float distance = relative.length();

            upper.position.set(socket).add(0, -2, 0);
            float yaw = (float) Mth.atan2(relative.z, -relative.x) + Mth.HALF_PI;
            float pitch = (float) Math.asin(relative.y / distance) + Mth.HALF_PI;

            float hipAngle = (float) Math.acos(Mth.clamp(distance / (upperLength + lowerLength), -1, 1));
            float kneeAngle = Mth.PI - 2 * hipAngle;

            upper.rotation.rotationYXZ(yaw, pitch + hipAngle, 0);
            lower.rotation.rotationX(kneeAngle - Mth.PI);
        }
    }
}
