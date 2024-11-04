package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import birsy.clinker.common.world.entity.rope.RopeEntity;
import birsy.clinker.common.world.entity.rope.RopeEntitySegment;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class GroundMoveControl extends MoveControl {
    public GroundMoveControl(GroundLocomoteEntity pMob) {
        super(pMob);
    }

    public GroundLocomoteEntity getEntity() {
        return (GroundLocomoteEntity) this.mob;
    }

    private final Vector3d walkVector = new Vector3d();
    private final Vector3d desiredDirection = new Vector3d();
    @Override
    public void tick() {
        GroundLocomoteEntity me = this.getEntity();
        me.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
        float speed = 0.1F;

        float acceleration = 100;
        me.walk(0, 0, 0);
        desiredDirection.set(this.wantedX, this.wantedY, this.wantedZ).sub(me.position().x, me.position().y, me.position().z).normalize();

        if (this.operation == Operation.MOVE_TO) {
            // if we've already reached out destination, then stop
            if (me.getPosition(1.0F).distanceToSqr(this.wantedX, this.wantedY, this.wantedZ) < me.getBbWidth()*2) this.operation = Operation.WAIT;
            walkVector.set(desiredDirection).mul(speed);

            me.walk(
                    (float) MathUtils.approach(me.previousWalk.x, walkVector.x, acceleration),
                    (float) 0,
                    (float) MathUtils.approach(me.previousWalk.z, walkVector.z, acceleration)
            );

            if (me.horizontalCollision) {
                me.setJumping(true);
            }
        }

        if (this.operation == Operation.STRAFE) {
            Vec3 forwardDirection = me.getLookAngle().multiply(1,0,1).normalize();
            Vec3 perpendicularDirection = forwardDirection.cross(new Vec3(0, 1, 0)).normalize();

            me.walk(
                    (float) MathUtils.approach(me.previousWalk.x, me.walk.x() + (perpendicularDirection.x * Mth.abs(this.strafeRight) + forwardDirection.x * this.strafeForwards) * speed, acceleration),
                    (float) 0,
                    (float) MathUtils.approach(me.previousWalk.x, me.walk.z() + (perpendicularDirection.z * Mth.abs(this.strafeRight) + forwardDirection.z * this.strafeForwards) * speed, acceleration)
            );
        }

        if (this.operation == Operation.JUMPING) {
            me.setJumping(true);
            this.operation = Operation.WAIT;
        }

        if (this.operation == Operation.WAIT) {
            acceleration = 0.05F;
            me.walk(
                    MathUtils.approach(me.previousWalk.x, 0, acceleration),
                    0,
                    MathUtils.approach(me.previousWalk.z, 0, acceleration)
            );
        }
    }
}
