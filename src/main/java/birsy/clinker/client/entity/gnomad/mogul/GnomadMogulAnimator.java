package birsy.clinker.client.entity.gnomad.mogul;

import birsy.clinker.client.entity.gnomad.basic.GnomadAnimator;
import birsy.clinker.common.world.entity.gnomad.mogul.MogulAttackHandler;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.animation.Animation;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import static birsy.clinker.client.AnimationUtilities.*;
import static net.minecraft.core.Direction.Axis.*;

public class GnomadMogulAnimator extends Animator<GnomadMogulEntity, GnomadMogulSkeleton> {
    public final AnimationEntry<?, ?> idleAnim, maskAnim, walkAnim, strafeAnim, floatAnim, sitAnim;
    public final Animator.TimedAnimationEntry<?, ?> upSwingAnim, leftSwingAnim, rightSwingAnim;
    private int maskShakeTime = 0, maskShakeDuration = 1;
    private boolean maskShaking = false;
    private float desiredLookPitchOffset = 0, lookPitchOffset = 0,
            desiredLookYawOffset = 0, lookYawOffset = 0;

    private float floatingTransition = 0;
    private float sitFactor = 0.0F;
    public final SurveyorWheel stepCounter = new SurveyorWheel(0.4F);

    protected GnomadMogulAnimator(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton) {
        super(parent, skeleton);
        this.upSwingAnim = this.addTimedAnimation(UpAttackAnimation.INSTANCE, 3, UpAttackAnimation.INSTANCE.animLength);
        this.leftSwingAnim = this.addTimedAnimation(LeftAttackAnimation.INSTANCE, 3, LeftAttackAnimation.INSTANCE.animLength);
        this.rightSwingAnim = this.addTimedAnimation(RightAttackAnimation.INSTANCE, 3, RightAttackAnimation.INSTANCE.animLength);

        this.strafeAnim = this.addAnimation(StrafeAnimation.INSTANCE, 2);
        this.walkAnim = this.addAnimation(WalkAnimation.INSTANCE, 1);
        this.sitAnim = this.addAnimation(SitAnimation.INSTANCE, 1);
        this.maskAnim = this.addAnimation(MaskAnimation.INSTANCE, 0);
        this.idleAnim = this.addAnimation(IdleAnimation.INSTANCE, 0);
        this.floatAnim = this.addAnimation(FloatAnimation.INSTANCE, 3);
    }

    private void updateAttackAnim(TimedAnimationEntry<?, ?> attack) {
        if (attack.playing) {
            attack.setMixFactor(Mth.lerp(0.3F, attack.getMixFactor(), 1));
        } else {
            attack.setMixFactor(Mth.lerp(0.1F, attack.getMixFactor(), 0));
        }
    }

