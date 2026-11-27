package birsy.clinker.common.entity.ai;

import birsy.clinker.common.entity.GroundLocomotionEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

public class GroundMoveControl extends MoveControl {
    private final Vector3f locomotionVector = new Vector3f();
    private final GroundLocomotionEntity<?> entity;

    public GroundMoveControl(GroundLocomotionEntity<?> entity) {
        super(entity);
        this.entity = entity;
    }

    public boolean isStrafing() {
        return this.operation == Operation.STRAFE;
    }

    @Override
    public void tick() {
        float baseSpeed = (float) (this.getSpeedModifier() *
                entity.getAttributeValue(Attributes.MOVEMENT_SPEED) *
                entity.speedModifier);

        this.locomotionVector.zero();
        entity.setLocomotionVector(0, 0, 0);
        switch (operation) {
            case MOVE_TO -> moveTo(baseSpeed);
            case STRAFE  -> strafe(baseSpeed);
        }

        // test if we should try to jump
        if (operation != Operation.WAIT && entity.horizontalCollision && entity.onGround()) {
            Vec3 desiredPos = new Vec3(wantedX, wantedY, wantedZ);
            Vec3 locomotion = new Vec3(locomotionVector);
            Vec3 stepUpMovement = Entity.collideBoundingBox(entity, locomotion,
                    entity.getBoundingBox().move(0, entity.maxUpStep(), 0), entity.level(), List.of());
            // todo: figure out a better way to estimate jump height
            Vec3 jumpMovement = Entity.collideBoundingBox(entity, locomotion,
                    entity.getBoundingBox().move(0, 1.1, 0), entity.level(), List.of());
            if (jumpMovement.distanceToSqr(desiredPos) < stepUpMovement.distanceToSqr(desiredPos))
                entity.getJumpControl().jump();
        }

        if (operation != Operation.STRAFE) {
            strafeForwards = 0;
            strafeRight = 0;
        }

        float acceleration = 0.1F;
        entity.setLocomotionVector(
                Mth.approach(entity.previousLocomotionVector.x, locomotionVector.x, acceleration), 0,
                Mth.approach(entity.previousLocomotionVector.z, locomotionVector.z, acceleration)
        );
    }
    
    private void moveTo(float speed) {
        double deltaX = wantedX - entity.getX(), deltaY = wantedY - entity.getY(),
               deltaZ = wantedZ - entity.getZ();

        double lateralDistance = Math.max(Math.abs(deltaX), Math.abs(deltaZ));
        if (lateralDistance <= entity.getBbWidth() * 0.5F) {
            operation = Operation.WAIT;
            return;
        }

        locomotionVector.set(deltaX, 0, deltaZ);
        locomotionVector.normalize().mul((float) Math.min(speed, Mth.length(deltaX, 0, deltaZ)));
    }

    private void strafe(float speed) {
        Vector3fc forward = entity.getBodyFacingDirection(1.0F);
        locomotionVector.set(
                (-forward.z() * strafeRight + forward.x() * strafeForwards) * speed, 0,
                (forward.x() * strafeRight + forward.z() * strafeForwards) * speed
        );
    }
}
