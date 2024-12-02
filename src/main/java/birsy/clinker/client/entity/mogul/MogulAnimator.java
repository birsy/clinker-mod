package birsy.clinker.client.entity.mogul;

import birsy.clinker.common.world.entity.gnomad.mogul.MogulAttackHandler;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.necromancer.animation.Animation;
import birsy.necromancer.animation.Animator;
import birsy.clinker.common.world.entity.gnomad.mogul.GnomadMogulEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.tslat.smartbrainlib.util.RandomUtil;
import org.joml.Vector3f;

public class MogulAnimator extends Animator<GnomadMogulEntity, MogulSkeleton> {
    public final AnimationEntry<?, ?> idleAnim, maskAnim, walkAnim, strafeAnim;
    public final TimedAnimationEntry<?, ?> upSwingAnim, leftSwingAnim, rightSwingAnim;
    private int maskShakeTime = 0, maskShakeDuration = 1;
    private boolean maskShaking = false;

    private final Vector3f smoothedAcceleration = new Vector3f();
    private static final Vector3f temp = new Vector3f();

    protected MogulAnimator(GnomadMogulEntity parent, MogulSkeleton skeleton) {
        super(parent, skeleton);
        this.upSwingAnim = this.addTimedAnimation(UpAttackAnimation.INSTANCE, 3, UpAttackAnimation.INSTANCE.animLength);
        this.leftSwingAnim = this.addTimedAnimation(LeftAttackAnimation.INSTANCE, 3, LeftAttackAnimation.INSTANCE.animLength);
        this.rightSwingAnim = this.addTimedAnimation(RightAttackAnimation.INSTANCE, 3, RightAttackAnimation.INSTANCE.animLength);

        this.strafeAnim = this.addAnimation(StrafeAnimation.INSTANCE, 2);
        this.walkAnim = this.addAnimation(WalkAnimation.INSTANCE, 1);
        this.maskAnim = this.addAnimation(MaskAnimation.INSTANCE, 0);
        this.idleAnim = this.addAnimation(IdleAnimation.INSTANCE, 0);
    }

    private void updateAttackAnim(TimedAnimationEntry<?, ?> attack) {
        if (attack.playing) {
            attack.setMixFactor(Mth.lerp(0.3F, attack.getMixFactor(), 1));
        } else {
            attack.setMixFactor(Mth.lerp(0.1F, attack.getMixFactor(), 0));
        }
    }

    @Override
    public void animate(GnomadMogulEntity entity) {
        super.animate(entity);
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
        this.maskAnim.setMixFactor(shakeAmount * shakeAmount * shakeAmount * shakeAmount * 0.5F);
        this.maskAnim.setTime(entity.tickCount);

        // locomotion
        float moveTime = entity.getCumulativeWalk() * 2.3F;

        float walkFac = Mth.clamp(5 * entity.getWalkAmount(1.0F), -0.8F, 0.8F);
        this.walkAnim.setMixFactor(walkFac);
        this.walkAnim.setTime(moveTime);

        float strafeFac = Mth.clamp(5 * -entity.getStrafeAmount(1.0F), -0.8F, 0.8F);
        this.strafeAnim.setMixFactor(strafeFac);
        this.strafeAnim.setTime(strafeFac);

        // flinch
        if (entity.hurtDuration > 0) {
            float flinchTime = entity.tickCount;
            float flinchFactor = (float) entity.hurtTime / entity.hurtDuration;
            skeleton.MogulRoot.rotateDeg(Mth.sin(flinchTime) * 4 * flinchFactor, Direction.Axis.X);
            skeleton.MogulRoot.rotateDeg(Mth.cos(flinchTime) * 4 * flinchFactor, Direction.Axis.Z);

            skeleton.MogulHead.rotateDeg(Mth.sin(flinchTime - 1) * 8 * flinchFactor, Direction.Axis.X);
            skeleton.MogulHead.rotateDeg(Mth.cos(flinchTime - 1) * 8 * flinchFactor, Direction.Axis.Z);

            skeleton.MogulRightArm.rotateDeg(Mth.sin(flinchTime - 1) * 8 * flinchFactor, Direction.Axis.X);
            skeleton.MogulRightArm.rotateDeg(Mth.cos(flinchTime - 1) * 8 * flinchFactor, Direction.Axis.Z);
            skeleton.MogulLeftArm.rotateDeg(Mth.sin(flinchTime - 1) * 8 * flinchFactor, Direction.Axis.X);
            skeleton.MogulLeftArm.rotateDeg(Mth.cos(flinchTime - 1) * 8 * flinchFactor, Direction.Axis.Z);
        }

        // death
        if (entity.isDeadOrDying()) {
            float deathFactor = Mth.clamp(entity.deathTime / 17F, 0, 1);
            deathFactor = MathUtils.ease(deathFactor, MathUtils.EasingType.easeOutBack);
            skeleton.MogulBody.rotation.rotateAxis(deathFactor * 70 * Mth.DEG_TO_RAD, Mth.sqrt(2), 0, Mth.sqrt(2));
        }

        smoothedAcceleration.lerp(temp.set(entity.acceleration.x(), entity.acceleration.y(), entity.acceleration.z()), 0.15F);

        Vector3f axis = smoothedAcceleration.mul(1, 0, 1, temp);
        if (axis.lengthSquared() > 0) {
            float angle = axis.length();
            axis = axis.normalize().cross(0, 1, 0);
            skeleton.MogulRoot.rotation.rotateAxis(angle * 2, axis);
        }

        if (smoothedAcceleration.y() < 0) {
            skeleton.MogulRoot.ySize = Mth.lerp(Mth.clamp(smoothedAcceleration.y() * -5, 0, 1), 1, 0.5F);
        }
    }