    @Override
    public void animate() {
        super.animate();
        GnomadMogulEntity entity = this.parent;

        boolean sitting = entity.isSitting();
        this.sitFactor = Mth.approach(sitFactor, sitting ? 1 : 0, sitting ? 0.025F : 0.0125F);
        this.sitAnim.setMixFactor(sitFactor);
        this.sitAnim.setTime(sitting ? 0 : 1);

//        float robeAlpha = 1.0F;
//        skeleton.MogulFrontRobe.color.set(1,1,1,robeAlpha);
//        skeleton.MogulLeftRobe.color.set(1,1,1,robeAlpha);
//        skeleton.MogulBackRobe.color.set(1,1,1,robeAlpha);
//        skeleton.MogulRightRobe.color.set(1,1,1,robeAlpha);

        // attacks!
        updateAttackAnim(upSwingAnim);
        updateAttackAnim(rightSwingAnim);
        updateAttackAnim(leftSwingAnim);

        // idle
        this.idleAnim.setTime(entity.tickCount);
        this.idleAnim.setMixFactor(1.0F);

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

        // look around a little randomly
        if (RandomUtil.oneInNChance(40)) desiredLookYawOffset = (float) RandomUtil.randomScaledGaussianValue(1.5F);
        if (RandomUtil.oneInNChance(40)) desiredLookPitchOffset = (float) RandomUtil.randomScaledGaussianValue(2.0F);
        lookYawOffset = Mth.approach(lookYawOffset, desiredLookYawOffset, 0.7F);
        lookPitchOffset = Mth.approach(lookPitchOffset, desiredLookPitchOffset, 0.7F);
        skeleton.MogulNeck.rotateDeg(lookYawOffset * 0.5F, Y);
        skeleton.MogulHead.rotateDeg(lookYawOffset * 0.5F, Y);
        skeleton.MogulNeck.rotateDeg(lookPitchOffset * 0.5F, X);
        skeleton.MogulHead.rotateDeg(lookPitchOffset * 0.5F, X);

        // mask shake noise
        // todo: make some kind of "woosh-y" noise for this
//        if (shakeAmount > 0.05) {
//            float maskShakeTime = 0.5F + entity.tickCount * 0.44F, prevMaskShakeTime = 0.5F + (entity.tickCount - 1) * 0.44F;
//            float maskShakeDeriv = -nSin(maskShakeTime * 0.5F), previousMaskShakeDeriv = -nSin(prevMaskShakeTime * 0.5F);
//            if (Math.signum(maskShakeDeriv) != Math.signum(previousMaskShakeDeriv)) {
//                entity.level().playLocalSound(entity, SoundEvents.BAT_TAKEOFF, SoundSource.HOSTILE,
//                        shakeAmount * 0.25F, (shakeAmount * 1.5F + (float)RandomUtil.randomValueBetween(0.0, 0.25)) * 0.1F
//                );
//            }
//        }

        // locomotion


        if (entity.isFloating()) {
            this.floatingTransition = Mth.lerp(0.8F, this.floatingTransition, 1.0F);
        } else {
            this.floatingTransition = Mth.lerp(0.1F, this.floatingTransition,  0.0F);
        }

        float moveTime = entity.getCumulativeLocomotionAmount() * 2.3F;

        float walkAmount = entity.getForwardLocomotionAmount(1.0F),
              strafeAmount = entity.getStrafeLocomotionAmount(1.0F);
        float strideLength = Mth.clampedMap((float) Mth.length(walkAmount, strafeAmount), 0, 2.0F, 1.1F, 1.1F);
        this.stepCounter.update(strideLength, entity.getCumulativeLocomotionAmount());
        float stepsTaken = stepCounter.angle();

        float walkFac = Mth.clamp(10 * walkAmount, -2.0F, 2.0F) * (1 - this.floatingTransition);
        this.walkAnim.setMixFactor(walkFac);
        this.walkAnim.setTime(stepsTaken);

        float strafeFac = Mth.clamp(10 * strafeAmount, -2.0F, 2.0F) * (1 - this.floatingTransition);
        this.strafeAnim.setMixFactor(-strafeFac);
        this.strafeAnim.setTime(stepsTaken);

        this.floatAnim.setMixFactor(this.floatingTransition);
        this.floatAnim.setTime(entity.tickCount);

        // flinch
        if (entity.hurtDuration > 0) {
            float flinchTime = entity.tickCount;
            float flinchFactor = (float) entity.hurtTime / entity.hurtDuration;
            skeleton.MogulRoot.rotateDeg(nSin(flinchTime) * 4 * flinchFactor, X);
            skeleton.MogulRoot.rotateDeg(-nSin(flinchTime) * 4 * flinchFactor, Z);

            skeleton.MogulHead.rotateDeg(nSin(flinchTime - 1) * 8 * flinchFactor, X);
            skeleton.MogulHead.rotateDeg(-nSin(flinchTime - 1) * 8 * flinchFactor, Z);

            skeleton.MogulRightArm.rotateDeg(nSin(flinchTime - 1) * 8 * flinchFactor, X);
            skeleton.MogulRightArm.rotateDeg(-nSin(flinchTime - 1) * 8 * flinchFactor, Z);
            skeleton.MogulLeftArm.rotateDeg(nSin(flinchTime - 1) * 8 * flinchFactor, X);
            skeleton.MogulLeftArm.rotateDeg(-nSin(flinchTime - 1) * 8 * flinchFactor, Z);

            Vector3fc hurtDirection = entity.getLastHitDirection();
            if (hurtDirection.x() != 0 || hurtDirection.z() != 0) {
                float hurtRotationAxisX = -hurtDirection.z(),
                        hurtRotationAxisZ = hurtDirection.x();
                skeleton.MogulRoot.rotation.rotateAxis(-10 * Mth.DEG_TO_RAD * flinchFactor, hurtRotationAxisX, 0, hurtRotationAxisZ);
            }
        }

        // death
        if (entity.isDeadOrDying()) {
            float deathFactor = Mth.clamp(entity.deathTime / 17F, 0, 1);
            deathFactor = MathUtils.ease(deathFactor, MathUtils.EasingType.easeOutBack);
            skeleton.MogulBody.rotation.rotateAxis(deathFactor * 70 * Mth.DEG_TO_RAD, Mth.sqrt(2), 0, Mth.sqrt(2));
        }

//        smoothedAcceleration.lerp(temp.set(entity.acceleration.x(), entity.acceleration.y(), entity.acceleration.z()), 0.15F);
//
//        Vector3f axis = smoothedAcceleration.mul(1, 0, 1, temp);
//        if (axis.lengthSquared() > 0) {
//            float angle = axis.length();
//            axis = axis.normalize().cross(0, 1, 0);
//            skeleton.MogulRoot.rotation.rotateAxis(angle * 2, axis);
//        }
    }

    private static class SitAnimation extends Animation<GnomadMogulEntity, GnomadMogulSkeleton> {
        protected static SitAnimation INSTANCE = new SitAnimation();
        @Override
        public void apply(GnomadMogulEntity entity, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            boolean standing = time > 0.5F;
            float sitFactor = MathUtils.ease(mixFactor, standing ? MathUtils.EasingType.easeInBack : MathUtils.EasingType.easeInOutCubic);

            skeleton.MogulBody.offsetY(-5 * sitFactor);
            skeleton.MogulBody.offsetZ(5 * sitFactor);

//            skeleton.MogulNeck.offsetY(3 * sitFactor);
//            skeleton.MogulBackHelmetFlap.rotateDeg(20 * sitFactor, X);

            skeleton.MogulLeftLeg.offsetY(5 * sitFactor);
            skeleton.MogulRightLeg.offsetY(5 * sitFactor);
            skeleton.MogulLeftLeg.offsetZ(-7 * sitFactor);
            skeleton.MogulRightLeg.offsetZ(-7 * sitFactor);
            skeleton.MogulLeftLeg.rotateDeg(-15 * sitFactor, X);
            skeleton.MogulRightLeg.rotateDeg(-15 * sitFactor, X);

            float robeRotation = 20 * sitFactor;
            skeleton.MogulFrontRobe.rotateDeg(robeRotation, X);
            skeleton.MogulBackRobe.rotateDeg(-robeRotation, X);
            skeleton.MogulLeftRobe.rotateDeg(robeRotation, X);
            skeleton.MogulRightRobe.rotateDeg(robeRotation, X);

            skeleton.MogulLeftArm.rotateDeg(-5 * sitFactor, Z);
            skeleton.MogulRightArm.rotateDeg(5 * sitFactor, Z);

            skeleton.MogulLeftArm.offsetY(-5 * sitFactor);
            skeleton.MogulRightArm.offsetY(-5 * sitFactor);

            float handSitFactor = standing ? sitFactor : MathUtils.ease(Mth.clampedMap(mixFactor, 0.1F, 1.0F, 0.0F, 1.0F), MathUtils.EasingType.easeOutBack);

            Bone dominantHand = entity.isLeftHanded() ? skeleton.MogulLeftArm : skeleton.MogulRightArm;
            Bone dominantHandGrip = entity.isLeftHanded() ? skeleton.MogulLeftArmGrasp : skeleton.MogulRightArmGrasp;
            Bone nonDominantHand = entity.isLeftHanded() ? skeleton.MogulRightArm : skeleton.MogulLeftArm;

            dominantHand.rotation.slerp(dominantHand.baseRotation, sitFactor);
            dominantHand.rotateDeg(45 * handSitFactor, X);
            dominantHand.rotateDeg(12 * sitFactor, Z);

            dominantHandGrip.offsetZ(1.5F * sitFactor);
            dominantHandGrip.rotateDeg(-20 * sitFactor, Y);
            nonDominantHand.rotateDeg(10 * sitFactor * (entity.isLeftHanded() ? 1 : -1), Z);

            if (standing) {
                float biasedMiddleFactor = nSin(mixFactor * mixFactor);
                skeleton.MogulBody.rotateDeg(-10 * biasedMiddleFactor, X);
                skeleton.MogulNeck.rotateDeg(-8 * biasedMiddleFactor, X);

                skeleton.MogulLeftArm.rotateDeg(-10 * biasedMiddleFactor, X);
                skeleton.MogulRightArm.rotateDeg(-10 * biasedMiddleFactor, X);

                skeleton.MogulLeftArm.rotateDeg(-8 * biasedMiddleFactor, Z);
                skeleton.MogulRightArm.rotateDeg(8 * biasedMiddleFactor, Z);

                nonDominantHand.rotateDeg(30 * biasedMiddleFactor, X);
            }
        }
    }

