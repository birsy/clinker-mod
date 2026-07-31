package birsy.clinker.client.entity.slabcrab;

import birsy.clinker.common.entity.SlabCrabEntity;
import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.animation.Animation;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class SlabCrabAnimator extends Animator<SlabCrabEntity, SlabCrabSkeleton> {
    public final AnimationEntry<?, ?> awakeIdleAnim, walkBobAnim, walkLegsAnim, strafeLegsAnim;

    protected SlabCrabAnimator(SlabCrabEntity parent, SlabCrabSkeleton skeleton) {
        super(parent, skeleton);
        this.awakeIdleAnim = this.addAnimation(IdleAnimation.INSTANCE, 0);
        this.walkBobAnim = this.addAnimation(WalkBobAnimation.INSTANCE, 1);
        this.walkLegsAnim = this.addAnimation(WalkLegsAnimation.INSTANCE, 1);
        this.strafeLegsAnim = this.addAnimation(StrafeLegsAnimation.INSTANCE, 2);
    }

    @Override
    public void animate() {
        super.animate();
        SlabCrabEntity entity = this.parent;

        float bodyYaw = Mth.wrapDegrees(180 - entity.yBodyRot);

        float headYaw = Mth.wrapDegrees(180 - entity.yHeadRot);
        float netHeadYaw = Mth.degreesDifference(bodyYaw, headYaw);
        netHeadYaw = Mth.clamp(netHeadYaw, -80, 80);
        float headPitch = -entity.getViewXRot(1.0F);

        float time = entity.tickCount;
        float speed = 0.5F;
        float degree = 1.0F;
        skeleton.body.rotateDeg(bodyYaw, Direction.Axis.Y);
        skeleton.rightMandible.rotateDeg(-5, Direction.Axis.Y);
        skeleton.leftMandible.rotateDeg(5, Direction.Axis.Y);
        //skeleton.rightEye.offsetY(-3);
        //skeleton.leftEye.offsetY(-3);

        this.awakeIdleAnim.setMixFactor(1F);
        this.awakeIdleAnim.setTime(time);
        float walkMult = 10;
        float walkFac = entity.getForwardLocomotionAmount(1.0F) * walkMult * 2,
              strafeFac = entity.getStrafeLocomotionAmount(1.0F) * walkMult;
        float walkTime = entity.getCumulativeLocomotionAmount() * 30;
        this.walkBobAnim.setMixFactor((float) Mth.length(walkFac, strafeFac));

        if (Math.abs(walkFac) > Math.abs(strafeFac)) {
            walkFac = Math.max(Math.abs(walkFac), 0.5F) * Mth.sign(walkFac);
        } else {
            strafeFac = Math.max(Math.abs(strafeFac), 0.5F) * Mth.sign(strafeFac);
        }

        this.walkBobAnim.setTime(walkTime);
        this.walkLegsAnim.setMixFactor(walkFac);
        this.walkLegsAnim.setTime(walkTime);
        this.strafeLegsAnim.setMixFactor(strafeFac);
        this.strafeLegsAnim.setTime(walkTime);

        skeleton.leftEye.rotateDeg(netHeadYaw, Direction.Axis.Y);
        skeleton.rightEye.rotateDeg(netHeadYaw, Direction.Axis.Y);
        skeleton.leftEye.rotateDeg(headPitch, Direction.Axis.X);
        skeleton.rightEye.rotateDeg(headPitch, Direction.Axis.X);
    }

    private static class IdleAnimation extends Animation<SlabCrabEntity, SlabCrabSkeleton> {
        protected static IdleAnimation INSTANCE = new IdleAnimation();

        @Override
        public void apply(SlabCrabEntity entity, SlabCrabSkeleton skeleton, float mixFactor, float time) {
            float speed = 0.5F;
            skeleton.body.offsetY(1 * mixFactor);

            skeleton.rightEye.rotateDeg(Mth.sin(time * speed * 0.12F) * -5 * mixFactor, Direction.Axis.Z);
            skeleton.rightEye.rotateDeg(Mth.cos(time * speed * 0.12F) * -5 * mixFactor, Direction.Axis.X);
            skeleton.leftEye.rotateDeg(Mth.sin(time * speed * 0.13F) * 5 * mixFactor, Direction.Axis.Z);
            skeleton.leftEye.rotateDeg(Mth.cos(time * speed * 0.13F) * 5 * mixFactor, Direction.Axis.X);

            skeleton.rightMandible.rotateDeg(-20 * mixFactor, Direction.Axis.Y);
            skeleton.rightMandible.rotateDeg(Mth.sin(time * speed * 0.125F) * -5 * mixFactor, Direction.Axis.Z);
            skeleton.leftMandible.rotateDeg(20 * mixFactor, Direction.Axis.Y);
            skeleton.leftMandible.rotateDeg(Mth.sin(time * speed * 0.128F) * 5 * mixFactor, Direction.Axis.Z);

            skeleton.rightClaw.rotateDeg(-25 * mixFactor, Direction.Axis.Y);
            skeleton.rightClaw.rotateDeg(10 * mixFactor, Direction.Axis.Z);
            skeleton.leftClaw.rotateDeg(25 * mixFactor, Direction.Axis.Y);
            skeleton.leftClaw.rotateDeg(-10 * mixFactor, Direction.Axis.Z);

            skeleton.rightClaw.rotateDeg(Mth.sin(time * speed * 0.12F) * -2 * mixFactor, Direction.Axis.Z);
            skeleton.rightClaw.rotateDeg(Mth.cos(time * speed * 0.12F) * -2 * mixFactor, Direction.Axis.X);
            skeleton.leftClaw.rotateDeg(Mth.sin(time * speed * 0.13F) * 2 * mixFactor, Direction.Axis.Z);
            skeleton.leftClaw.rotateDeg(Mth.cos(time * speed * 0.13F) * 2 * mixFactor, Direction.Axis.X);

            for (int i = 0; i < skeleton.rightLegs.length; i++) {
                Bone leg = skeleton.rightLegs[i];
                leg.rotateDeg(105 * mixFactor, Direction.Axis.Z);
            }
            for (int i = 0; i < skeleton.leftLegs.length; i++) {
                Bone leg = skeleton.leftLegs[i];
                leg.rotateDeg(-105 * mixFactor, Direction.Axis.Z);
            }
        }
    }

    private static class WalkBobAnimation extends Animation<SlabCrabEntity, SlabCrabSkeleton> {
        protected static WalkBobAnimation INSTANCE = new WalkBobAnimation();

        @Override
        public boolean running(SlabCrabEntity entity, SlabCrabSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(SlabCrabEntity entity, SlabCrabSkeleton skeleton, float mixFactor, float time) {
            float speed = 0.5F;
            float walkFactor = Math.abs(mixFactor);

            skeleton.body.offsetY(Math.abs(Mth.sin(time * speed * 2F)) * 0.25F * mixFactor);
            skeleton.body.rotateDeg(Mth.sin(time * speed * 2F) * walkFactor, Direction.Axis.Z);

            skeleton.rightEye.offsetY(Math.abs(Mth.sin(time * speed - 0.5F)) * 0.1F * walkFactor);
            skeleton.leftEye.offsetY(Math.abs(Mth.sin(time * speed - 0.5F)) * 0.1F * walkFactor);

            skeleton.rightMandible.rotateDeg(Mth.cos(time * speed * 2) * -2 * walkFactor, Direction.Axis.Z);
            skeleton.leftMandible.rotateDeg(Mth.sin(time * speed * 2) * 2 * walkFactor, Direction.Axis.Z);

            skeleton.rightClaw.rotateDeg(Mth.sin(time * speed * 2) * 2 * walkFactor, Direction.Axis.Z);
            skeleton.leftClaw.rotateDeg(Mth.cos(time * speed * 2) * -2 * walkFactor, Direction.Axis.Z);
        }
    }

    private static class StrafeLegsAnimation extends Animation<SlabCrabEntity, SlabCrabSkeleton> {
        protected static StrafeLegsAnimation INSTANCE = new StrafeLegsAnimation();

        @Override
        public boolean running(SlabCrabEntity entity, SlabCrabSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(SlabCrabEntity entity, SlabCrabSkeleton skeleton, float mixFactor, float time) {
            float speed = 0.5F;
            float walkFactor = Math.abs(mixFactor);
            float walkDirection = mixFactor > 0 ? 1 : 0;
            for (int i = 0; i < skeleton.rightLegs.length; i++) {
                float legOffset = ((i % 2 == 0) ? 0 : Mth.PI) + i * 0.5F;
                Bone leg = skeleton.rightLegs[i];
                leg.offsetX(Mth.sin(time * speed + legOffset) * 1F * walkFactor);
                leg.rotateDeg(Mth.cos(time * speed + 0.25F + legOffset + Mth.PI * (1 - walkDirection)) * 30 * walkFactor, Direction.Axis.Z);
            }
            for (int i = 0; i < skeleton.leftLegs.length; i++) {
                float legOffset = ((i % 2 == 0) ? 0 : Mth.PI) + i * 0.5F;
                Bone leg = skeleton.leftLegs[i];
                leg.offsetX(Mth.sin(time * speed + legOffset) * 1F * walkFactor);
                leg.rotateDeg(Mth.cos(time * speed + 0.25F + legOffset + Mth.PI * walkDirection) * 30 * walkFactor, Direction.Axis.Z);
            }
        }
    }

    private static class WalkLegsAnimation extends Animation<SlabCrabEntity, SlabCrabSkeleton> {
        protected static WalkLegsAnimation INSTANCE = new WalkLegsAnimation();

        @Override
        public boolean running(SlabCrabEntity entity, SlabCrabSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(SlabCrabEntity entity, SlabCrabSkeleton skeleton, float mixFactor, float time) {
            float speed = 0.5F;
            float walkFactor = Math.abs(mixFactor);
            float walkDirection = mixFactor > 0 ? 1 : 0;
            for (int i = 0; i < skeleton.rightLegs.length; i++) {
                float legOffset = ((i % 2 == 0) ? 0 : Mth.PI) + i * 0.5F;
                Bone leg = skeleton.rightLegs[i];
                leg.rotateDeg(Math.clamp(Mth.cos(time * speed + 0.25F + legOffset), 0, 1) * 30 * walkFactor, Direction.Axis.Z);
                leg.rotateDeg(Mth.sin(time * speed + legOffset + (1 - walkDirection) * Mth.PI) * 1F * walkFactor * 15, Direction.Axis.X);
            }
            for (int i = 0; i < skeleton.leftLegs.length; i++) {
                float legOffset = ((i % 2 == 0) ? 0 : Mth.PI) + i * 0.5F;
                Bone leg = skeleton.leftLegs[i];
                leg.rotateDeg(Math.clamp(Mth.sin(time * speed + 0.25F + legOffset), -1, 0) * 30 * walkFactor, Direction.Axis.Z);
                leg.rotateDeg(Mth.cos(time * speed + legOffset + (1 - walkDirection) * Mth.PI) * 1F * walkFactor * 15, Direction.Axis.X);
            }
        }
    }
}
