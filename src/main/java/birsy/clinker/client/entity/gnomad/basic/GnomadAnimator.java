package birsy.clinker.client.entity.gnomad.basic;

import birsy.clinker.client.entity.gnomad.mogul.GnomadMogulAnimator;
import birsy.clinker.client.entity.gnomad.mogul.GnomadMogulSkeleton;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import birsy.clinker.common.world.entity.gnomad.testing.SquadTestingThrowerEntity;
import foundry.veil.api.client.necromancer.animation.Animation;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import static net.minecraft.core.Direction.Axis.*;
import static birsy.clinker.client.AnimationUtilities.*;


public class GnomadAnimator extends Animator<SquadTestingThrowerEntity, GnomadSkeleton> {
    public final AnimationEntry<?, ?> idleAnim, walkAnim, strafeAnim;
    protected GnomadAnimator(SquadTestingThrowerEntity parent, GnomadSkeleton skeleton) {
        super(parent, skeleton);
        this.idleAnim = this.addAnimation(IdleAnimation.INSTANCE, 0);
        this.walkAnim = this.addAnimation(WalkAnimation.INSTANCE, 1);
        this.strafeAnim = this.addAnimation(StrafeAnimation.INSTANCE, 2);
    }

    @Override
    public void animate() {
        super.animate();
        SquadTestingThrowerEntity entity = this.parent;

        this.idleAnim.setMixFactor(1.0F);
        this.idleAnim.setTime(entity.tickCount);

        float moveTime = entity.getCumulativeLocomotionAmount() * 1.5F;

        float walkFac = Mth.clamp(12 * entity.getForwardLocomotionAmount(1.0F), -2.0F, 2.0F);
        this.walkAnim.setMixFactor(walkFac);
        this.walkAnim.setTime(moveTime);

        float strafeFac = Mth.clamp(12 * entity.getStrafeLocomotionAmount(1.0F), -2.0F, 2.0F);
        this.strafeAnim.setMixFactor(strafeFac);
        this.strafeAnim.setTime(moveTime);

        // flinch
        if (entity.hurtDuration > 0) {
            float flinchTime = entity.tickCount * 0.9F;
            float flinchFactor = (float) entity.hurtTime / entity.hurtDuration;
            skeleton.root.rotateDeg(Mth.sin(flinchTime) * 8 * flinchFactor, Direction.Axis.X);
            skeleton.root.rotateDeg(Mth.cos(flinchTime) * 8 * flinchFactor, Direction.Axis.Z);

            skeleton.head.rotateDeg(Mth.sin(flinchTime - 1) * 8 * flinchFactor, Direction.Axis.X);
            skeleton.head.rotateDeg(Mth.cos(flinchTime - 1) * 8 * flinchFactor, Direction.Axis.Z);

            skeleton.rightArm.rotateDeg(Mth.sin(flinchTime - 1) * 15 * flinchFactor, Direction.Axis.X);
            skeleton.rightArm.rotateDeg(Mth.cos(flinchTime - 1) * 15 * flinchFactor, Direction.Axis.Z);
            skeleton.leftArm.rotateDeg(Mth.sin(flinchTime - 1) * 15 * flinchFactor, Direction.Axis.X);
            skeleton.leftArm.rotateDeg(Mth.cos(flinchTime - 1) * -15 * flinchFactor, Direction.Axis.Z);

            skeleton.leftLeg.rotateDeg(Mth.sin(flinchTime - 1) * 15 * flinchFactor, Direction.Axis.X);
            skeleton.leftLeg.rotateDeg(Mth.cos(flinchTime - 1) * -15 * flinchFactor, Direction.Axis.Z);
            skeleton.rightLeg.rotateDeg(Mth.sin(flinchTime - 1) * 15 * flinchFactor, Direction.Axis.X);
            skeleton.rightLeg.rotateDeg(Mth.cos(flinchTime - 1) * 15 * flinchFactor, Direction.Axis.Z);
        }
    }