    private static class MaskAnimation extends Animation<GnomadMogulEntity, GnomadMogulSkeleton> {
        protected static MaskAnimation INSTANCE = new MaskAnimation();
        @Override
        public void apply(GnomadMogulEntity entity, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            float headShake = Mth.sin(time * 0.44F) * mixFactor * 0.05F * Mth.RAD_TO_DEG;
            float faceShake = Mth.sin(time * 0.44F) * mixFactor * 0.25F * Mth.RAD_TO_DEG;

            skeleton.MogulHead.rotateDeg(headShake, Z);
            skeleton.MogulHelmetBase.rotateDeg(faceShake * -0.1F, Z);
            skeleton.MogulRightHelmetFlap.rotateDeg(-faceShake * 0.25F, X);
            skeleton.MogulLeftHelmetFlap .rotateDeg( faceShake * 0.25F, X);
            skeleton.MogulFace.rotateDeg(faceShake, Z);
        }
    }

    private static class IdleAnimation extends Animation<GnomadMogulEntity, GnomadMogulSkeleton> {
        protected static IdleAnimation INSTANCE = new IdleAnimation();

        @Override
        public void apply(GnomadMogulEntity entity, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            float bodyYaw = Mth.wrapDegrees(180 - entity.yBodyRot);

            float headYaw = Mth.wrapDegrees(180 - entity.yHeadRot);
            float netHeadYaw = Mth.degreesDifference(bodyYaw, headYaw);
            netHeadYaw = Mth.clamp(netHeadYaw, -80, 80);
            float headPitch = -entity.getViewXRot(1.0F);

            float globalSpeed = 1.0F / Mth.PI;

            skeleton.MogulRoot.rotateDeg(bodyYaw, Y);

            skeleton.MogulRightArm.offsetY(nSin(time * globalSpeed * 0.02F) * mixFactor * 0.5F);
            skeleton.MogulLeftArm.offsetY(nSin(time * globalSpeed * 0.02F) * mixFactor * 0.5F);

            skeleton.MogulLeftArm.rotateDeg(nSin(time * globalSpeed * 0.055F) * mixFactor * 1.0F, X);
            skeleton.MogulLeftArm.rotateDeg(-nSin(time * globalSpeed * 0.06F) * mixFactor * 1.0F, Z);
            skeleton.MogulRightArm.rotateDeg(nSin(time * globalSpeed * 0.062F) * mixFactor * 1.0F, X);
            skeleton.MogulRightArm.rotateDeg(-nSin(time * globalSpeed * 0.059F) * mixFactor * 1.0F, Z);

            skeleton.MogulNeck.rotateDeg(nSin(time * globalSpeed * 0.062F) * mixFactor * 1.0F, X);
            skeleton.MogulNeck.rotateDeg(-nSin(time * globalSpeed * 0.059F) * mixFactor * 1.0F, Z);

            skeleton.MogulLeftHelmetFlap .rotateDeg(-nSin(time * globalSpeed * 0.06F) * mixFactor * 3.0F - 8.0F, X);
            skeleton.MogulRightHelmetFlap.rotateDeg(-nSin(time * globalSpeed * 0.061F) * mixFactor * 3.0F - 8.0F, X);

            skeleton.MogulLeftRobe .rotateDeg(-nSin(time * globalSpeed * 0.059F) * mixFactor * 1.5F, X);
            skeleton.MogulRightRobe.rotateDeg(-nSin(time * globalSpeed * 0.060F) * mixFactor * 1.5F, X);
            skeleton.MogulFrontRobe.rotateDeg(-nSin(time * globalSpeed * 0.061F) * mixFactor * 1.5F, X);
            skeleton.MogulBackRobe .rotateDeg(-nSin(time * globalSpeed * 0.062F) * mixFactor * 1.5F, X);

            skeleton.MogulNeck.rotateDeg(netHeadYaw * 0.5F, Y);
            skeleton.MogulHead.rotateDeg(netHeadYaw * 0.5F, Y);
            skeleton.MogulNeck.rotateDeg(headPitch * 0.5F, X);
            skeleton.MogulHead.rotateDeg(headPitch * 0.5F, X);

            skeleton.MogulBackHelmetFlap.rotateDeg(-headPitch, X);
        }
    }

    private static class WalkAnimation extends Animation<GnomadMogulEntity, GnomadMogulSkeleton> {
        protected static WalkAnimation INSTANCE = new WalkAnimation();

