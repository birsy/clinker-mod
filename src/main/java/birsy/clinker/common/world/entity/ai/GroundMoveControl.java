package birsy.clinker.common.world.entity.ai;

import birsy.clinker.common.world.entity.GroundLocomoteEntity;
import birsy.clinker.core.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;

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
        float speed = me.getSpeed() * 0.5F;

        float acceleration = 0.1F / 10.0F;
        me.walk(0, 0, 0);
        double dX = this.wantedX - me.getX(),
               dY = this.wantedY - me.getY(),
               dZ = this.wantedZ - me.getZ();

        desiredDirection.set(dX, dY, dZ).normalize();

        if (this.operation == Operation.MOVE_TO) {
            //me.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(wantedX, me.getEyeY(), wantedZ));

            // if we've already reached out destination, then stop
            if (me.getPosition(1.0F).distanceToSqr(this.wantedX, this.wantedY, this.wantedZ) < me.getBbWidth()*0.1F) this.operation = Operation.WAIT;
            walkVector.set(desiredDirection).mul(speed * 1.5F);

            // weird jumping logic taken from MoveControl
            BlockPos blockpos = me.blockPosition();
            BlockState blockstate = me.level().getBlockState(blockpos);
            VoxelShape voxelshape = blockstate.getCollisionShape(me.level(), blockpos);
            if (dY > me.getStepHeight() && dX * dX + dZ * dZ < Math.max(1.0F, me.getBbWidth())
                    || !voxelshape.isEmpty()
                    && me.getY() < voxelshape.max(Direction.Axis.Y) + blockpos.getY()
                    && !blockstate.is(BlockTags.DOORS)
                    && !blockstate.is(BlockTags.FENCES)) {
                this.mob.getJumpControl().jump();
                this.operation = Operation.JUMPING;
            }
        }

        if (this.operation == Operation.STRAFE) {
            Vec3 forwardDirection = me.getLookAngle().multiply(1,0,1).normalize();
            Vec3 perpendicularDirection = forwardDirection.cross(new Vec3(0, 1, 0)).normalize();
            walkVector.set(
                    (perpendicularDirection.x * this.strafeRight + forwardDirection.x * this.strafeForwards) * speed, 0,
                    (perpendicularDirection.z * this.strafeRight + forwardDirection.z * this.strafeForwards) * speed
            );
            if (me.horizontalCollision) me.setJumping(true);
        } else {
            this.strafeForwards = 0;
            this.strafeRight = 0;
        }

        if (this.operation == Operation.JUMPING) {
            if (this.mob.onGround()) this.operation = Operation.WAIT;
        }

        if (this.operation == Operation.WAIT) {
            acceleration = 0.05F;
            walkVector.set(0, 0, 0);
            mob.xxa = 0;
            mob.zza = 0;
        }

        me.walk(
                (float) MathUtil.approach(me.previousWalk.x, walkVector.x, acceleration),
                (float) 0,
                (float) MathUtil.approach(me.previousWalk.z, walkVector.z, acceleration)
        );
    }
}
