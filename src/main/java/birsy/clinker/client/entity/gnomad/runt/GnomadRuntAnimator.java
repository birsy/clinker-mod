package birsy.clinker.client.entity.gnomad.runt;

import birsy.clinker.common.world.entity.gnomad.testing.SquadTestingSupplierEntity;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class GnomadRuntAnimator extends Animator<SquadTestingSupplierEntity, GnomadRuntSkeleton> {
    protected GnomadRuntAnimator(SquadTestingSupplierEntity parent, GnomadRuntSkeleton skeleton) {
        super(parent, skeleton);
    }

    @Override
    public void animate() {
        super.animate();
        skeleton.root.size.mul(0.8F);

        SquadTestingSupplierEntity entity = this.parent;

        float bodyYaw = Mth.wrapDegrees(180 - entity.yBodyRot);
        float headYaw = Mth.wrapDegrees(180 - entity.yHeadRot);
        float netHeadYaw = Mth.degreesDifference(bodyYaw, headYaw);
        netHeadYaw = Mth.clamp(netHeadYaw, -80, 80);
        float headPitch = -entity.getViewXRot(1.0F);
        skeleton.root.rotateDeg(bodyYaw, Direction.Axis.Y);
        skeleton.head.rotateDeg(netHeadYaw, Direction.Axis.Y);
        skeleton.head.rotateDeg(headPitch, Direction.Axis.X);


        float torsoRotation = 5;
        skeleton.torso.rotateDeg(torsoRotation, Direction.Axis.X);

        skeleton.rightArm.rotateDeg(-torsoRotation, Direction.Axis.X);
        skeleton.leftArm.rotateDeg(-torsoRotation, Direction.Axis.X);
        skeleton.rightArm.rotateDeg(25, Direction.Axis.Z);
        skeleton.leftArm.rotateDeg(-25, Direction.Axis.Z);

        skeleton.head.rotateDeg(-torsoRotation, Direction.Axis.X);

        if (entity.isHoldingDelivery()) {
            skeleton.rightArm.rotateDeg(80, Direction.Axis.Y);
            skeleton.leftArm.rotateDeg(-80, Direction.Axis.Y);
            skeleton.rightArm.rotateDeg(70, Direction.Axis.Z);
            skeleton.leftArm.rotateDeg(-70, Direction.Axis.Z);
        }
    }
}
