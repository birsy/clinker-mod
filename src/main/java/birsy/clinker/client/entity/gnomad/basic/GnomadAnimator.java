package birsy.clinker.client.entity.gnomad.basic;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.core.util.MathUtils;
import foundry.veil.api.client.necromancer.animation.Animation;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.joml.Vector3fc;

import static net.minecraft.core.Direction.Axis.*;
import static birsy.clinker.client.AnimationUtilities.*;


public class GnomadAnimator extends Animator<GnomadEntity, GnomadSkeleton> {
    public final AnimationEntry<?, ?> idleAnim, walkAnim, strafeAnim, hurtAnim, maskAnim;
    private int maskShakeTime = 0, maskShakeDuration = 1;
    private boolean maskShaking = false;

    protected GnomadAnimator(GnomadEntity parent, GnomadSkeleton skeleton) {
        super(parent, skeleton);
        this.idleAnim = this.addAnimation(IdleAnimation.INSTANCE, 0);
        this.walkAnim = this.addAnimation(WalkAnimation.INSTANCE, 1);
        this.strafeAnim = this.addAnimation(StrafeAnimation.INSTANCE, 2);
        this.hurtAnim = this.addAnimation(HurtAnimation.INSTANCE, 3);
        this.maskAnim = this.addAnimation(MaskAnimation.INSTANCE, 4);
    }

    @Override
    public void animate() {
        super.animate();
        GnomadEntity entity = this.parent;

        this.idleAnim.setMixFactor(1.0F);
        this.idleAnim.setTime(entity.tickCount);

        float moveTime = entity.getCumulativeLocomotionAmount() * 1.7F;

        float walkFac = Mth.clamp(12 * entity.getForwardLocomotionAmount(1.0F), -2.0F, 2.0F);
        this.walkAnim.setMixFactor(walkFac);
        this.walkAnim.setTime(moveTime);

        float strafeFac = Mth.clamp(12 * entity.getStrafeLocomotionAmount(1.0F), -2.0F, 2.0F);
        this.strafeAnim.setMixFactor(strafeFac);
        this.strafeAnim.setTime(moveTime);

        if (entity.hurtDuration > 0) {
            float hurtMixFactor = (float) entity.hurtTime / entity.hurtDuration;
            this.hurtAnim.setTime(entity.tickCount * 0.28F);
            this.hurtAnim.setMixFactor(hurtMixFactor);

            Vector3fc hurtDirection = entity.getLastHitDirection();
            if (hurtDirection.x() != 0 || hurtDirection.z() != 0) {
                float hurtRotationAxisX = -hurtDirection.z(),
                        hurtRotationAxisZ = hurtDirection.x();
                skeleton.root.rotation.rotateAxis(-30 * Mth.DEG_TO_RAD * hurtMixFactor, hurtRotationAxisX, 0, hurtRotationAxisZ);
            }
        } else {
            this.hurtAnim.setMixFactor(0.0F);
        }

        // randomly start shaking face
        if (maskShaking) {
            this.maskShakeTime++;
            // if we're out of time, stop shaking
            if (this.maskShakeTime > this.maskShakeDuration) {
                this.maskShaking = false;
                this.maskShakeTime = 0;
            }
        } else if (RandomUtil.oneInNChance(100)) {
            this.maskShaking = true;
            this.maskShakeTime = 0;
            this.maskShakeDuration = RandomUtil.randomNumberBetween(2 * 20, 12 * 20);
        }
        float normalizedTime = (float) this.maskShakeTime / this.maskShakeDuration;
        float shakeAmount = Mth.clamp(-4.0F*normalizedTime*normalizedTime + 4.0F*normalizedTime, 0, 1);
        shakeAmount = shakeAmount * shakeAmount * shakeAmount * shakeAmount * 0.5F;
        this.maskAnim.setMixFactor(shakeAmount);
        this.maskAnim.setTime(entity.tickCount);

        this.skeleton.leftHandPivot.offsetZ(-1);
        this.skeleton.rightHandPivot.offsetZ(-1);

        // death
        if (entity.isDeadOrDying()) {
            float deathFactor = Mth.clamp(entity.deathTime / 17F, 0, 1);
            deathFactor = MathUtils.ease(deathFactor, MathUtils.EasingType.easeOutBack);
            skeleton.root.rotation.rotateLocalZ(
                    deathFactor * 70 * Mth.DEG_TO_RAD
            );
        }
    }

