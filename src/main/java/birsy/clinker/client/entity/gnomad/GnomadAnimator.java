package birsy.clinker.client.entity.gnomad;

import birsy.clinker.client.entity.slabcrab.SlabCrabSkeleton;
import birsy.clinker.common.world.entity.SlabCrabEntity;
import birsy.clinker.common.world.entity.gnomad.testing.SquadTestingThrowerEntity;
import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.animation.Animation;
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
        skeleton.root.rotateDeg(180, Direction.Axis.Y);

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
