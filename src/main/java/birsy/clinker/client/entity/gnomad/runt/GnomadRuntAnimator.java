package birsy.clinker.client.entity.gnomad.runt;

import birsy.clinker.client.entity.gnomad.basic.GnomadAnimator;
import birsy.clinker.common.world.entity.gnomad.testing.SquadTestingSupplierEntity;
import foundry.veil.api.client.necromancer.animation.Animation;
import foundry.veil.api.client.necromancer.animation.Animator;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector3fc;

import static birsy.clinker.client.AnimationUtilities.nSin;
import static net.minecraft.core.Direction.Axis.*;

public class GnomadRuntAnimator extends Animator<SquadTestingSupplierEntity, GnomadRuntSkeleton> {
    public final AnimationEntry<?, ?> idleAnim, walkAnim, strafeAnim, hurtAnim;
    protected GnomadRuntAnimator(SquadTestingSupplierEntity parent, GnomadRuntSkeleton skeleton) {
        super(parent, skeleton);
        this.idleAnim = this.addAnimation(IdleAnimation.INSTANCE, 0);
        this.walkAnim = this.addAnimation(WalkAnimation.INSTANCE, 1);
        this.strafeAnim = this.addAnimation(StrafeAnimation.INSTANCE, 2);
        this.hurtAnim = this.addAnimation(HurtAnimation.INSTANCE, 3);
    }

    @Override
    public void animate() {
        super.animate();
        skeleton.root.size.mul(0.8F);

        SquadTestingSupplierEntity entity = this.parent;

        this.idleAnim.setMixFactor(1.0F);
        this.idleAnim.setTime(entity.tickCount);

        float moveTime = entity.getCumulativeLocomotionAmount() * 2.5F;

        float walkFac = Mth.clamp(12 * entity.getForwardLocomotionAmount(1.0F), -2.0F, 2.0F);
        this.walkAnim.setMixFactor(walkFac);
        this.walkAnim.setTime(moveTime);

        float strafeFac = Mth.clamp(12 * entity.getStrafeLocomotionAmount(1.0F), -1.0F, 1.0F);
        this.strafeAnim.setMixFactor(strafeFac);
        this.strafeAnim.setTime(moveTime);

        if (entity.isHoldingDelivery()) {
            skeleton.rightArm.rotateDeg(80, Direction.Axis.Y);
            skeleton.leftArm.rotateDeg(-80, Direction.Axis.Y);
            skeleton.rightArm.rotateDeg(70, Direction.Axis.Z);
            skeleton.leftArm.rotateDeg(-70, Direction.Axis.Z);
        }

        if (entity.hurtDuration > 0) {
            float hurtMixFactor = (float) entity.hurtTime / entity.hurtDuration;
            this.hurtAnim.setTime(entity.tickCount * 0.28F);
            this.hurtAnim.setMixFactor(hurtMixFactor);

            Vector3fc hurtDirection = entity.getLastHitDirection();
            if (hurtDirection.x() != 0 || hurtDirection.z() != 0) {
                float hurtRotationAxisX = -hurtDirection.z(),
                        hurtRotationAxisZ = hurtDirection.x();
                skeleton.root.rotation.rotateAxis(-60 * Mth.DEG_TO_RAD * hurtMixFactor, hurtRotationAxisX, 0, hurtRotationAxisZ);
            }
        } else {
            this.hurtAnim.setMixFactor(0.0F);
        }
    }

    private static class HurtAnimation extends Animation<SquadTestingSupplierEntity, GnomadRuntSkeleton> {
        protected static HurtAnimation INSTANCE = new HurtAnimation();