        @Override
        public boolean running(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            float degree = 1.0F;
            float sign = Mth.sign(mixFactor);
            mixFactor = Mth.abs(mixFactor);

            float legBobAmountH = 6, legBobAmountV = 6;
            float legHeightOffset = 0.35F * legBobAmountV * mixFactor * degree;

            // bounce mix factor decreases with speed...
            float bounceMixFactor = mixFactor;
            if (mixFactor > 1.0F) bounceMixFactor = Mth.clampedMap(bounceMixFactor, 1.0F, 3.0F, mixFactor, 0);
            // squared mix factor really emphasizes movements when running
            float squaredMixFactor = mixFactor * mixFactor;

            // body movement
            skeleton.MogulRoot.offsetY(Math.abs(nSin(time + 0.5F)) * 2 * bounceMixFactor * degree);
            skeleton.MogulRoot.offsetZ(nSin(time * 2 + 0.7F) * bounceMixFactor * degree * sign);
            skeleton.MogulRoot.rotateDeg(-6 * mixFactor * degree * sign, X);

            skeleton.MogulNeck.offsetY(-nSin(time * 2 - 0.1F) * 0.2F * bounceMixFactor * degree);
            skeleton.MogulHead.offsetY(-nSin(time * 2 - 0.2F) * 0.2F * bounceMixFactor * degree);
            skeleton.MogulNeck.rotateDeg(3 * mixFactor * degree * sign, X);
            skeleton.MogulHead.rotateDeg(3 * mixFactor * degree * sign, X);

            skeleton.MogulLeftHelmetFlap.rotateDeg(nSin(time * 2 - 0.1F) * 3 * bounceMixFactor * degree, X);
            skeleton.MogulRightHelmetFlap.rotateDeg(nSin(time * 2 - 0.1F) * 3 * bounceMixFactor * degree, X);

            float flapRotation = -15 * mixFactor * degree * sign;
            flapRotation += 2 * nSin(time * 2 + 0.1F) * mixFactor * degree * sign;
            skeleton.MogulFrontRobe.rotateDeg(flapRotation * (sign > 0.0F ? 0.5F : 1.0F), X);
            skeleton.MogulBackRobe.rotateDeg(flapRotation * (sign > 0.0F ? 1.0F : 0.5F), X);

            // leg movement
            float clampedMixFactor = Math.min(mixFactor, 1.0F);
            skeleton.MogulLeftLeg.offsetZ(-nSin(time) * legBobAmountH * clampedMixFactor * degree * sign);
            float leftLegY = nSin(0.5F + time) * legBobAmountV * mixFactor * degree;
            if (leftLegY < 0) leftLegY *= 0.5F;
            skeleton.MogulLeftLeg.offsetY(leftLegY + legHeightOffset);
            skeleton.MogulLeftLeg.rotateDeg(-10 * sign * clampedMixFactor, X);
            skeleton.MogulLeftLeg.rotateDeg(-nSin(time) * mixFactor * -20 * sign * degree, X);

            skeleton.MogulRightLeg.offsetZ(nSin(time) * legBobAmountH * clampedMixFactor * degree * sign);
            float rightLegY = -nSin(0.5F + time) * legBobAmountV * mixFactor * degree;
            if (rightLegY < 0) rightLegY *= 0.5F;
            skeleton.MogulRightLeg.offsetY(rightLegY + legHeightOffset);
            skeleton.MogulRightLeg.rotateDeg(-10 * sign * clampedMixFactor, X);
            skeleton.MogulRightLeg.rotateDeg(nSin(time) * mixFactor * -20 * sign * degree, X);

            // arm movement
            float armCircleTime = time - 0.1F;

            skeleton.MogulLeftArm.rotateDeg(clampedMixFactor * degree * -5, Z);
            skeleton.MogulLeftArm.rotateDeg(clampedMixFactor * degree * -20, X);
            skeleton.MogulLeftArm.rotateDeg(-nSin(time) * mixFactor * degree * 20 * sign, X);
            skeleton.MogulLeftArm.offsetY(nSin(time * 2 - 0.5F) * sign * bounceMixFactor * degree);
            skeleton.MogulLeftArm.offsetZ(nSin(armCircleTime) * mixFactor * degree * 2 * sign);

            skeleton.MogulRightArm.rotateDeg(clampedMixFactor * degree * 5, Z);
            skeleton.MogulRightArm.rotateDeg(clampedMixFactor * degree * -20, X);
            skeleton.MogulRightArm.rotateDeg(-nSin(time) * mixFactor * degree * -20 * sign, X);
            skeleton.MogulRightArm.offsetY(nSin(time * 2 - 0.5F) * sign * bounceMixFactor * degree);
            skeleton.MogulRightArm.offsetZ(-nSin(armCircleTime) * mixFactor * degree * 2 * sign);
        }
    }

    private static class StrafeAnimation extends Animation<GnomadMogulEntity, GnomadMogulSkeleton> {
        protected static StrafeAnimation INSTANCE = new StrafeAnimation();