    private static class MaskAnimation extends Animation<GnomadEntity, GnomadSkeleton> {
        protected static MaskAnimation INSTANCE = new MaskAnimation();
        @Override
        public void apply(GnomadEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            float headShake = Mth.sin(time * 0.44F) * mixFactor * 0.05F * Mth.RAD_TO_DEG;
            float faceShake = Mth.sin(0.5F + time * 0.44F) * mixFactor * 0.25F * Mth.RAD_TO_DEG;
            skeleton.head.rotateDeg(headShake, Direction.Axis.Z);
            skeleton.face.rotateDeg(faceShake, Direction.Axis.Z);
        }
    }

    private static class HurtAnimation extends Animation<GnomadEntity, GnomadSkeleton> {
        protected static HurtAnimation INSTANCE = new HurtAnimation();

        public void apply(GnomadEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            skeleton.root.rotateDeg(nSin(0.0F + time) * 8 * mixFactor, Direction.Axis.X);
            skeleton.root.rotateDeg(nSin(0.5F + time) * 8 * mixFactor, Direction.Axis.Z);

            skeleton.head.rotateDeg(nSin(0.0F + time - 1) * 8 * mixFactor, Direction.Axis.X);
            skeleton.head.rotateDeg(nSin(0.5F + time - 1) * 8 * mixFactor, Direction.Axis.Z);

            skeleton.rightArm.rotateDeg(nSin(0.0F + time - 1) * 15 * mixFactor, Direction.Axis.X);
            skeleton.rightArm.rotateDeg(nSin(0.5F + time - 1) * 15 * mixFactor, Direction.Axis.Z);
            skeleton.leftArm.rotateDeg(nSin(0.0F + time - 1) * 15 * mixFactor, Direction.Axis.X);
            skeleton.leftArm.rotateDeg(nSin(0.5F + time - 1) * -15 * mixFactor, Direction.Axis.Z);

            skeleton.leftLeg.rotateDeg(nSin(0.0F + time - 1) * 15 * mixFactor, Direction.Axis.X);
            skeleton.leftLeg.rotateDeg(nSin(0.5F + time - 1) * -15 * mixFactor, Direction.Axis.Z);
            skeleton.rightLeg.rotateDeg(nSin(0.0F + time - 1) * 15 * mixFactor, Direction.Axis.X);
            skeleton.rightLeg.rotateDeg(nSin(0.5F + time - 1) * 15 * mixFactor, Direction.Axis.Z);
        }
    }

    private static class IdleAnimation extends Animation<GnomadEntity, GnomadSkeleton> {
        protected static IdleAnimation INSTANCE = new IdleAnimation();

        public void apply(GnomadEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
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

    private static class WalkAnimation extends Animation<GnomadEntity, GnomadSkeleton> {
        protected static WalkAnimation INSTANCE = new WalkAnimation();

        @Override
        public boolean running(GnomadEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(GnomadEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            float degree = 1.0F;
            float sign = Mth.sign(mixFactor);
            mixFactor = Mth.abs(mixFactor);

            // bounce mix factor decreases with speed...
            float bounceMixFactor = mixFactor;
            if (mixFactor > 1.0F) bounceMixFactor = Mth.clampedMap(bounceMixFactor, 1.0F, 3.0F, mixFactor, 0);
            // squared mix factor really emphasizes movements when running
            float squaredMixFactor = mixFactor * mixFactor;

            skeleton.root.offsetY(Math.abs(nSin(time + 0.5F)) * 1.0F * bounceMixFactor * degree);
            skeleton.root.offsetZ(nSin(time * 2) * 0.5F * bounceMixFactor * sign * degree);

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

    private static class StrafeAnimation extends Animation<GnomadEntity, GnomadSkeleton> {
        protected static StrafeAnimation INSTANCE = new StrafeAnimation();

        @Override
        public boolean running(GnomadEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(GnomadEntity entity, GnomadSkeleton skeleton, float mixFactor, float time) {
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
            skeleton.rightArm.rotateDeg(nSin(time + 0.2F) * -12 * armSwingMixFactor * sign * degree, X);
            skeleton.leftArm.rotateDeg(-nSin(time + 0.2F) * -12 * armSwingMixFactor * sign * degree, X);

            skeleton.neck.offsetY(nSin(time * 2 + 0.15F) * 0.10F * bounceMixFactor * degree);
            skeleton.head.offsetY(nSin(time * 2 + 0.30F) * 0.05F * bounceMixFactor * degree);
            skeleton.bag.offsetY(nSin(time * 2 + 0.4F) * 0.1F * bounceMixFactor * degree);
        }
    }
}