        public void apply(SquadTestingSupplierEntity entity, GnomadRuntSkeleton skeleton, float mixFactor, float time) {
            skeleton.root.rotateDeg(nSin(0.0F + time) * 12 * mixFactor, Direction.Axis.X);
            skeleton.root.rotateDeg(nSin(0.5F + time) * 12 * mixFactor, Direction.Axis.Z);
            skeleton.torso.rotateDeg(nSin(0.0F + time - 0.3F) * 12 * mixFactor, Direction.Axis.X);
            skeleton.torso.rotateDeg(nSin(0.5F + time - 0.3F) * 12 * mixFactor, Direction.Axis.Z);

            skeleton.head.rotateDeg(nSin(0.0F + time - 1) * 8 * mixFactor, Direction.Axis.X);
            skeleton.head.rotateDeg(nSin(0.5F + time - 1) * 8 * mixFactor, Direction.Axis.Z);

            skeleton.rightArm.rotateDeg(nSin(0.0F + time - 1) * 15 * mixFactor, Direction.Axis.X);
            skeleton.rightArm.rotateDeg(nSin(0.5F + time - 1) * 15 * mixFactor, Direction.Axis.Z);
            skeleton.leftArm.rotateDeg(nSin(0.0F + time - 1) * 15 * mixFactor, Direction.Axis.X);
            skeleton.leftArm.rotateDeg(nSin(0.5F + time - 1) * -15 * mixFactor, Direction.Axis.Z);

            skeleton.leftLeg.rotateDeg(nSin(0.0F + time + 1) * 15 * mixFactor, Direction.Axis.X);
            skeleton.leftLeg.rotateDeg(nSin(0.5F + time + 1) * -15 * mixFactor, Direction.Axis.Z);
            skeleton.rightLeg.rotateDeg(nSin(0.0F + time + 1) * 15 * mixFactor, Direction.Axis.X);
            skeleton.rightLeg.rotateDeg(nSin(0.5F + time + 1) * -15 * mixFactor, Direction.Axis.Z);
        }
    }

    private static class IdleAnimation extends Animation<SquadTestingSupplierEntity, GnomadRuntSkeleton> {
        protected static IdleAnimation INSTANCE = new IdleAnimation();

        public void apply(SquadTestingSupplierEntity entity, GnomadRuntSkeleton skeleton, float mixFactor, float time) {
            float speed = 1.0F / 40.0F;
            float degree = 1.0F;

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
            skeleton.rightArm.rotateDeg(10, Direction.Axis.Z);
            skeleton.leftArm.rotateDeg(-10, Direction.Axis.Z);

            skeleton.head.rotateDeg(-torsoRotation, Direction.Axis.X);

            skeleton.rightArm.rotateDeg(nSin(time * speed * 1.0F) * 1 * mixFactor * degree, Z);
            skeleton.rightArm.rotateDeg(nSin(time * speed * 0.9F) * 1 * mixFactor * degree, X);

            skeleton.leftArm.rotateDeg(nSin(time * speed * 0.85F) * 1 * mixFactor * degree, Z);
            skeleton.leftArm.rotateDeg(nSin(time * speed * 0.95F) * 1 * mixFactor * degree, X);

            skeleton.torso.offsetY(nSin(time * speed * 0.7F) * 0.1F * mixFactor * degree);
            skeleton.head.offsetY(nSin(time * speed * 0.7F + 0.2F) * 0.1F * mixFactor * degree);

        }
    }

    private static class WalkAnimation extends Animation<SquadTestingSupplierEntity, GnomadRuntSkeleton> {
        protected static WalkAnimation INSTANCE = new WalkAnimation();

