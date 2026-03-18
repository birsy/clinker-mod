package birsy.clinker.client.entity.gnomad.basic;

import birsy.clinker.common.world.entity.gnomad.testing.SquadTestingThrowerEntity;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class GnomadAnimator extends Animator<SquadTestingThrowerEntity, GnomadSkeleton> {
    protected GnomadAnimator(SquadTestingThrowerEntity parent, GnomadSkeleton skeleton) {
        super(parent, skeleton);
    }

    @Override
    public void animate() {
        super.animate();
        SquadTestingThrowerEntity entity = this.parent;

        float bodyYaw = Mth.wrapDegrees(180 - entity.yBodyRot);
        float headYaw = Mth.wrapDegrees(180 - entity.yHeadRot);
        float netHeadYaw = Mth.degreesDifference(bodyYaw, headYaw);
        netHeadYaw = Mth.clamp(netHeadYaw, -80, 80);
        float headPitch = -entity.getViewXRot(1.0F);
        skeleton.root.rotateDeg(bodyYaw, Direction.Axis.Y);
        skeleton.neck.rotateDeg(netHeadYaw, Direction.Axis.Y);
        skeleton.head.rotateDeg(headPitch, Direction.Axis.X);

        float torsoRotation = -5;
        skeleton.torso.rotateDeg(torsoRotation, Direction.Axis.X);
        skeleton.torso.rotation.conjugate(skeleton.skirt.rotation);

        skeleton.rightArm.rotateDeg(-torsoRotation, Direction.Axis.X);
        skeleton.leftArm.rotateDeg(-torsoRotation, Direction.Axis.X);
        skeleton.rightArm.rotateDeg(5, Direction.Axis.Z);
        skeleton.leftArm.rotateDeg(-5, Direction.Axis.Z);

        float neckRotation = 10;
        skeleton.neck.rotateDeg(-torsoRotation + neckRotation, Direction.Axis.X);
        skeleton.headJoint.rotateDeg(-neckRotation, Direction.Axis.X);


    }
}