    private static class MaskAnimation extends Animation<GnomadMogulEntity, MogulSkeleton> {
        protected static MaskAnimation INSTANCE = new MaskAnimation();
        @Override
        public void apply(GnomadMogulEntity entity, MogulSkeleton skeleton, float mixFactor, float time) {
            float headShake = Mth.sin(time * 0.44F) * mixFactor * 0.05F * Mth.RAD_TO_DEG;
            float faceShake = Mth.sin(0.5F + time * 0.44F) * mixFactor * 0.25F * Mth.RAD_TO_DEG;

            skeleton.MogulHead.rotateDeg(headShake, Direction.Axis.Z);
            skeleton.MogulHelmetBase.rotateDeg(faceShake * -0.1F, Direction.Axis.Z);
            skeleton.MogulRightHelmetFlap.rotateDeg(-faceShake * 0.25F, Direction.Axis.X);
            skeleton.MogulLeftHelmetFlap .rotateDeg( faceShake * 0.25F, Direction.Axis.X);
            skeleton.MogulFace.rotateDeg(faceShake, Direction.Axis.Z);
        }
    }

    private static class IdleAnimation extends Animation<GnomadMogulEntity, MogulSkeleton> {
        protected static IdleAnimation INSTANCE = new IdleAnimation();
        @Override
        public void apply(GnomadMogulEntity entity, MogulSkeleton skeleton, float mixFactor, float time) {
            float bodyYaw = 180 - entity.yBodyRot;
            float netHeadYaw = -(entity.yHeadRot - entity.yBodyRot);
            float headPitch =  -entity.getViewXRot(1.0F);

            float globalSpeed = 1.0F;

            skeleton.MogulRoot.rotateDeg(bodyYaw, Direction.Axis.Y);

            skeleton.MogulRightArm.y += Mth.sin(time * globalSpeed * 0.02F) * mixFactor * 0.5F;
            skeleton.MogulLeftArm.y += Mth.sin(time * globalSpeed * 0.02F) * mixFactor * 0.5F;

            skeleton.MogulLeftArm.rotateDeg(Mth.sin(time * globalSpeed * 0.055F) * mixFactor * 1.0F, Direction.Axis.X);
            skeleton.MogulLeftArm.rotateDeg(Mth.cos(time * globalSpeed * 0.06F) * mixFactor * 1.0F, Direction.Axis.Z);
            skeleton.MogulRightArm.rotateDeg(Mth.sin(time * globalSpeed * 0.062F) * mixFactor * 1.0F, Direction.Axis.X);
            skeleton.MogulRightArm.rotateDeg(Mth.cos(time * globalSpeed * 0.059F) * mixFactor * 1.0F, Direction.Axis.Z);

            skeleton.MogulNeck.rotateDeg(Mth.sin(time * globalSpeed * 0.062F) * mixFactor * 1.0F, Direction.Axis.X);
            skeleton.MogulNeck.rotateDeg(Mth.cos(time * globalSpeed * 0.059F) * mixFactor * 1.0F, Direction.Axis.Z);

            skeleton.MogulLeftHelmetFlap .rotateDeg(Mth.cos(time * globalSpeed * 0.06F) * mixFactor * 3.0F - 8.0F, Direction.Axis.X);
            skeleton.MogulRightHelmetFlap.rotateDeg(Mth.cos(time * globalSpeed * 0.061F) * mixFactor * 3.0F - 8.0F, Direction.Axis.X);

            skeleton.MogulLeftRobe .rotateDeg(Mth.cos(time * globalSpeed * 0.059F) * mixFactor * 1.5F, Direction.Axis.X);
            skeleton.MogulRightRobe.rotateDeg(Mth.cos(time * globalSpeed * 0.060F) * mixFactor * 1.5F, Direction.Axis.X);
            skeleton.MogulFrontRobe.rotateDeg(Mth.cos(time * globalSpeed * 0.061F) * mixFactor * 1.5F, Direction.Axis.X);
            skeleton.MogulBackRobe .rotateDeg(Mth.cos(time * globalSpeed * 0.062F) * mixFactor * 1.5F, Direction.Axis.X);

            skeleton.MogulNeck.rotateDeg(netHeadYaw * 0.5F, Direction.Axis.Y);
            skeleton.MogulHead.rotateDeg(netHeadYaw * 0.5F, Direction.Axis.Y);
            skeleton.MogulNeck.rotateDeg(headPitch * 0.5F, Direction.Axis.X);
            skeleton.MogulHead.rotateDeg(headPitch * 0.5F, Direction.Axis.X);

            skeleton.MogulBackHelmetFlap.rotateDeg(-headPitch, Direction.Axis.X);
        }
    }