        @Override
        public boolean running(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            float degree = 1.0F;
            float sign = Mth.sign(mixFactor);
            mixFactor = Mth.abs(mixFactor);
            boolean movingLeft = sign > 0.0F;

            float legBobAmountH = 3, legBobAmountV = 6;
            float legHeightOffset = 0.35F * legBobAmountV * mixFactor * degree;

            // bounce mix factor decreases with speed...
            float bounceMixFactor = mixFactor;
            if (mixFactor > 1.0F) bounceMixFactor = Mth.clampedMap(bounceMixFactor, 1.0F, 3.0F, mixFactor, 0);
            // squared mix factor really emphasizes movements when running
            float squaredMixFactor = mixFactor * mixFactor;

            // body movement
            skeleton.MogulRoot.offsetY(Math.abs(nSin(time + 0.5F)) * 2 * bounceMixFactor * degree);
            skeleton.MogulRoot.offsetX(nSin(time * 2 + 0.7F) * bounceMixFactor * degree * sign);
            skeleton.MogulRoot.rotateDeg(10 * mixFactor * degree * sign, Z);

            skeleton.MogulNeck.offsetY(-nSin(time * 2 - 0.1F) * 0.2F * bounceMixFactor * degree);
            skeleton.MogulHead.offsetY(-nSin(time * 2 - 0.2F) * 0.2F * bounceMixFactor * degree);
            skeleton.MogulNeck.rotateDeg(-3 * mixFactor * degree * sign, Z);
            skeleton.MogulHead.rotateDeg(-3 * mixFactor * degree * sign, Z);

            float flapRotation = 15 * mixFactor * degree * sign;
            flapRotation += -2 * nSin(time * 2 + 0.1F) * mixFactor * degree * sign;
            skeleton.MogulLeftRobe.rotateDeg(-flapRotation * (movingLeft ? 0.5F : 1.0F), X);
            skeleton.MogulRightRobe.rotateDeg(flapRotation * (movingLeft ? 1.0F : 0.5F), X);

            // leg movement
            float clampedMixFactor = Math.min(mixFactor, 1.0F);
            skeleton.MogulLeftLeg.offsetX(-nSin(time) * legBobAmountH * clampedMixFactor * degree * sign);
            float leftLegY = nSin(0.5F + time) * legBobAmountV * mixFactor * degree;
            if (leftLegY < 0) leftLegY *= 0.5F;
            skeleton.MogulLeftLeg.offsetY(leftLegY + legHeightOffset);
            skeleton.MogulLeftLeg.rotateDeg(10 * sign * clampedMixFactor, Z);
            skeleton.MogulLeftLeg.rotateDeg(nSin(time) * mixFactor * -20 * sign * degree, Z);

            skeleton.MogulRightLeg.offsetX(nSin(time) * legBobAmountH * clampedMixFactor * degree * sign);
            float rightLegY = -nSin(0.5F + time) * legBobAmountV * mixFactor * degree;
            if (rightLegY < 0) rightLegY *= 0.5F;
            skeleton.MogulRightLeg.offsetY(rightLegY + legHeightOffset);
            skeleton.MogulRightLeg.rotateDeg(10 * sign * clampedMixFactor, Z);
            skeleton.MogulRightLeg.rotateDeg(-nSin(time) * mixFactor * -20 * sign * degree, Z);

            float armCircleTime = time - 0.1F;

            float armLeanFactor = sign > 0.0F ? 1.0F : 0.0F;
            skeleton.MogulLeftArm.rotateDeg(-nSin(time) * mixFactor * degree * 10 * sign, X);
            skeleton.MogulLeftArm.offsetY(nSin(time * 2 - 0.5F) * sign * bounceMixFactor * degree);
            skeleton.MogulLeftArm.offsetZ(nSin(armCircleTime) * mixFactor * degree * 2 * sign);
            if (!movingLeft) skeleton.MogulLeftArm.rotateDeg(clampedMixFactor * degree * -10, Z);

            skeleton.MogulRightArm.rotateDeg(-nSin(time) * mixFactor * degree * -10 * sign, X);
            skeleton.MogulRightArm.offsetY(nSin(time * 2 - 0.5F) * sign * bounceMixFactor * degree);
            skeleton.MogulRightArm.offsetZ(-nSin(armCircleTime) * mixFactor * degree * 2 * sign);
            if (movingLeft) skeleton.MogulRightArm.rotateDeg(clampedMixFactor * degree * 10, Z);

//            float globalDegree = 1.0F;
//
//            float legBobAmountH = 12;
//            float legBobAmountV = 12;
//            float timeOffset = Mth.PI * 0.5F;
//            float verticalOffset = 0F;
//
//            float sign = Mth.sign(mixFactor);
//            mixFactor = Mth.abs(mixFactor);
//
//            float legHeightOffset = -0.2F * legBobAmountV * mixFactor * globalDegree;
//            float sideIntensity = 0.2F;
//            skeleton.MogulLeftLeg.offsetX(-nSin(time + timeOffset) * legBobAmountH * mixFactor * globalDegree * 0.5F * sign);
//            float leftLegY = (-nSin(time + timeOffset) - verticalOffset) * legBobAmountV * mixFactor * globalDegree;
//            if (leftLegY < 0) leftLegY *= 0.2F;
//            skeleton.MogulLeftLeg.offsetY(leftLegY + legHeightOffset);
//            skeleton.MogulLeftLeg.rotateDeg(-30 * mixFactor * sign, Z);
//            skeleton.MogulLeftLeg.rotateDeg(nSin(time + timeOffset) * mixFactor * globalDegree * -20 * sideIntensity * sign, Z);
//            skeleton.MogulLeftLeg.rotateDeg(-nSin(time) * mixFactor * globalDegree * -20 * sideIntensity * sign, Z);
//
//            skeleton.MogulRightLeg.offsetX(nSin(time) * legBobAmountH * mixFactor * globalDegree * 0.5F * sign);
//            float rightLegY = (-nSin(time) - verticalOffset) * legBobAmountV * mixFactor * globalDegree;
//            if (rightLegY < 0) rightLegY *= 0.2F;
//            skeleton.MogulRightLeg.offsetY(rightLegY + legHeightOffset);
//            skeleton.MogulRightLeg.rotateDeg(-30 * mixFactor * sign, Z);
//            skeleton.MogulRightLeg.rotateDeg(nSin(time + timeOffset) * mixFactor * globalDegree * 20 * sideIntensity * sign, Z);
//            skeleton.MogulRightLeg.rotateDeg(-nSin(time) * mixFactor * globalDegree * 20 * sideIntensity * sign, Z);
//
//            skeleton.MogulRoot.rotateDeg(-10 * mixFactor * globalDegree * sign, Z);
//
//            skeleton.MogulRoot.offsetY(nSin(time * 2) * mixFactor * globalDegree);
//            skeleton.MogulRoot.offsetX(nSin(time * 2 - 1F) * mixFactor * globalDegree * -2 * sign);
//            skeleton.MogulRoot.rotateDeg(-10 * mixFactor * globalDegree, X);
//
//            skeleton.MogulLeftRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -5, 20) * mixFactor * globalDegree, X);
//            skeleton.MogulLeftRobe.rotateDeg(-nSin(time - 0.3F) * mixFactor * globalDegree * 10 * (sign * 0.5F + 0.5F), X);
//            skeleton.MogulRightRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, 20, -5) * mixFactor * globalDegree, X);
//            skeleton.MogulRightRobe.rotateDeg(nSin(time + Mth.HALF_PI + 0.3F) * mixFactor * globalDegree * 10 * (-sign * 0.5F + 0.5F), X);
//
//            skeleton.MogulLeftArm.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -20, -10) * mixFactor * globalDegree, Z);
//            skeleton.MogulRightArm.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, 10, 20) * mixFactor * globalDegree, Z);
//
//            skeleton.MogulNeck.rotateDeg(10 * mixFactor * globalDegree * sign, Z);
        }
    }

