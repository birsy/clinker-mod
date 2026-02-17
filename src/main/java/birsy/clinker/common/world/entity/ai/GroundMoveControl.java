package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomotionEntity;
import birsy.clinker.core.Clinker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class GroundMoveControl extends MoveControl {
    private final Vector3f locomotionVector = new Vector3f();

    public GroundMoveControl(GroundLocomotionEntity entity) {
        super(entity);
    }

    public GroundLocomotionEntity mob() {
        return (GroundLocomotionEntity) this.mob;
    }

    @Override
    public void tick() {
        GroundLocomotionEntity me = mob();

        float baseSpeed = (float) (this.getSpeedModifier() * me.getAttributeValue(Attributes.MOVEMENT_SPEED));

        this.locomotionVector.zero();
        me.setLocomotionVector(0, 0, 0);
        switch (operation) {
            case MOVE_TO -> moveTo(me, baseSpeed);
            case STRAFE  -> strafe(me, baseSpeed);
        }

        if (operation != Operation.STRAFE) {
            strafeForwards = 0;
            strafeRight = 0;
        }

        applyDesiredVelocity(me);
    }


    private void moveTo(GroundLocomotionEntity me, float speed) {
        double deltaX = wantedX - me.getX(),
               deltaY = wantedY - me.getY(),
               deltaZ = wantedZ - me.getZ();

        double lateralDistance = Math.max(Math.abs(deltaX), Math.abs(deltaZ)); // boxy distance because entity colliders are AABBs not capsules/cylinders

        if (lateralDistance <= me.getBbWidth() * 0.5F) {
            operation = Operation.WAIT;
            return;
        }

        locomotionVector.set(deltaX, 0, deltaZ);
        locomotionVector.normalize().mul((float) Math.min(speed, Mth.length(deltaX, 0, deltaZ)));
        if (shouldJump(me, deltaY, lateralDistance)) me.getJumpControl().jump();
    }

    private boolean shouldJump(GroundLocomotionEntity me, double deltaY, double lateralDistance) {
        if (deltaY <= me.maxUpStep()) return false; // we can already step up to the target point
        if (!me.horizontalCollision) return false;
        return lateralDistance <= (me.getBbWidth() * 0.5 + 0.6); // we're close enough to the target point for a jump to make it
    }

    private void strafe(GroundLocomotionEntity me, float speed) {
        Vector3fc forward = me.getBodyFacingDirection(1.0F);
        locomotionVector.set(
                (-forward.z() * strafeRight + forward.x() * strafeForwards) * speed,
                0,
                (forward.x() * strafeRight + forward.z() * strafeForwards) * speed
        );
        if (me.horizontalCollision) me.getJumpControl().jump();
    }

    private void applyDesiredVelocity(GroundLocomotionEntity me) {
        float acceleration = operation == Operation.WAIT ? 0.008F : 0.1F;
        me.setLocomotionVector(
                Mth.approach(me.previousLocomotionVector.x, locomotionVector.x, acceleration),
                0,
                Mth.approach(me.previousLocomotionVector.z, locomotionVector.z, acceleration)
        );
    }
}
