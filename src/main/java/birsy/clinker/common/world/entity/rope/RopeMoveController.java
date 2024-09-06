package birsy.clinker.common.world.entity.rope;

import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.VectorUtils;
import com.mojang.math.Axis;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RopeMoveController extends MoveControl {
    public RopeMoveController(RopeEntity<?> ropeEntity) {
        super(ropeEntity);
    }

    public RopeEntity<? extends RopeEntity.RopeEntitySegment> getEntity() {
        return (RopeEntity<? extends RopeEntity.RopeEntitySegment>) this.mob;
    }

    @Override
    public void tick() {
        RopeEntity<? extends RopeEntity.RopeEntitySegment> me = this.getEntity();
        me.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
        float speed = me.getSpeed();
        if (this.operation == Operation.MOVE_TO || this.operation == Operation.STRAFE) {
            // face more towards the direction we want to go...
            // todo: only move a certain amount every tick
            me.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(this.getWantedX(), this.getWantedX(), this.getWantedZ()));
        }

        Vector3d walkVector = new Vector3d();
        float acceleration = 1.0F;
        switch (this.operation) {
            case MOVE_TO:
                acceleration = 0.1F;

                // move segments forward
                // the "head segment" moves in the facing direction
                // the rest follow behind.
                for (RopeEntity.RopeEntitySegment segment : me.segments) {
                    Vector3dc directionToWalk = segment.connectionDirection(1.0F);
                    walkVector.set(directionToWalk.x() * speed, directionToWalk.y() * speed, directionToWalk.z() * speed);
                    segment.walk(
                            MathUtils.approach(segment.previousWalk.x, walkVector.x, acceleration),
                            0,
                            MathUtils.approach(segment.previousWalk.z, walkVector.z, acceleration)
                    );
                }
                break;
            case STRAFE:
                // strafing moves every element in the same direction. this ensures the entire body circles you at once...
                // todo: sidewinder type motion?
                acceleration = 0.05F;

                // forwards strafe term
                Vec3 movementDir = me.getLookAngle().scale(this.strafeForwards);
                // left/right strafe term
                movementDir = movementDir.add(me.getLookAngle().cross(new Vec3(0, 1, 0)).scale(this.strafeRight));

                for (RopeEntity.RopeEntitySegment segment : me.segments) {
                    segment.walk(
                            MathUtils.approach(segment.previousWalk.x, movementDir.x, acceleration),
                            0,
                            MathUtils.approach(segment.previousWalk.z, movementDir.z, acceleration)
                    );
                }
                break;
            case JUMPING:
                // todo: lunge towards target position!
                this.operation = MoveControl.Operation.WAIT;
                break;
            case WAIT:
                // todo: scoot our ass towards the facing direction...
                acceleration = 0.05F;
                for (RopeEntity.RopeEntitySegment segment : me.segments) {
                    segment.walk(
                            MathUtils.approach(segment.previousWalk.x, 0, acceleration),
                            0,
                            MathUtils.approach(segment.previousWalk.z, 0, acceleration)
                    );
                }
                break;
        }
    }
}