    private static class FloatAnimation extends Animation<GnomadMogulEntity, GnomadMogulSkeleton> {
        protected static FloatAnimation INSTANCE = new FloatAnimation();

        private FloatAnimation() {
            super();
        }

        @Override
        public void apply(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            float globalDegree = 1.0F;
            float robeRotationFactor = (float) Math.clamp(parent.getDeltaMovement().y / -0.05F, 0, 1);
            skeleton.MogulLeftRobe.rotateDeg(15 * mixFactor * globalDegree * robeRotationFactor, X);
            skeleton.MogulRightRobe.rotateDeg(15 * mixFactor * globalDegree * robeRotationFactor, X);
            skeleton.MogulFrontRobe.rotateDeg(18 * mixFactor * globalDegree * robeRotationFactor, X);
            skeleton.MogulBackRobe.rotateDeg(-18 * mixFactor * globalDegree * robeRotationFactor, X);

            skeleton.MogulLeftArm.rotateDeg(-15 * mixFactor * globalDegree * robeRotationFactor, Z);
            skeleton.MogulLeftArm.rotateDeg(-22 * mixFactor * globalDegree, X);
            skeleton.MogulLeftArm.rotateDeg(5 * nSin(time * 0.07F) * mixFactor * globalDegree, Z);
            skeleton.MogulLeftArm.rotateDeg(5 * -nSin(time * 0.07F) * mixFactor * globalDegree, X);
            skeleton.MogulLeftArm.offsetY(-5 * mixFactor * globalDegree * (1 - robeRotationFactor));

            skeleton.MogulRightArm.rotateDeg(15 * mixFactor * globalDegree * robeRotationFactor, Z);
            skeleton.MogulRightArm.rotateDeg(-22 * mixFactor * globalDegree, X);
            skeleton.MogulRightArm.rotateDeg(-5 * nSin(time * 0.07F) * mixFactor * globalDegree, Z);
            skeleton.MogulRightArm.rotateDeg(-5 * -nSin(time * 0.07F) * mixFactor * globalDegree, X);
            skeleton.MogulRightArm.offsetY(-5 * mixFactor * globalDegree * (1 - robeRotationFactor));

            skeleton.MogulLeftLeg.rotateDeg(-8 * mixFactor * globalDegree * robeRotationFactor, Z);
            skeleton.MogulLeftLeg.rotateDeg(8 * nSin(time * 0.05F) * mixFactor * globalDegree, Z);
            skeleton.MogulLeftLeg.rotateDeg(8 * -nSin(time * 0.05F) * mixFactor * globalDegree, X);

            skeleton.MogulRightLeg.rotateDeg(8 * mixFactor * globalDegree * robeRotationFactor, Z);
            skeleton.MogulRightLeg.rotateDeg(-8 * nSin(time * 0.05F) * mixFactor * globalDegree, Z);
            skeleton.MogulRightLeg.rotateDeg(-8 * -nSin(time * 0.05F) * mixFactor * globalDegree, X);

            Vec3 velocity = parent.getDeltaMovement();
            float walkAmount = parent.getForwardLocomotionAmount(1.0F), strafeAmount = parent.getStrafeLocomotionAmount(1.0F);
            if (velocity.x - strafeAmount != 0 || velocity.z + walkAmount != 0) {
                velocity = velocity.multiply(1, 0, 1);
                // rotate the velocity based off the body rot
                float bodyRot = -parent.getPreciseBodyRotation(1.0F) * Mth.DEG_TO_RAD;
                float xFac = (float) (velocity.x * -nSin(bodyRot) - velocity.z * nSin(bodyRot)) * 0.5F - strafeAmount;
                float zFac = (float) (velocity.x * nSin(bodyRot) + velocity.z * -nSin(bodyRot)) * 0.5F + walkAmount;
                velocity = new Vec3(xFac, 0, zFac);

                float angle = (float) velocity.length() * 5 * mixFactor * globalDegree;
                velocity = velocity.normalize().cross(new Vec3(0, 1, 0));

                if (velocity.x != 0 || velocity.z != 0) {
                    skeleton.MogulRoot.rotation.rotateAxis(angle, (float) velocity.x, 0, (float) velocity.z);

                    angle = (float) (MathUtils.smoothClampExpo(angle * Mth.RAD_TO_DEG, -20F, 20F, 5.0F) * Mth.DEG_TO_RAD);
                    skeleton.MogulLeftLeg.rotation.rotateAxis(angle, (float) velocity.x, (float) velocity.y, (float) velocity.z);
                    skeleton.MogulRightLeg.rotation.rotateAxis(angle, (float) velocity.x, (float) velocity.y, (float) velocity.z);

                    skeleton.MogulLeftArm.rotation.rotateAxis(angle * -0.5F, (float) velocity.x, (float) velocity.y, (float) velocity.z);
                    skeleton.MogulRightArm.rotation.rotateAxis(angle * -0.5F, (float) velocity.x, (float) velocity.y, (float) velocity.z);
                    skeleton.MogulNeck.rotation.rotateAxis(angle * -0.4F, (float) velocity.x, (float) velocity.y, (float) velocity.z);
                    skeleton.MogulHead.rotation.rotateAxis(angle * -0.4F, (float) velocity.x, (float) velocity.y, (float) velocity.z);
                }
            }

            float moveTime = parent.getCumulativeLocomotionAmount() * 2.3F;
            float walkFac = Mth.sqrt(walkAmount * walkAmount + strafeAmount * strafeAmount) * 10;
            skeleton.MogulLeftLeg.rotateDeg(20 * nSin(moveTime) * mixFactor * globalDegree * walkFac, X);
            skeleton.MogulRightLeg.rotateDeg(20 * nSin(moveTime + Mth.PI) * mixFactor * globalDegree * walkFac, X);

            skeleton.MogulRoot.offsetY(nSin(parent.tickCount * 0.1F) * 2 * mixFactor * globalDegree);
        }
    }

