package birsy.clinker.client.entity.gnomadrunt;

import birsy.clinker.client.entity.gnomad.GnomadSkeleton;
import birsy.clinker.common.world.entity.gnomad.testing.SquadTestingSupplierEntity;
import birsy.clinker.common.world.entity.gnomad.testing.SquadTestingThrowerEntity;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.Direction;

public class GnomadRuntAnimator extends Animator<SquadTestingSupplierEntity, GnomadRuntSkeleton> {
    protected GnomadRuntAnimator(SquadTestingSupplierEntity parent, GnomadRuntSkeleton skeleton) {
        super(parent, skeleton);
    }

    @Override
    public void animate() {
        super.animate();
        skeleton.root.rotateDeg(180, Direction.Axis.Y);
        skeleton.root.size.mul(0.8F);

        float torsoRotation = 5;
        skeleton.torso.rotateDeg(torsoRotation, Direction.Axis.X);

        skeleton.rightArm.rotateDeg(-torsoRotation, Direction.Axis.X);
        skeleton.leftArm.rotateDeg(-torsoRotation, Direction.Axis.X);
        skeleton.rightArm.rotateDeg(25, Direction.Axis.Z);
        skeleton.leftArm.rotateDeg(-25, Direction.Axis.Z);

        skeleton.head.rotateDeg(-torsoRotation, Direction.Axis.X);

    }
}