    private static class IdleAnimation extends Animation<SquadTestingThrowerEntity, GnomadSkeleton> {
        protected static IdleAnimation INSTANCE = new IdleAnimation();

        public void apply(SquadTestingThrowerEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            float bodyYaw = Mth.wrapDegrees(180 - entity.yBodyRot);
            float headYaw = Mth.wrapDegrees(180 - entity.yHeadRot);
            float netHeadYaw = Mth.degreesDifference(bodyYaw, headYaw);
            netHeadYaw = Mth.clamp(netHeadYaw, -80, 80);
            float headPitch = -entity.getViewXRot(1.0F);
            skeleton.root.rotateDeg(bodyYaw, Y);
            skeleton.neck.rotateDeg(netHeadYaw, Y);
            skeleton.head.rotateDeg(headPitch, X);

            float torsoRotation = -5;
            skeleton.torso.rotateDeg(torsoRotation, X);
            skeleton.torso.rotation.conjugate(skeleton.skirt.rotation);

            skeleton.rightArm.rotateDeg(-torsoRotation, X);
            skeleton.leftArm.rotateDeg(-torsoRotation, X);
            skeleton.rightArm.rotateDeg(5, Z);
            skeleton.leftArm.rotateDeg(-5, Z);

            float neckRotation = 10;
            skeleton.neck.rotateDeg(-torsoRotation + neckRotation, X);
            skeleton.headJoint.rotateDeg(-neckRotation, X);

            float speed = 1.0F / 40.0F;
            float degree = 1.0F;

            skeleton.rightArm.rotateDeg(nSin(time * speed * 1.0F) * 1 * mixFactor * degree, Z);
            skeleton.rightArm.rotateDeg(nSin(time * speed * 0.9F) * 1 * mixFactor * degree, X);

            skeleton.leftArm.rotateDeg(nSin(time * speed * 0.85F) * 1 * mixFactor * degree, Z);
            skeleton.leftArm.rotateDeg(nSin(time * speed * 0.95F) * 1 * mixFactor * degree, X);

            skeleton.torso.offsetY(nSin(time * speed * 0.7F) * 0.2F * mixFactor * degree);

            skeleton.nose.rotateDeg(nSin(time * speed * 0.85F) * 1 * mixFactor * degree, Z);
            skeleton.nose.rotateDeg(nSin(time * speed * 0.95F) * 1 * mixFactor * degree, X);

            skeleton.neck.rotateDeg(nSin(time * speed * 0.6F) * 1 * mixFactor * degree, Z);
            skeleton.neck.rotateDeg(nSin(time * speed * 0.5F) * 1 * mixFactor * degree, X);
        }
    }

    private static class WalkAnimation extends Animation<SquadTestingThrowerEntity, GnomadSkeleton> {
        protected static WalkAnimation INSTANCE = new WalkAnimation();