    private static class AttackAnimation extends Animation<GnomadMogulEntity, GnomadMogulSkeleton> {
        protected final int animLength, windupLength, swingLength, recoveryLength;
        private AttackAnimation(MogulAttackHandler.MogulAttackType attackType) {
            this.animLength = attackType.length;
            this.windupLength = attackType.windupLength;
            this.swingLength = attackType.swingLength;
            this.recoveryLength = attackType.recoveryLength;
        }
    }

    private static class UpAttackAnimation extends AttackAnimation {
        protected static UpAttackAnimation INSTANCE = new UpAttackAnimation();

        private UpAttackAnimation() {
            super(MogulAttackHandler.SWING_UP);
        }

        @Override
        public void apply(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            float timeDelayed = time - 0.2F;
            float blend = 1 - Mth.clamp((time - (windupLength + swingLength)) / recoveryLength, 0, 1);
            blend = MathUtils.smoothstep(blend) * mixFactor;
            {   // windup anim
                float windupTime = Mth.clamp(time / windupLength, 0, 1);
                float windupTimeDelayed = Mth.clamp(timeDelayed / windupLength, 0, 1);

                float weaponSpinTime = Mth.clamp(time / (windupLength * 1.5F), 0, 1);
                float weaponSpin = MathUtils.ease(weaponSpinTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArmGrasp.rotateDeg(-40 * weaponSpin * blend, X);
                skeleton.MogulRightArmGrasp.rotateDeg(180 * weaponSpin * blend, Y);
                Vector3f movement = skeleton.MogulRightArmGrasp.rotation.transform(0, 1, 0, new Vector3f());
                float movementAmount = 5 * weaponSpin * blend;
                skeleton.MogulRightArmGrasp.offsetX(movement.x * movementAmount);
                skeleton.MogulRightArmGrasp.offsetY(movement.y * movementAmount);
                skeleton.MogulRightArmGrasp.offsetZ(movement.z * movementAmount);

                float armRaise = MathUtils.ease(windupTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArm.rotateDeg(30 * armRaise * blend, Y);
                skeleton.MogulRightArm.rotateDeg(30 * armRaise * blend, X);
                skeleton.MogulRightArm.offsetZ(-8 * MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutQuad) * blend);

                float bodyRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRoot.rotateDeg(5 * bodyRot * blend, Z);
                skeleton.MogulRoot.rotateDeg(-15 * bodyRot * blend, X);
                skeleton.MogulFrontRobe.rotateDeg(15 * bodyRot * blend, X);
                skeleton.MogulBackRobe.rotateDeg(15 * bodyRot * blend, X);

                float neckRot = MathUtils.ease(windupTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulNeck.rotateDeg(15 * bodyRot * blend, X);
                skeleton.MogulNeck.rotateDeg(-15 * neckRot * blend, X);

                float armRot = MathUtils.ease(windupTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulLeftArm.rotateDeg(15 * bodyRot * blend, X);
                skeleton.MogulLeftArm.rotateDeg(-15 * armRot * blend, X);
            }
            {   // swing anim
                float swingTime = Mth.clamp((time - windupLength) / swingLength, 0, 1);
                float swingTimeDelayed = Mth.clamp((timeDelayed - windupLength) / swingLength, 0, 1);

                float armRaise = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArm.rotateDeg(-40 * armRaise * blend, Y);
                skeleton.MogulRightArm.rotateDeg(90 * armRaise * blend, X);

                float bodyRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRoot.rotateDeg(-10 * bodyRot * blend, Z);
                skeleton.MogulRoot.rotateDeg(30 * bodyRot * blend, X);
                skeleton.MogulFrontRobe.rotateDeg(-40 * bodyRot * blend, X);
                skeleton.MogulBackRobe.rotateDeg(-40 * bodyRot * blend, X);

                float neckRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeOutBack);
                skeleton.MogulNeck.rotateDeg(-30 * bodyRot * blend, X);
                skeleton.MogulNeck.rotateDeg(30 * neckRot * blend, X);

                float armRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulLeftArm.rotateDeg(15 * bodyRot * blend, X);
                skeleton.MogulLeftArm.rotateDeg(-15 * armRot * blend, X);
            }
        }
    }

    private static class LeftAttackAnimation extends AttackAnimation {
        protected static LeftAttackAnimation INSTANCE = new LeftAttackAnimation();

        private LeftAttackAnimation() {
            super(MogulAttackHandler.SWING_LEFT);
        }

        @Override
        public void apply(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            float timeDelayed = time - 0.1F;
            float blend = 1 - Mth.clamp((time - (windupLength + swingLength)) / recoveryLength, 0, 1);

            blend = MathUtils.smoothstep(blend) * mixFactor;
            {   // windup anim
                float windupTime = Mth.clamp(time / windupLength, 0, 1);
                float windupTimeDelayed = Mth.clamp(timeDelayed / windupLength, 0, 1);
                float flap = nSin(2 * Mth.PI * windupTime) * 0.5F + 0.5F;

                float armRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRightArm.rotateDeg(100 * armRot * blend, X);
                skeleton.MogulRightArm.rotateDeg(40 * armRot * blend, Z);
                skeleton.MogulRightArm.rotateDeg(-40 * armRot * blend, Y);

                skeleton.MogulLeftArm.rotateDeg(4 * flap * blend, Z);

                float offset = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutQuad);
                skeleton.MogulRightArm.offsetZ(-3 * offset * blend);
                skeleton.MogulRightArm.offsetX(-2 * offset * blend);
                skeleton.MogulRightArmGrasp.offsetZ(-8 * offset * blend);

                float bodyRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRoot.rotateDeg(5 * bodyRot * blend, X);
                skeleton.MogulRoot.rotateDeg(-15 * bodyRot * blend, Y);

                float headRot = MathUtils.ease(windupTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulNeck.rotateDeg(-5 * headRot * blend, X);
                skeleton.MogulNeck.rotateDeg(15 * headRot * blend, Y);

                skeleton.MogulLeftRobe.rotateDeg(5 * flap * blend, X);
                skeleton.MogulRightRobe.rotateDeg(-5 * flap * blend, X);
            }
            {   // swing anim
                float swingTime = Mth.clamp((time - windupLength) / swingLength, 0, 1);
                float swingTimeDelayed = Mth.clamp((timeDelayed - windupLength) / swingLength, 0, 1);
                float flap = nSin(2 * Mth.PI * swingTime) * 0.5F + 0.5F;

                float armRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRightArm.rotateDeg(-20 * armRot * blend, Y);
                skeleton.MogulRightArm.rotateDeg(-120 * armRot * blend, X);
                //skeleton.MogulRightArm.rotateDeg(-60 * armRot * blend, Z);
                float offset = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArm.offsetX(2 * offset * blend);
                skeleton.MogulRightArm.offsetZ(-8 * offset * blend);

                skeleton.MogulLeftArm.rotateDeg(-5 * flap * blend, Z);
                float otherArmRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeOutBack);
                skeleton.MogulLeftArm.rotateDeg(-10 * otherArmRot * blend, Z);

                float bodyRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRoot.rotateDeg(-20 * bodyRot * blend, X);
                skeleton.MogulRoot.rotateDeg(30 * bodyRot * blend, Y);

                float headRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeOutBack);
                skeleton.MogulNeck.rotateDeg(20 * headRot * blend, X);
                skeleton.MogulNeck.rotateDeg(-30 * headRot * blend, Y);
                skeleton.MogulHead.rotateDeg(15 * headRot * blend, Z);

                skeleton.MogulLeftRobe.rotateDeg(8 * flap * blend, X);
                skeleton.MogulRightRobe.rotateDeg(-8 * flap * blend, X);

                skeleton.MogulHelmetBase.rotateDeg(5 * flap * blend, Z);
            }
        }
    }