        @Override
        public boolean running(SquadTestingSupplierEntity entity, GnomadRuntSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(SquadTestingSupplierEntity entity, GnomadRuntSkeleton skeleton, float mixFactor, float time) {
            float degree = 1.0F;
            float sign = Mth.sign(mixFactor);
            mixFactor = Mth.abs(mixFactor);

            // bounce mix factor decreases with speed...
            float bounceMixFactor = mixFactor;
            if (mixFactor > 1.0F) bounceMixFactor = Mth.clampedMap(bounceMixFactor, 1.0F, 2.5F, mixFactor, 0);
            // squared mix factor really emphasizes movements when running
            float squaredMixFactor = mixFactor * mixFactor;

            skeleton.root.offsetY(Math.abs(nSin(time + 0.5F)) * 0.25F * mixFactor * degree);
            skeleton.root.rotateDeg(nSin(time) * 1 * squaredMixFactor * sign * degree, Z);
            skeleton.torso.rotateDeg(nSin(time - 0.4F) * 1 * squaredMixFactor * sign * degree, Z);
            skeleton.head.rotateDeg(nSin(time - 0.8F) * 1 * squaredMixFactor * sign * degree, Z);

            skeleton.rightArm.rotateDeg(nSin(time - 0.8F) * 3 * squaredMixFactor * sign * degree, Z);
            skeleton.leftArm.rotateDeg(nSin(time - 0.8F) * 3 * squaredMixFactor * sign * degree, Z);


            float legMixFactor = (mixFactor + squaredMixFactor) * 0.5F;
            skeleton.rightLeg.offsetZ(nSin(time) * -0.25F * squaredMixFactor * sign * degree);
            skeleton.rightLeg.offsetY(nSin(time + 0.5F) * 0.25F * mixFactor * degree);
            skeleton.rightLeg.rotateDeg(nSin(time) * 35 * legMixFactor * sign * degree, X);

            skeleton.leftLeg.offsetZ(-nSin(time) * -0.25F * squaredMixFactor * sign * degree);
            skeleton.leftLeg.offsetY(-nSin(time + 0.5F) * 0.25F * mixFactor * degree);
            skeleton.leftLeg.rotateDeg(-nSin(time) * 35 * legMixFactor * sign * degree, X);

            skeleton.rightArm.rotateDeg(-nSin(time) * 35 * bounceMixFactor * sign * degree, X);
            skeleton.leftArm.rotateDeg(nSin(time) * 35 * bounceMixFactor * sign * degree, X);

            skeleton.rightArm.rotateDeg(10 * squaredMixFactor * degree, Z);
            skeleton.leftArm.rotateDeg(-10 * squaredMixFactor * degree, Z);
        }
    }

    private static class StrafeAnimation extends Animation<SquadTestingSupplierEntity, GnomadRuntSkeleton> {
        protected static StrafeAnimation INSTANCE = new StrafeAnimation();

        @Override
        public boolean running(SquadTestingSupplierEntity entity, GnomadRuntSkeleton skeleton, float mixFactor, float time) {
            return mixFactor != 0;
        }

        @Override
        public void apply(SquadTestingSupplierEntity entity, GnomadRuntSkeleton skeleton, float mixFactor, float time) {
            float degree = 1.0F;
            float sign = Mth.sign(mixFactor);
            mixFactor = Mth.abs(mixFactor);

            // bounce mix factor decreases with speed...
            float bounceMixFactor = mixFactor;
            if (mixFactor > 1.0F) bounceMixFactor = Mth.clampedMap(bounceMixFactor, 1.0F, 2.5F, mixFactor, 0);
            // squared mix factor really emphasizes movements when running
            float squaredMixFactor = mixFactor * mixFactor;

            skeleton.root.rotateDeg(-4 * mixFactor * sign * degree, Z);
            skeleton.head.rotateDeg(-4 * mixFactor * sign * degree, Z);

            skeleton.root.offsetY(Math.abs(nSin(time + 0.5F)) * 0.5F * bounceMixFactor * degree);
            skeleton.root.rotateDeg(nSin(time) * 3 * bounceMixFactor * sign * degree, Z);
            skeleton.torso.rotateDeg(nSin(time - 0.4F) * 3 * bounceMixFactor * sign * degree, Z);
            skeleton.head.rotateDeg(nSin(time - 0.8F) * 3 * bounceMixFactor * sign * degree, Z);

            skeleton.rightLeg.offsetY(nSin(time + 0.5F) * 0.5F * mixFactor * degree);
            skeleton.rightLeg.rotateDeg(nSin(time) * 30 * mixFactor * sign * degree, Z);

            skeleton.leftLeg.offsetY(-nSin(time + 0.5F) * 0.5F * mixFactor * degree);
            skeleton.leftLeg.rotateDeg(-nSin(time) * 30 * mixFactor * sign * degree, Z);

            skeleton.rightArm.rotateDeg(nSin(time) * 10 * mixFactor * sign * degree, X);
            skeleton.leftArm.rotateDeg(-nSin(time) * 10 * mixFactor * sign * degree, X);

            skeleton.rightArm.rotateDeg(50 * squaredMixFactor * degree, Z);
            skeleton.leftArm.rotateDeg(-50 * squaredMixFactor * degree, Z);
        }
    }
}