        @Override
        public boolean running(SquadTestingThrowerEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(SquadTestingThrowerEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            float degree = 1.0F;
            float sign = Mth.sign(mixFactor);
            mixFactor = Mth.abs(mixFactor);

            // bounce mix factor decreases with speed...
            float bounceMixFactor = mixFactor;
            if (mixFactor > 1.0F) bounceMixFactor = Mth.clampedMap(bounceMixFactor, 1.0F, 3.0F, mixFactor, 0);
            // squared mix factor really emphasizes movements when running
            float squaredMixFactor = mixFactor * mixFactor;

            skeleton.root.offsetY(Math.abs(nSin(time + 0.5F)) * 1.0F * bounceMixFactor * degree);
            skeleton.root.offsetX(nSin(time * 2) * 0.5F * bounceMixFactor * sign * degree);

            skeleton.root.rotateDeg(nSin(time) * 1 * bounceMixFactor * sign * degree, Z);

            skeleton.rightLeg.offsetZ(nSin(time) * -2 * mixFactor * sign * degree);
            skeleton.rightLeg.offsetY(nSin(time + 0.5F) * 1 * mixFactor * degree);
            skeleton.rightLeg.rotateDeg(nSin(time) * 15 * mixFactor * sign * degree, X);

            skeleton.leftLeg.offsetZ(-nSin(time) * -2 * mixFactor * sign * degree);
            skeleton.leftLeg.offsetY(-nSin(time + 0.5F) * 1 * mixFactor * degree);
            skeleton.leftLeg.rotateDeg(-nSin(time) * 15 * mixFactor * sign * degree, X);

            skeleton.rightArm.rotateDeg(nSin(time + 0.2F) * -12 * squaredMixFactor * sign * degree, X);
            skeleton.leftArm.rotateDeg(-nSin(time + 0.2F) * -12 * squaredMixFactor * sign * degree, X);

            skeleton.rightArm.offsetY(nSin(time * 2 + 0.2F) * 0.1F * bounceMixFactor * degree);
            skeleton.leftArm .offsetY(nSin(time * 2 + 0.2F) * 0.1F * bounceMixFactor * degree);

            skeleton.neck.offsetY(nSin(time * 2 + 0.15F) * 0.15F * bounceMixFactor * degree);
            skeleton.head.offsetY(nSin(time * 2 + 0.30F) * 0.1F * bounceMixFactor * degree);

            skeleton.bag.offsetY(nSin(time * 2 + 0.4F) * 0.1F * bounceMixFactor * degree);

            float torsoAngle = -3.0F * squaredMixFactor * degree * sign;
            skeleton.torso.rotateDeg(torsoAngle, X);
            skeleton.skirt.rotateDeg(-torsoAngle, X);
        }
    }

    private static class StrafeAnimation extends Animation<SquadTestingThrowerEntity, GnomadSkeleton> {
        protected static StrafeAnimation INSTANCE = new StrafeAnimation();

        @Override
        public boolean running(SquadTestingThrowerEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(SquadTestingThrowerEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            float degree = 1.0F;
            float sign = Mth.sign(mixFactor);
            mixFactor = Mth.abs(mixFactor);

            float bounceMixFactor = mixFactor;
            if (mixFactor > 1.0F) bounceMixFactor = Mth.clampedMap(bounceMixFactor, 1.0F, 3.0F, mixFactor, 0);

            skeleton.root.rotateDeg(-2 * mixFactor * sign * degree, Z);
            skeleton.neck.rotateDeg(2 * mixFactor * sign * degree, Z);
            skeleton.bag.rotateDeg(2 * mixFactor * sign * degree, Z);

            skeleton.root.offsetY(Math.abs(nSin(time + 0.5F)) * 0.5F * bounceMixFactor * degree);
            skeleton.root.rotateDeg(nSin(time) * 1 * bounceMixFactor * sign * degree, Z);

            skeleton.rightLeg.offsetX(nSin(time) * 1 * mixFactor * sign * degree);
            skeleton.rightLeg.offsetY(nSin(time + 0.5F) * 1 * mixFactor * degree);
            skeleton.rightLeg.rotateDeg(nSin(time) * 10 * mixFactor * sign * degree, Z);

            skeleton.leftLeg.offsetX(-nSin(time) * 1 * mixFactor * sign * degree);
            skeleton.leftLeg.offsetY(-nSin(time + 0.5F) * 1 * mixFactor * degree);
            skeleton.leftLeg.rotateDeg(-nSin(time) * 10 * mixFactor * sign * degree, Z);

            float armSwingMixFactor = mixFactor * mixFactor;
            skeleton.rightArm.rotateDeg(nSin(time + 0.2F) * -10 * armSwingMixFactor * sign * degree, X);
            skeleton.leftArm.rotateDeg(-nSin(time + 0.2F) * -10 * armSwingMixFactor * sign * degree, X);

            skeleton.neck.offsetY(nSin(time * 2 + 0.15F) * 0.10F * bounceMixFactor * degree);
            skeleton.head.offsetY(nSin(time * 2 + 0.30F) * 0.05F * bounceMixFactor * degree);
            skeleton.bag.offsetY(nSin(time * 2 + 0.4F) * 0.1F * bounceMixFactor * degree);
        }
    }

}