    private static class WalkAnimation extends Animation<GnomadMogulEntity, MogulSkeleton> {
        protected static WalkAnimation INSTANCE = new WalkAnimation();
        @Override
        public void apply(GnomadMogulEntity parent, MogulSkeleton skeleton, float mixFactor, float time) {
            float globalDegree = 1.0F;

            float legBobAmountH = 12;
            float legBobAmountV = 12;
            float timeOffset = Mth.PI * 0.5F;
            float verticalOffset = 0F;

            float sign = Mth.sign(mixFactor);
            float legHeightOffset = 0.35F * legBobAmountV * mixFactor * globalDegree;

            skeleton.MogulLeftLeg.z += Mth.cos(time + timeOffset) * legBobAmountH * mixFactor * globalDegree * sign;
            float leftLegY = (Mth.sin(time + timeOffset) - verticalOffset) * legBobAmountV * mixFactor * globalDegree;
            if (leftLegY < 0) leftLegY *= 0.5F;
            skeleton.MogulLeftLeg.y += leftLegY + legHeightOffset;
            skeleton.MogulLeftLeg.rotateDeg(-30 * mixFactor, Direction.Axis.X);
            skeleton.MogulLeftLeg.rotateDeg(Mth.sin(time + timeOffset) * mixFactor * globalDegree * -20 * sign, Direction.Axis.X);
            skeleton.MogulLeftLeg.rotateDeg(Mth.cos(time) * mixFactor * globalDegree * -20, Direction.Axis.X);

            skeleton.MogulRightLeg.z += Mth.sin(time) * legBobAmountH * mixFactor * globalDegree * sign;
            float rightLegY = (-Mth.cos(time) - verticalOffset) * legBobAmountV * mixFactor * globalDegree;
            if (rightLegY < 0) rightLegY *= 0.5F;
            skeleton.MogulRightLeg.y += rightLegY + legHeightOffset;
            skeleton.MogulRightLeg.rotateDeg(-30 * mixFactor, Direction.Axis.X);
            skeleton.MogulRightLeg.rotateDeg(Mth.sin(time + timeOffset) * mixFactor * globalDegree * 20 * sign, Direction.Axis.X);
            skeleton.MogulRightLeg.rotateDeg(Mth.cos(time) * mixFactor * globalDegree * 20, Direction.Axis.X);

            skeleton.MogulLeftArm.rotateDeg(-58 * mixFactor * (-sign * 0.5F + 0.5F), Direction.Axis.X);
            skeleton.MogulLeftArm.rotateDeg(Mth.sin(time + timeOffset) * mixFactor * globalDegree * 20 * sign, Direction.Axis.X);
            skeleton.MogulLeftArm.rotateDeg(Mth.cos(time) * mixFactor * globalDegree * 20 * sign, Direction.Axis.X);
            float armCircleTime = time + (Mth.HALF_PI - 1);
            skeleton.MogulLeftArm.z += Mth.cos(armCircleTime) * mixFactor * globalDegree * 5 * sign - 1 * mixFactor * globalDegree;

            skeleton.MogulRightArm.rotateDeg(-58 * mixFactor * (-sign * 0.5F + 0.5F), Direction.Axis.X);
            skeleton.MogulRightArm.rotateDeg(Mth.sin(time + timeOffset) * mixFactor * globalDegree * -20 * sign, Direction.Axis.X);
            skeleton.MogulRightArm.rotateDeg(Mth.cos(time) * mixFactor * globalDegree * -20 * sign, Direction.Axis.X);
            skeleton.MogulRightArm.z += Mth.sin(armCircleTime) * mixFactor * globalDegree * 5 * sign - 1 * mixFactor * globalDegree;

            skeleton.MogulRoot.y += Mth.sin(time * 2) * mixFactor * globalDegree * 2;
            skeleton.MogulRoot.z += Mth.sin(time * 2 - 1F) * mixFactor * globalDegree * 2 * sign;
            skeleton.MogulRoot.rotateDeg(-10 * mixFactor * globalDegree * sign, Direction.Axis.X);

            skeleton.MogulFrontRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -30, -10) * mixFactor * globalDegree * sign, Direction.Axis.X);
            skeleton.MogulBackRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -10, -30) * mixFactor * globalDegree * sign, Direction.Axis.X);

            skeleton.MogulNeck.rotateDeg(10 * mixFactor * globalDegree * sign, Direction.Axis.X);
        }
    }

    private static class StrafeAnimation extends Animation<GnomadMogulEntity, MogulSkeleton> {
        protected static StrafeAnimation INSTANCE = new StrafeAnimation();
        @Override
        public void apply(GnomadMogulEntity parent, MogulSkeleton skeleton, float mixFactor, float time) {
            float globalDegree = 1.0F;

            float legBobAmountH = 12;
            float legBobAmountV = 12;
            float timeOffset = Mth.PI * 0.5F;
            float verticalOffset = 0F;

            float sign = -Mth.sign(mixFactor);

            float legHeightOffset = -0.2F * legBobAmountV * mixFactor * globalDegree;
            float sideIntensity = 0.2F;
            skeleton.MogulLeftLeg.x += Mth.cos(time + timeOffset) * legBobAmountH * mixFactor * globalDegree * 0.5F * sign;
            float leftLegY = (-Mth.sin(time + timeOffset) - verticalOffset) * legBobAmountV * mixFactor * globalDegree;
            if (leftLegY < 0) leftLegY *= 0.2F;
            skeleton.MogulLeftLeg.y += leftLegY + legHeightOffset;
            skeleton.MogulLeftLeg.rotateDeg(-30 * mixFactor * sign, Direction.Axis.Z);
            skeleton.MogulLeftLeg.rotateDeg(Mth.sin(time + timeOffset) * mixFactor * globalDegree * -20 * sideIntensity * sign, Direction.Axis.Z);
            skeleton.MogulLeftLeg.rotateDeg(Mth.cos(time) * mixFactor * globalDegree * -20 * sideIntensity * sign, Direction.Axis.Z);

            skeleton.MogulRightLeg.x += Mth.sin(time) * legBobAmountH * mixFactor * globalDegree * 0.5F * sign;
            float rightLegY = (Mth.cos(time) - verticalOffset) * legBobAmountV * mixFactor * globalDegree;
            if (rightLegY < 0) rightLegY *= 0.2F;
            skeleton.MogulRightLeg.y += rightLegY + legHeightOffset;
            skeleton.MogulRightLeg.rotateDeg(-30 * mixFactor * sign, Direction.Axis.Z);
            skeleton.MogulRightLeg.rotateDeg(Mth.sin(time + timeOffset) * mixFactor * globalDegree * 20 * sideIntensity * sign, Direction.Axis.Z);
            skeleton.MogulRightLeg.rotateDeg(Mth.cos(time) * mixFactor * globalDegree * 20 * sideIntensity * sign, Direction.Axis.Z);

            skeleton.MogulRoot.rotateDeg(-10 * mixFactor * globalDegree * sign, Direction.Axis.Z);

            skeleton.MogulRoot.y += Mth.sin(time * 2) * mixFactor * globalDegree;
            skeleton.MogulRoot.x += Mth.sin(time * 2 - 1F) * mixFactor * globalDegree * -2 * sign;
            skeleton.MogulRoot.rotateDeg(-10 * mixFactor * globalDegree, Direction.Axis.X);

            skeleton.MogulLeftRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -5, 20) * mixFactor * globalDegree, Direction.Axis.X);
            skeleton.MogulLeftRobe.rotateDeg(Mth.cos(time - 0.3F) * mixFactor * globalDegree * 10 * (sign * 0.5F + 0.5F), Direction.Axis.X);
            skeleton.MogulRightRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, 20, -5) * mixFactor * globalDegree, Direction.Axis.X);
            skeleton.MogulRightRobe.rotateDeg(Mth.sin(time + Mth.HALF_PI + 0.3F) * mixFactor * globalDegree * 10 * (-sign * 0.5F + 0.5F), Direction.Axis.X);

            skeleton.MogulLeftArm.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -20, -10) * mixFactor * globalDegree, Direction.Axis.Z);
            skeleton.MogulRightArm.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, 10, 20) * mixFactor * globalDegree, Direction.Axis.Z);

            skeleton.MogulNeck.rotateDeg(10 * mixFactor * globalDegree * sign, Direction.Axis.Z);
        }
    }

    private static class AttackAnimation extends Animation<GnomadMogulEntity, MogulSkeleton> {
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
        public void apply(GnomadMogulEntity parent, MogulSkeleton skeleton, float mixFactor, float time) {
            float timeDelayed = time - 0.2F;
            float blend = 1 - Mth.clamp((time - (windupLength + swingLength)) / recoveryLength, 0, 1);
            blend = MathUtils.smoothstep(blend) * mixFactor;
            {   // windup anim
                float windupTime = Mth.clamp(time / windupLength, 0, 1);
                float windupTimeDelayed = Mth.clamp(timeDelayed / windupLength, 0, 1);

                float weaponSpinTime = Mth.clamp(time / (windupLength * 1.5F), 0, 1);
                float weaponSpin = MathUtils.ease(weaponSpinTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArmGrasp.rotateDeg(-40 * weaponSpin * blend, Direction.Axis.X);
                skeleton.MogulRightArmGrasp.rotateDeg(180 * weaponSpin * blend, Direction.Axis.Y);
                Vector3f movement = skeleton.MogulRightArmGrasp.rotation.transform(0, 1, 0, new Vector3f());
                float movementAmount = 5 * weaponSpin * blend;
                skeleton.MogulRightArmGrasp.x += movement.x * movementAmount;
                skeleton.MogulRightArmGrasp.y += movement.y * movementAmount;
                skeleton.MogulRightArmGrasp.z += movement.z * movementAmount;

                float armRaise = MathUtils.ease(windupTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArm.rotateDeg(30 * armRaise * blend, Direction.Axis.Y);
                skeleton.MogulRightArm.rotateDeg(30 * armRaise * blend, Direction.Axis.X);
                skeleton.MogulRightArm.z -= 8 * MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutQuad) * blend;

                float bodyRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRoot.rotateDeg(5 * bodyRot * blend, Direction.Axis.Z);
                skeleton.MogulRoot.rotateDeg(-15 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulFrontRobe.rotateDeg(15 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulBackRobe.rotateDeg(15 * bodyRot * blend, Direction.Axis.X);

                float neckRot = MathUtils.ease(windupTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulNeck.rotateDeg(15 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulNeck.rotateDeg(-15 * neckRot * blend, Direction.Axis.X);

                float armRot = MathUtils.ease(windupTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulLeftArm.rotateDeg(15 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulLeftArm.rotateDeg(-15 * armRot * blend, Direction.Axis.X);
            }
            {   // swing anim
                float swingTime = Mth.clamp((time - windupLength) / swingLength, 0, 1);
                float swingTimeDelayed = Mth.clamp((timeDelayed - windupLength) / swingLength, 0, 1);

                float armRaise = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArm.rotateDeg(-40 * armRaise * blend, Direction.Axis.Y);
                skeleton.MogulRightArm.rotateDeg(90 * armRaise * blend, Direction.Axis.X);

                float bodyRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRoot.rotateDeg(-10 * bodyRot * blend, Direction.Axis.Z);
                skeleton.MogulRoot.rotateDeg(30 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulFrontRobe.rotateDeg(-40 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulBackRobe.rotateDeg(-40 * bodyRot * blend, Direction.Axis.X);

                float neckRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeOutBack);
                skeleton.MogulNeck.rotateDeg(-30 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulNeck.rotateDeg(30 * neckRot * blend, Direction.Axis.X);

                float armRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulLeftArm.rotateDeg(15 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulLeftArm.rotateDeg(-15 * armRot * blend, Direction.Axis.X);
            }
        }
    }

    private static class LeftAttackAnimation extends AttackAnimation {
        protected static LeftAttackAnimation INSTANCE = new LeftAttackAnimation();

        private LeftAttackAnimation() {
            super(MogulAttackHandler.SWING_LEFT);
        }

        @Override
        public void apply(GnomadMogulEntity parent, MogulSkeleton skeleton, float mixFactor, float time) {
            float timeDelayed = time - 0.1F;
            float blend = 1 - Mth.clamp((time - (windupLength + swingLength)) / recoveryLength, 0, 1);

            blend = MathUtils.smoothstep(blend) * mixFactor;
            {   // windup anim
                float windupTime = Mth.clamp(time / windupLength, 0, 1);
                float windupTimeDelayed = Mth.clamp(timeDelayed / windupLength, 0, 1);
                float flap = -Mth.cos(2 * Mth.PI * windupTime) * 0.5F + 0.5F;

                float armRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRightArm.rotateDeg(100 * armRot * blend, Direction.Axis.X);
                skeleton.MogulRightArm.rotateDeg(40 * armRot * blend, Direction.Axis.Z);
                skeleton.MogulRightArm.rotateDeg(-40 * armRot * blend, Direction.Axis.Y);

                skeleton.MogulLeftArm.rotateDeg(4 * flap * blend, Direction.Axis.Z);

                float offset = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutQuad);
                skeleton.MogulRightArm.z -= 3 * offset * blend;
                skeleton.MogulRightArm.x -= 2 * offset * blend;
                skeleton.MogulRightArmGrasp.z -= 8 * offset * blend;

                float bodyRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRoot.rotateDeg(5 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulRoot.rotateDeg(-15 * bodyRot * blend, Direction.Axis.Y);

                float headRot = MathUtils.ease(windupTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulNeck.rotateDeg(-5 * headRot * blend, Direction.Axis.X);
                skeleton.MogulNeck.rotateDeg(15 * headRot * blend, Direction.Axis.Y);

                skeleton.MogulLeftRobe.rotateDeg(5 * flap * blend, Direction.Axis.X);
                skeleton.MogulRightRobe.rotateDeg(-5 * flap * blend, Direction.Axis.X);
            }
            {   // swing anim
                float swingTime = Mth.clamp((time - windupLength) / swingLength, 0, 1);
                float swingTimeDelayed = Mth.clamp((timeDelayed - windupLength) / swingLength, 0, 1);
                float flap = -Mth.cos(2 * Mth.PI * swingTime) * 0.5F + 0.5F;

                float armRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRightArm.rotateDeg(-20 * armRot * blend, Direction.Axis.Y);
                skeleton.MogulRightArm.rotateDeg(-120 * armRot * blend, Direction.Axis.X);
                //skeleton.MogulRightArm.rotateDeg(-60 * armRot * blend, Direction.Axis.Z);
                float offset = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArm.x += 2 * offset * blend;
                skeleton.MogulRightArm.z -= 8 * offset * blend;

                skeleton.MogulLeftArm.rotateDeg(-5 * flap * blend, Direction.Axis.Z);
                float otherArmRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeOutBack);
                skeleton.MogulLeftArm.rotateDeg(-10 * otherArmRot * blend, Direction.Axis.Z);

                float bodyRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRoot.rotateDeg(-20 * bodyRot * blend, Direction.Axis.X);
                skeleton.MogulRoot.rotateDeg(30 * bodyRot * blend, Direction.Axis.Y);

                float headRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeOutBack);
                skeleton.MogulNeck.rotateDeg(20 * headRot * blend, Direction.Axis.X);
                skeleton.MogulNeck.rotateDeg(-30 * headRot * blend, Direction.Axis.Y);
                skeleton.MogulHead.rotateDeg(15 * headRot * blend, Direction.Axis.Z);

                skeleton.MogulLeftRobe.rotateDeg(8 * flap * blend, Direction.Axis.X);
                skeleton.MogulRightRobe.rotateDeg(-8 * flap * blend, Direction.Axis.X);

                skeleton.MogulHelmetBase.rotateDeg(5 * flap * blend, Direction.Axis.Z);

            }
        }
    }

    private static class RightAttackAnimation extends AttackAnimation {
        protected static RightAttackAnimation INSTANCE = new RightAttackAnimation();

        private RightAttackAnimation() {
            super(MogulAttackHandler.SWING_RIGHT);
        }

        @Override
        public void apply(GnomadMogulEntity parent, MogulSkeleton skeleton, float mixFactor, float time) {
            float timeDelayed = time - 0.2F;
            float timeDelayedForward = time + 0.1F;

            float blend = 1 - Mth.clamp((time - (windupLength + swingLength)) / recoveryLength, 0, 1);
            blend = MathUtils.smoothstep(blend) * mixFactor;
            {   // windup anim
                float windupTime = Mth.clamp(time / windupLength, 0, 1);
                float windupTimeDelayed = Mth.clamp(timeDelayed / windupLength, 0, 1);
                float windupTimeDelayedForward = Mth.clamp(timeDelayedForward / windupLength, 0, 1);

                float armRaise = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRightArm.rotateDeg(30 * armRaise * blend, Direction.Axis.X);
                skeleton.MogulRightArm.rotateDeg(80 * armRaise * blend, Direction.Axis.Y);
                skeleton.MogulRightArm.z -= 8 * MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutQuad) * blend;

                float armRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRightArm.rotateDeg(30 * armRot * blend, Direction.Axis.X);
                float otherArmRot = MathUtils.ease(windupTimeDelayed, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulLeftArm.rotateDeg(-4 * otherArmRot * blend, Direction.Axis.Z);

                float bodyRot = MathUtils.ease(windupTime, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulRoot.rotateDeg(15 * bodyRot * blend, Direction.Axis.Y);
                float neckRot = MathUtils.ease(windupTimeDelayedForward, MathUtils.EasingType.easeInOutBack);
                skeleton.MogulNeck.rotateDeg(-15 * neckRot * blend, Direction.Axis.Y);
            }
            {   // swing anim
                float swingTime = Mth.clamp((time - windupLength) / swingLength, 0, 1);
                float swingTimeDelayed = Mth.clamp((timeDelayed - windupLength) / swingLength, 0, 1);

                float armRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRightArm.rotateDeg(-90 * armRot * blend, Direction.Axis.X);

                float bodyRot = MathUtils.ease(swingTime, MathUtils.EasingType.easeOutBack);
                skeleton.MogulRoot.rotateDeg(-30 * bodyRot * blend, Direction.Axis.Y);
                skeleton.MogulNeck.rotateDeg(30 * bodyRot * blend, Direction.Axis.Y);

                float otherArmRot = MathUtils.ease(swingTimeDelayed, MathUtils.EasingType.easeOutBack);
                skeleton.MogulLeftArm.rotateDeg(4 * otherArmRot * blend, Direction.Axis.Z);
            }

            float windupTime = Mth.clamp((time - 0.2F) / (windupLength+swingLength), 0, 1);
            Vector3f movement = skeleton.MogulRightArmGrasp.rotation.transform(0, 1, 0, new Vector3f());
            float movementAmount = 10 * windupTime * blend;
            skeleton.MogulRightArmGrasp.x += movement.x * movementAmount;
            skeleton.MogulRightArmGrasp.y += movement.y * movementAmount;
            skeleton.MogulRightArmGrasp.z += movement.z * movementAmount;
        }
    }
}