    private static class RightAttackAnimation extends AttackAnimation {
        protected static RightAttackAnimation INSTANCE = new RightAttackAnimation();

        private RightAttackAnimation() {
            super(MogulAttackHandler.SWING_RIGHT);
        }

        @Override
        public void apply(GnomadMogulEntity parent, GnomadMogulSkeleton skeleton, float mixFactor, float time) {
            float timeDelayed = time - 0.2F;
            float timeDelayedForward = time + 0.1F;

            float blend = 1 - Mth.clamp((time - (windupLength + swingLength)) / recoveryLength, 0, 1);
            blend = MathUtils.smoothstep(blend) * mixFactor;
            {   // windup anim
                float windupTime = Mth.clamp(time / windupLength, 0, 1);
                float windupTimeDelayed = Mth.clamp(timeDelayed / windupLength, 0, 1);
                float windupTimeDelayedForward = Mth.clamp(timeDelayedForward / windupLength, 0, 1);

                float armRaise = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRightArm.rotateDeg(30 * armRaise * blend, X);
                skeleton.MogulRightArm.rotateDeg(80 * armRaise * blend, Y);
                skeleton.MogulRightArm.offsetZ(-8 * MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutQuad) * blend);

                float armRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRightArm.rotateDeg(30 * armRot * blend, X);
                float otherArmRot = MathUtils.ease(windupTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulLeftArm.rotateDeg(-4 * otherArmRot * blend, Z);

                float bodyRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRoot.rotateDeg(15 * bodyRot * blend, Y);
                float neckRot = MathUtils.ease(windupTimeDelayedForward, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulNeck.rotateDeg(-15 * neckRot * blend, Y);
            }
            {   // swing anim
                float swingTime = Mth.clamp((time - windupLength) / swingLength, 0, 1);
                float swingTimeDelayed = Mth.clamp((timeDelayed - windupLength) / swingLength, 0, 1);

                float armRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArm.rotateDeg(-90 * armRot * blend, X);

                float bodyRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRoot.rotateDeg(-30 * bodyRot * blend, Y);
                skeleton.MogulNeck.rotateDeg(30 * bodyRot * blend, Y);

                float otherArmRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeOutBack);
                skeleton.MogulLeftArm.rotateDeg(4 * otherArmRot * blend, Z);
            }

            float windupTime = Mth.clamp((time - 0.2F) / (windupLength+swingLength), 0, 1);
            Vector3f movement = skeleton.MogulRightArmGrasp.rotation.transform(0, 1, 0, new Vector3f());
            float movementAmount = 10 * windupTime * blend;
            skeleton.MogulRightArmGrasp.offsetX(movement.x * movementAmount);
            skeleton.MogulRightArmGrasp.offsetY(movement.y * movementAmount);
            skeleton.MogulRightArmGrasp.offsetZ(movement.z * movementAmount);
        }
    }
}