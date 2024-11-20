package birsy.clinker.client.entity.mogul;

import birsy.clinker.client.necromancer.animation.Animation;
import birsy.clinker.client.necromancer.animation.Animator;
import birsy.clinker.common.world.entity.gnomad.GnomadMogulEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.tslat.smartbrainlib.util.RandomUtil;

public class MogulAnimator extends Animator<GnomadMogulEntity, MogulSkeleton> {
    private static MogulAnimator.IdleAnimation IDLE = new IdleAnimation();
    private static MogulAnimator.MaskAnimation MASK = new MaskAnimation();
    private static MogulAnimator.WalkAnimation WALK = new WalkAnimation();
    private static MogulAnimator.StrafeAnimation STRAFE = new StrafeAnimation();

    private final AnimationEntry<?, ?> idle, mask, walk, strafe;
    private int maskShakeTime = 0, maskShakeDuration = 1;
    private boolean maskShaking = false;

    protected MogulAnimator(GnomadMogulEntity parent, MogulSkeleton skeleton) {
        super(parent, skeleton);
        this.strafe = this.addAnimation(STRAFE, 2);
        this.walk = this.addAnimation(WALK, 1);
        this.mask = this.addAnimation(MASK, 0);
        this.idle = this.addAnimation(IDLE, 0);
    }

    @Override
    public void animate(GnomadMogulEntity entity) {
        super.animate(entity);
        // idle
        this.idle.setTime(entity.tickCount);
        this.idle.setMixFactor(1.0F);

        // randomly start shaking mask
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
        this.mask.setMixFactor(shakeAmount * shakeAmount * shakeAmount * shakeAmount * 0.5F);
        this.mask.setTime(entity.tickCount);

        // locomotion
        float moveTime = entity.getCumulativeWalk() * 2.3F;

        float walkFac = Mth.clamp(5 * entity.getWalkAmount(1.0F), -0.8F, 0.8F);
        this.walk.setMixFactor(walkFac);
        this.walk.setTime(moveTime);

        float strafeFac = Mth.clamp(5 * -entity.getStrafeAmount(1.0F), -0.8F, 0.8F);
        this.strafe.setMixFactor(strafeFac);
        this.strafe.setTime(strafeFac);
    }

    private static class MaskAnimation extends Animation<GnomadMogulEntity, MogulSkeleton> {
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
            skeleton.MogulRoot.z += Mth.sin(time * 2 - 1F) * mixFactor * globalDegree * 0.5F * sign;
            skeleton.MogulRoot.rotateDeg(-10 * mixFactor * globalDegree * sign, Direction.Axis.X);

            skeleton.MogulFrontRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -30, -10) * mixFactor * globalDegree * sign, Direction.Axis.X);
            skeleton.MogulBackRobe.rotateDeg(Mth.lerp((sign + 1.0F) * 0.5F, -10, -30) * mixFactor * globalDegree * sign, Direction.Axis.X);

            skeleton.MogulNeck.rotateDeg(10 * mixFactor * globalDegree * sign, Direction.Axis.X);
        }
    }

    private static class StrafeAnimation extends Animation<GnomadMogulEntity, MogulSkeleton> {
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
}
