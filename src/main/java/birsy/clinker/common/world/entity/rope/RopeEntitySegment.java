package birsy.clinker.common.world.entity.rope;

import birsy.clinker.common.world.entity.ColliderEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.VectorUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RopeEntitySegment {
    public final RopeEntity parent;
    public final int type;
    protected final Vector3d
            position = new Vector3d(0, 0, 0),
            previousPosition = new Vector3d(0, 0, 0),
            nextPosition = new Vector3d(0, 0, 0),
            acceleration = new Vector3d(0, 0, 0),
            walk = new Vector3d(0, 0, 0),
            previousWalk = new Vector3d(0, 0, 0);
    protected ColliderEntity collider;
    protected boolean[] collidingOnAxisLastTick = new boolean[Direction.values().length];
    protected boolean[] collidingOnAxis = new boolean[Direction.values().length];
    protected boolean canCollideHorizontally = true;
    protected boolean canFly = false;
    private double stepOffset = 0.0F;
    protected int index;

    public final float radius, length;

    public RopeEntitySegment(RopeEntity parent, int type, float radius, float length, int index) {
        this.parent = parent;
        this.type = type;
        this.position.set(parent.getX(), parent.getY(), parent.getZ());
        this.previousPosition.set(position);
        this.nextPosition.set(position);
        this.radius = radius;
        this.length = length;
        if (!this.parent.level().isClientSide()) {
            this.collider = ColliderEntity.create(parent, radius * 2.0F, radius * 2.0F);
            this.parent.level().addFreshEntity(this.collider);
        }

        this.index = index;
    }

    public void setInitialPosition(double x, double y, double z) {
        this.previousPosition.set(x, y, z);
        this.position.set(x, y, z);
        this.nextPosition.set(x, y, z);
        this.acceleration.set(0);
    }

    public void setNextPosition(double x, double y, double z) { this.nextPosition.set(x, y, z); }

    public void setPosition(double x, double y, double z) { this.position.set(x, y, z); }

    public Vector3dc getPosition() { return this.position; }


    @OnlyIn(Dist.CLIENT)
    Vector3d interpolatedPosition = new Vector3d();
    @OnlyIn(Dist.CLIENT)
    public Vector3dc getPosition(float partialTick) {
        return this.previousPosition.lerp(this.position, partialTick, this.interpolatedPosition);
    }

    private final Vector3d connectionDir = new Vector3d(0, 0, 1);
    public Vector3dc getAttachmentDirection(float partialTick) {
        if (this.index == 0) {
            return this.getPosition(partialTick).sub(((RopeEntitySegment) this.parent.segments.get(this.index + 1)).getPosition(partialTick), this.connectionDir);
        }
        return ((RopeEntitySegment) this.parent.segments.get(this.index - 1)).getPosition(partialTick)
                .sub(this.getPosition(partialTick), this.connectionDir);
    }

    public Vector3dc getPrevWalkVector() { return this.previousWalk; }

    public Vector3dc getWalkVector() { return this.walk; }

    @OnlyIn(Dist.CLIENT)
    Vector3d interpolatedWalkVector = new Vector3d();
    @OnlyIn(Dist.CLIENT)
    public Vector3dc getWalkVector(float partialTick) {
        return this.previousWalk.lerp(this.walk, partialTick, this.interpolatedWalkVector);
    }

    public double getWalkAmount(float partialTick) { return this.getWalkVector(partialTick).dot(this.getAttachmentDirection(partialTick)); }

    private final Vector3d strafeDir = new Vector3d(1, 0, 0);
    public double getStrafeAmount(float partialTick) { return this.getWalkVector(partialTick).dot(this.getAttachmentDirection(partialTick).cross(0, 1, 0, strafeDir)); }

    private final Vector3d deltaMovement = new Vector3d();
    public Vector3dc getDeltaMovement() { return this.getDeltaMovement(deltaMovement); }

    public Vector3d getDeltaMovement(Vector3d assignment) { return this.position.sub(this.previousPosition, assignment); }

    public Vector3dc getDeltaMovementNoWalking() { return this.getDeltaMovementNoWalking(deltaMovement); }
    protected Vector3d getDeltaMovementNoWalking(Vector3d assignment) { return this.position.sub(this.previousPosition, assignment).sub(this.previousWalk).sub(0, stepOffset, 0); }


    public boolean isCollidingOnAxis(Direction direction) { return this.collidingOnAxis[direction.get3DDataValue()]; }

    public boolean isOnGround() { return this.isCollidingOnAxis(Direction.DOWN); }

    public boolean isCollidingHorizontally() { return this.isCollidingOnAxis(Direction.NORTH) || this.isCollidingOnAxis(Direction.SOUTH) || this.isCollidingOnAxis(Direction.EAST) || this.isCollidingOnAxis(Direction.WEST); }

    public boolean isWalkingIntoWall() {
        if (this.walk.x() > 0 && this.isCollidingOnAxis(Direction.EAST )) return true;
        if (this.walk.x() < 0 && this.isCollidingOnAxis(Direction.WEST )) return true;
        if (this.walk.z() > 0 && this.isCollidingOnAxis(Direction.SOUTH)) return true;
        if (this.walk.z() < 0 && this.isCollidingOnAxis(Direction.NORTH)) return true;

        return false;
    }


    protected void accelerate(double x, double y, double z) {
        this.acceleration.add(x, y, z);
    }

    public void walk(double x, double y, double z) {
        if (!this.isOnGround() && !this.isCollidingHorizontally() && !this.canFly) {
            float airControlMultiplier = 0.5F;
            this.walkRaw(x * airControlMultiplier, y * airControlMultiplier, z * airControlMultiplier);
            return;
        }
        this.walkRaw(x, y, z);
    }

    public void walkRaw(double x, double y, double z) {
        this.walk.set(x, y, z);
    }

    private final Vector3d velocity = new Vector3d(0);
    public void integrate() {
        this.previousWalk.set(this.walk);

        for (int i = 0; i < collidingOnAxis.length; i++) {
            this.collidingOnAxisLastTick[i] = this.collidingOnAxis[i];
            this.collidingOnAxis[i] = false;
        }

        this.getDeltaMovementNoWalking(this.velocity).mul(0.95);
        this.stepOffset = 0.0F;
        this.previousPosition.set(this.position);

        this.velocity.add(this.acceleration);
        this.acceleration.set(0, 0, 0);

        this.position.add(this.velocity, this.nextPosition).add(this.walk, this.nextPosition);
        this.walk.set(0,0,0);
    }

    @OnlyIn(Dist.CLIENT)
    protected void updateClient() {
        this.previousPosition.set(this.position);
        this.position.set(this.nextPosition);

        this.previousWalk.set(this.walk);
    }

    private final Vector3d constrainedNextPosition = new Vector3d(),
            stepUpVerticalComponent = new Vector3d(),
            tempA = new Vector3d(), tempB = new Vector3d();
    public void collideWithLevel() {
        float epsilon = 0.001F;
        float stepUpHeight = this.parent.getStepHeight();

        double xMovement = this.nextPosition.x - this.position.x, yMovement = this.nextPosition.y - this.position.y, zMovement = this.nextPosition.z - this.position.z;

        AABB aabb = new AABB(
                this.position.x, this.position.y, this.position.z,
                this.nextPosition.x, this.nextPosition.y, this.nextPosition.z
        ).inflate(radius);
        aabb = aabb.setMaxY(aabb.maxY + stepUpHeight);

        // gather potential collisions
        List<VoxelShape> potentialCollisions = this.parent.level().getEntityCollisions(parent, aabb.expandTowards(this.movement.x, this.movement.y, this.movement.z));
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(potentialCollisions.size() + 1);
        builder.addAll(potentialCollisions);
        builder.addAll(this.parent.level().getBlockCollisions(this.parent, aabb.expandTowards(this.movement.x, this.movement.y, this.movement.z)));
        WorldBorder worldborder = this.parent.level().getWorldBorder();
        if (worldborder.isInsideCloseToBorder(this.parent, aabb)) builder.add(worldborder.getCollisionShape());

        ImmutableList<VoxelShape> collisions = builder.build();

        // first do a normal collision pass
        this.collideWithVoxelShapes(this.position, this.nextPosition, this.constrainedNextPosition, collisions);

        // if we are on the ground and collided with a wall, do check to see if we should step up the block.
        boolean collidedHorizontally = Math.abs(this.constrainedNextPosition.x - this.nextPosition.x) > epsilon || Math.abs(this.constrainedNextPosition.z - this.nextPosition.z) > epsilon;
        boolean collidedVertically = Math.abs(this.constrainedNextPosition.y - this.nextPosition.y) > epsilon;
        if (collidedHorizontally) {

            this.collideWithVoxelShapes(
                    this.tempA.set(this.position.x(), this.position.y() + stepUpHeight, this.position.z()),
                    this.tempB.set(this.nextPosition.x(), this.position.y() + stepUpHeight, this.nextPosition.z()),
                    this.stepUpVerticalComponent,
                    collisions);

            //if stepUpVerticalComponent went further horizontally than constrainedNextPosition, then we know that the stepUpHeight actually did step up something.
            if (this.stepUpVerticalComponent.distanceSquared(this.position.x(), this.stepUpVerticalComponent.y(), this.position.z) -
                    this.constrainedNextPosition.distanceSquared(this.position.x(), this.constrainedNextPosition.y(), this.position.z) > epsilon) {
                // move down so we're on the ground again...
                this.collideWithVoxelShapes(
                        this.stepUpVerticalComponent,
                        this.tempA.set(this.stepUpVerticalComponent.x(), this.position.y(), this.stepUpVerticalComponent.z()),
                        this.tempB,
                        collisions);
                this.stepOffset = Math.max(this.stepOffset, this.tempB.y() - this.constrainedNextPosition.y());
                this.constrainedNextPosition.set(this.tempB);
            }
        }

        // if any axis is different, then we're colliding on that axis
        if (Math.abs((this.nextPosition.x - this.position.x) - (this.constrainedNextPosition.x - this.position.x)) > 0.01) this.collidingOnAxis[xMovement < 0 ? Direction.WEST .get3DDataValue() : Direction.EAST .get3DDataValue()] = true;
        if (Math.abs((this.nextPosition.y - this.position.y) - (this.constrainedNextPosition.y - this.position.y)) > 0.01) this.collidingOnAxis[yMovement < 0 ? Direction.DOWN .get3DDataValue() : Direction.UP   .get3DDataValue()] = true;
        if (Math.abs((this.nextPosition.z - this.position.z) - (this.constrainedNextPosition.z - this.position.z)) > 0.01) this.collidingOnAxis[zMovement < 0 ? Direction.NORTH.get3DDataValue() : Direction.SOUTH.get3DDataValue()] = true;
        this.nextPosition.set(this.constrainedNextPosition);
    }

    private void collideWithVoxelShapes(Vector3dc startPos, Vector3dc nextPos, Vector3d collisionAdjustedNextPos, Iterable<VoxelShape> potentialCollisions) {
        AABB aabb = new AABB(
                startPos.x() + radius, startPos.y() + radius, startPos.z() + radius,
                startPos.x() - radius, startPos.y() - radius, startPos.z() - radius
        );

        double xMovement = nextPos.x() - startPos.x(), yMovement = nextPos.y() - startPos.y(), zMovement = nextPos.z() - startPos.z();

        if (yMovement != 0.0) {
            yMovement = Shapes.collide(Direction.Axis.Y, aabb, potentialCollisions, yMovement);
            if (yMovement != 0.0) aabb = aabb.move(0.0, yMovement, 0.0);
        }

        if (this.canCollideHorizontally) {
            boolean scootZFirst = Math.abs(xMovement) < Math.abs(zMovement);
            if (scootZFirst) {
                if (zMovement != 0.0) {
                    zMovement = Shapes.collide(Direction.Axis.Z, aabb, potentialCollisions, zMovement);
                    if (zMovement != 0.0) aabb = aabb.move(0.0, 0.0, zMovement);
                }
                if (xMovement != 0.0) xMovement = Shapes.collide(Direction.Axis.X, aabb, potentialCollisions, xMovement);
            } else {
                if (xMovement != 0.0) {
                    xMovement = Shapes.collide(Direction.Axis.X, aabb, potentialCollisions, xMovement);
                    if (xMovement != 0.0) aabb = aabb.move(xMovement, 0.0, 0.0);
                }
                if (zMovement != 0.0) zMovement = Shapes.collide(Direction.Axis.Z, aabb, potentialCollisions, zMovement);
            }

        }

        startPos.add(xMovement, yMovement, zMovement, collisionAdjustedNextPos);
    }

    private final Vector3d center = new Vector3d();
    private final Vector3d direction = new Vector3d();
    public void collideWithOtherSegment(RopeEntitySegment segment) {
        RopeEntitySegment segmentA = this;
        RopeEntitySegment segmentB = segment;

        if (segmentA == segmentB) return;

        float minimumDistanceV = segmentA.radius + segmentB.radius;
        float minimumDistanceH = minimumDistanceV * 0.5F;
        Vector3d pos1 = segmentA.nextPosition;
        Vector3d pos2 = segmentB.nextPosition;

        // these are treated as cylinders so they don't push each other into the void lol
        double verticalOffset = pos1.y - pos2.y;
        double horizontalDistance = (pos1.x - pos2.x)*(pos1.x - pos2.x) + (pos1.z - pos2.z)*(pos1.z - pos2.z);

        if (!(Math.abs(verticalOffset) < minimumDistanceV) || !(horizontalDistance < minimumDistanceH * minimumDistanceH)) return;

        horizontalDistance = Math.sqrt(horizontalDistance);

        float mass1 = 1.3333F * Mth.PI * segmentA.radius * segmentA.radius * segmentA.radius;
        float mass2 = 1.3333F * Mth.PI * segmentB.radius * segmentB.radius * segmentB.radius;
        float totalMass = mass1 + mass2;

        pos1.lerp(pos2, 0.5F, center);
        if ((horizontalDistance - minimumDistanceH) > (Math.abs(verticalOffset) - minimumDistanceV)) {
            pos1.sub(pos2, direction).mul(1, 0, 1).normalize().mul(-0.5 * minimumDistanceH);
            segmentA.nextPosition.set(center.x - direction.x, segmentA.nextPosition.y, center.z - direction.z);
            segmentB.nextPosition.set(center.x + direction.x, segmentB.nextPosition.y, center.z + direction.z);

            // honestly i don't really give a shit if they collide horizontally LOL
            // whatever...
        } else {
            double offset = Math.signum(verticalOffset) * 0.5 * minimumDistanceV;
            pos1.sub(pos2, direction).normalize().mul(-0.5 * minimumDistanceV);
            segmentA.nextPosition.set(center.x - direction.x, center.y - direction.y, center.z - direction.z);
            segmentB.nextPosition.set(center.x + direction.x, center.y + direction.y, center.z + direction.z);

            if (offset > 0) {
                segmentA.collidingOnAxis[Direction.DOWN.get3DDataValue()] = true;
                segmentB.collidingOnAxis[Direction.UP.get3DDataValue()] = true;
            } else {
                segmentA.collidingOnAxis[Direction.UP.get3DDataValue()] = true;
                segmentB.collidingOnAxis[Direction.DOWN.get3DDataValue()] = true;
            }
        }
    }

    private final Vector3d movement = new Vector3d();
    public void finalizeSim() {
        this.nextPosition.sub(this.position, this.movement);
        // apply friction if need be...
        if (this.isOnGround()) {
            // get the block at our feet
            BlockPos feetPosition = BlockPos.containing(this.position.x, this.position.y - (this.radius + 0.01), this.position.z);
            float friction = this.parent.level().getBlockState(feetPosition).getFriction(this.parent.level(), feetPosition, this.parent);
            friction = Mth.lerp(0.5F, friction, 1.0F);
            this.movement.mul(friction, 1.0F, friction);
        }
        this.position.add(this.movement, this.nextPosition);

        this.position.set(this.nextPosition);
        this.collider.setPos(this.position.x, this.position.y - (this.radius), this.position.z);
    }

    public void remove(Entity.RemovalReason reason) {
        this.collider.remove(reason);
    }

    /*  private final Vector3d movementWithoutCollision = new Vector3d();
    private float finalFrictionFactor = 1.0F;
    private void applyLevelCollisionsAndFinalize(Level level) {
        AABB aabb = new AABB(
                this.position.x + radius, this.position.y + radius, this.position.z + radius,
                this.position.x - radius, this.position.y - radius, this.position.z - radius
        );
        this.nextPosition.sub(this.position, this.movement);
        this.movementWithoutCollision.set(this.movement);

        // constrain movement based off level collisions...
        // todo: write this myself
        List<VoxelShape> list = level.getEntityCollisions(parent, aabb.expandTowards(this.movement.x, this.movement.y, this.movement.z));
        VectorUtils.toJOML(Entity.collideBoundingBox(parent, VectorUtils.toMoj(this.movement), aabb, level, list), this.movement);

        // apply friction
        // ...only on y-axis. simplifies + this is actually how vanilla handles it LOL
        // sorry ryan. Will Not Work With Landlord :)
        if (Math.abs(this.movement.y - this.movementWithoutCollision.y) > 0.01) {
            // get the block at our feet
            BlockPos feetPosition = BlockPos.containing(this.position.x, this.position.y - (this.radius + 0.01), this.position.z);
            float friction = this.parent.level().getBlockState(feetPosition).getFriction(this.parent.level(), feetPosition, this.parent);
            friction = Mth.lerp(0.5F, friction, 1.0F);
            finalFrictionFactor = Math.min(finalFrictionFactor, friction);
            //this.isOnGround = true;
        }

        if (Math.abs(this.movement.x - this.movementWithoutCollision.x) > 0.01 || Math.abs(this.movement.z - this.movementWithoutCollision.z) > 0.01) {
            //this.isCollidingHorizontally = true;
        }

        this.position.add(this.movement, this.nextPosition);
    }*/
}
