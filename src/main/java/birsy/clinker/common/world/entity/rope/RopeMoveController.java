package birsy.clinker.common.world.entity.rope;

import birsy.clinker.core.util.MathUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RopeMoveController extends MoveControl {
    public RopeMoveController(RopeEntity<?> ropeEntity) {
        super(ropeEntity);
    }

    public RopeEntity<? extends RopeEntitySegment> getEntity() {
        return (RopeEntity<? extends RopeEntitySegment>) this.mob;
    }

    @Override
    public void tick() {
        RopeEntity<? extends RopeEntitySegment> me = this.getEntity();
        me.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
        float speed = me.getSpeed();
        float climbSpeed = 5;

        Vector3d walkVector = new Vector3d();
        float acceleration = 0.1F;

        for (RopeEntitySegment segment : me.segments) segment.walk(0,0,0);
        Vector3d desiredDirection = new Vector3d(this.wantedX, this.wantedY, this.wantedZ).sub(me.position().x, me.position().y, me.position().z).normalize();

        if (this.operation == Operation.MOVE_TO) {
            Vector3d slitherVector = new Vector3d();

            // move segments forward
            for (int i = 0; i < me.segments.size(); i++) {
                RopeEntitySegment segment = me.segments.get(i);
                boolean slowingDown = segment.previousWalk.lengthSquared() > walkVector.lengthSquared();
                boolean isBody = i > 0;

                Vector3dc directionToWalk = segment.getAttachmentDirection(1.0F);
                if (!isBody) directionToWalk = desiredDirection;
                walkVector.set(directionToWalk).mul(speed);

                // slither our body somewhat?
                float slitherAmount = (float)i / me.segments.size();
                float wiggleFactor = slitherAmount*16 - me.tickCount*0.1F;
                desiredDirection.cross(0, 1, 0, slitherVector).mul(Mth.sin(wiggleFactor)).mul(slitherAmount*slitherAmount).mul(2);

                walkVector.add(slitherVector);

                // the "head segment" moves in the facing direction
                if (!isBody || slowingDown) {
                    float usedAccel = acceleration;
                    if (slowingDown && !isBody) usedAccel = acceleration * 0.5F;
                    segment.walk(
                            MathUtils.approach(segment.previousWalk.x, walkVector.x, usedAccel),
                            0,
                            MathUtils.approach(segment.previousWalk.z, walkVector.z, usedAccel)
                    );
                } else {
                    // the rest follow behind.
                    segment.walk(walkVector.x, walkVector.y, walkVector.z);
                }


            }
        }

        if (this.operation == Operation.STRAFE) {
            Vec3 forwardDirection = me.getLookAngle().multiply(1,0,1).normalize();
            Vec3 perpendicularDirection = forwardDirection.cross(new Vec3(0, 1, 0)).normalize().scale(Mth.sign(this.strafeRight));

            float strafeSpeed = 1F;
            float schmooveFactor = 0.1F;
            Vector3d desiredPosition = new Vector3d(me.segments.get(0).getPosition());

            for (int i = 1; i < me.segments.size(); i++) {
                RopeEntitySegment segment = me.segments.get(i);

                float factor = i / (float)me.segments.size();
                float angle = -90F;

                float ppFactor = ((factor - schmooveFactor) / (1-schmooveFactor)) - me.tickCount*0.01F;
                float pingPong = Mth.abs(Mth.frac((ppFactor*3 - 1) / (2.0F)) * 2.0F - 1);
                pingPong = MathUtils.mapRange(0, 1, -1, 1, pingPong);
                pingPong *= 2;
                pingPong = Mth.clamp(pingPong, -1, 1);
                pingPong = MathUtils.mapRange(-1, 1, 0, 1, pingPong);
                pingPong = MathUtils.ease(pingPong, MathUtils.EasingType.easeInOutSine);
                pingPong = MathUtils.mapRange(0, 1, -1, 1, pingPong);
                angle = Mth.lerp(Mth.clamp(factor*1.4F, 0, 1), angle, pingPong * 90F);

                angle = (float) Math.toRadians(angle);

                desiredPosition.set(me.segments.get(i - 1).getPosition()).add(me.segments.get(i - 1).getWalkVector())
                        .add(perpendicularDirection.x()*Mth.cos(angle), 0, perpendicularDirection.z()*Mth.cos(angle))
                        .add(forwardDirection.x()*Mth.sin(angle), 0, forwardDirection.z()*Mth.sin(angle));

                // set walk vector to the direction towards the desired position
                desiredPosition.sub(segment.position, walkVector);
                if (walkVector.length() < 0.2) continue;
                if (walkVector.length() > 0) walkVector.normalize();
                walkVector.mul(strafeSpeed);
                segment.walk(
                        walkVector.x,
                        0,
                        walkVector.z
                );

            }

            for (RopeEntitySegment segment : me.segments) {
                segment.walk(
                        segment.walk.x() + (perpendicularDirection.x * Mth.abs(this.strafeRight) + forwardDirection.x * this.strafeForwards) * speed,
                        segment.walk.y(),
                        segment.walk.z() + (perpendicularDirection.z * Mth.abs(this.strafeRight) + forwardDirection.z * this.strafeForwards) * speed
                );
            }
        }

        if (this.operation == Operation.JUMPING) {
            // todo: lunge towards target position!
            this.operation = Operation.WAIT;
        }

        if (this.operation == Operation.WAIT) {
            // todo: scoot our ass towards the facing direction...
            acceleration = 0.05F;
            for (RopeEntitySegment segment : me.segments) {
                segment.walk(
                        MathUtils.approach(segment.previousWalk.x, 0, acceleration),
                        0,
                        MathUtils.approach(segment.previousWalk.z, 0, acceleration)
                );
            }
        }

        if (this.operation == Operation.MOVE_TO || this.operation == Operation.STRAFE) {
            for (RopeEntitySegment segment : me.segments) {
                // climb up any geometry we walk into
                if (segment.isWalkingIntoWall()) {
                    segment.walk(
                            segment.walk.x,
                            segment.walk.y + climbSpeed,
                            segment.walk.z
                    );
                }
            }
        }
    }
}
