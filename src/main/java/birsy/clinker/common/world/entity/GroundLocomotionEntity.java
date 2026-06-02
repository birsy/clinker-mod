package birsy.clinker.common.world.entity;

import birsy.clinker.common.networking.packet.ClientboundMobLocomotionSyncPacket;
import birsy.clinker.common.world.entity.ai.*;
import net.minecraft.SharedConstants;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class GroundLocomotionEntity extends PathfinderMob {
    protected static final EntityDataAccessor<Float> DATA_SYNCED_BODY_ROTATION =
            SynchedEntityData.defineId(GroundLocomotionEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Vector3f> DATA_LAST_HIT_DIRECTION =
            SynchedEntityData.defineId(GroundLocomotionEntity.class, EntityDataSerializers.VECTOR3);

    public final Vector3f locomotionVector = new Vector3f(),
                          previousLocomotionVector = new Vector3f(),
                          smoothedLocomotionVector = new Vector3f(),
                          smoothedLocomotionGoalVector = new Vector3f();
    protected float cumulativeLocomotionAmount = 0, cumulativeLocomotionAmountGoal = 0;
    protected final Scheduler scheduler = new Scheduler();

    public float speedModifier = 1.0F;

    protected GroundLocomotionEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = createMoveControl();
        this.lookControl = createLookControl();
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SYNCED_BODY_ROTATION, 0.0F);
        builder.define(DATA_LAST_HIT_DIRECTION, new Vector3f(0, 0, 1));
    }

    @Override protected PathNavigation createNavigation(Level pLevel) {
        return new GroundNavigationControl(this, pLevel, 0.5F);
    }

    @Override public GroundMoveControl getMoveControl() {
        return (GroundMoveControl) super.getMoveControl();
    }
    protected GroundMoveControl createMoveControl() {
        return new GroundMoveControl(this);
    }

    public GroundBodyAngleControl getBodyRotationControl() { return (GroundBodyAngleControl) this.bodyRotationControl; }
    @Override protected BodyRotationControl createBodyControl() {
        return new GroundBodyAngleControl(this, this::getDefaultBodyYaw, this::getDefaultBodyTurnSpeed);
    }

    @Override public GroundLookAngleControl getLookControl() { return (GroundLookAngleControl) super.getLookControl(); }
    protected GroundLookAngleControl createLookControl() {
        return new GroundLookAngleControl(this,
                () -> 0F, 80,
                this::getSyncedBodyRotation, 60,
                () -> 0.5F
        );
    }

    @Override
    public void onDamageTaken(DamageContainer damageContainer) {
        super.onDamageTaken(damageContainer);
        Vec3 damageSourcePos = damageContainer.getSource().getSourcePosition();
        if (damageSourcePos == null) return;
        setLastHitDirection(
                (float) (this.getX() - damageSourcePos.x),
                (float) ((this.getY() + this.getBbHeight() * 0.5F) - damageSourcePos.y),
                (float) (this.getZ() - damageSourcePos.z)
        );
    }

    @Override
    public void tick() {
        this.previousLocomotionVector.set(locomotionVector);
        this.scheduler.tick();
        super.tick();
        if (this.level().isClientSide()) {
            this.locomotionVector.zero();
            this.smoothedLocomotionVector.lerp(smoothedLocomotionGoalVector, 0.2F);
            this.cumulativeLocomotionAmount = Mth.lerp(0.5F, this.cumulativeLocomotionAmount, this.cumulativeLocomotionAmountGoal);
        } else {
            this.getBodyRotationControl().tick();
        }
    }

    @Override
    public void setSpeed(float speed) {
        float zza = this.zza;
        super.setSpeed(speed);
        this.setZza(zza);
    }

    @Override
    protected void customServerAiStep() {
        if (SharedConstants.IS_RUNNING_IN_IDE) this.debugMove();
        this.setYya(locomotionVector.y);
        if (this.locomotionVector.x != 0 || this.locomotionVector.y != 0 || this.locomotionVector.z != 0) this.setSpeed(0.5F);
        if (this.locomotionVector.x != 0 || this.locomotionVector.z != 0) {
            float inverseAngle = -this.getYRot();
            float sinA = Mth.sin(inverseAngle * Mth.DEG_TO_RAD), cosA = Mth.cos(inverseAngle *  Mth.DEG_TO_RAD);
            // rotated because xxa/zza are relative to rotation
            this.setXxa(locomotionVector.x * cosA - locomotionVector.z * sinA);
            this.setZza(locomotionVector.z * cosA + locomotionVector.x * sinA);
        } else {
            this.setXxa(0);
            this.setZza(0);
        }
        if (this.onGround()) {
            double lateralDistanceMoved = Mth.length(this.getDeltaMovement().x, this.getDeltaMovement().z);
            this.cumulativeLocomotionAmount += (float) lateralDistanceMoved;
        }
        PacketDistributor.sendToPlayersTrackingEntity(this, new ClientboundMobLocomotionSyncPacket(this.getId(), this.locomotionVector, this.cumulativeLocomotionAmount));
    }

    protected float getDefaultBodyYaw() {
        // if we're moving, turn to face that direction
        if (locomotionVector != null && (locomotionVector.x != 0 || locomotionVector.z != 0)) {
            // but don't turn while strafing
            if (!getMoveControl().isStrafing()) {
                return (float) -Mth.atan2(locomotionVector.x, locomotionVector.z) * Mth.RAD_TO_DEG;
            }
        }

        // look where the head is trying to look
        float headYaw = this.getLookControl().lookTargetController.getDesiredYaw();
        float currentBodyYaw = this.getBodyRotationControl().lookTargetController.getDesiredYaw();
        float degreesDiff = Mth.degreesDifference(currentBodyYaw, headYaw);
        if (Math.abs(degreesDiff) > 90.0F) degreesDiff = -degreesDiff;
        return Mth.approachDegrees(currentBodyYaw, headYaw + Mth.clamp(degreesDiff, -60, 60), 5);
    }
    protected float getDefaultBodyTurnSpeed() {
        if (locomotionVector != null && (locomotionVector.x != 0 || locomotionVector.z != 0)) {
            return 0.2F;
        }
        return 0.05F;
    }

    public void setSyncedBodyRotation(float rotation) { this.getEntityData().set(DATA_SYNCED_BODY_ROTATION, rotation); }
    public float getSyncedBodyRotation() { return this.getEntityData().get(DATA_SYNCED_BODY_ROTATION); }

    public void setLocomotionVector(float x, float y, float z) { this.locomotionVector.set(x, y, z);}
    public void setLocomotionVector(Vector3fc vec) { this.setLocomotionVector(vec.x(), vec.y(), vec.z()); }

    private final Vector3f facingDir = new Vector3f();
    public Vector3fc getBodyFacingDirection(float partialTicks) {
        float angle = -Mth.rotLerp(partialTicks, this.yBodyRotO, this.yBodyRot) * Mth.DEG_TO_RAD;
        return facingDir.set(Mth.sin(angle), 0, Mth.cos(angle));
    }

    public Vector3fc getLocomotionVectorForAnimation() { return smoothedLocomotionVector; }
    public float getForwardLocomotionAmount(float partialTick) {
        return this.getLocomotionVectorForAnimation().dot(this.getBodyFacingDirection(partialTick));
    }
    public float getStrafeLocomotionAmount(float partialTick) {
        Vector3fc bodyFacingDir = this.getBodyFacingDirection(partialTick);
        return this.getLocomotionVectorForAnimation().dot(-bodyFacingDir.z(), bodyFacingDir.y(), bodyFacingDir.x());
    }

    public void setCumulativeLocomotionAmount(float amount) { this.cumulativeLocomotionAmountGoal = amount; }
    public float getCumulativeLocomotionAmount() { return cumulativeLocomotionAmount; }

    public void setLastHitDirection(float x, float y, float z) { getEntityData().set(DATA_LAST_HIT_DIRECTION, new Vector3f(x, y, z)); }
    public Vector3fc getLastHitDirection() { return getEntityData().get(DATA_LAST_HIT_DIRECTION); }

    private void debugMove() {
        float maxSpeed = 0.8F;
        Player target = EntityRetrievalUtil.getNearestEntity(this, 40.0F, (entity -> entity instanceof Player));

        if (target == null) {
            this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), maxSpeed, 1.0);
            return;
        }

        // approach player
        if (target.getMainHandItem().is(Items.CARROT_ON_A_STICK)) {
            this.moveTowardsPosition(target.getX(), this.getY(), target.getZ(), maxSpeed, this.getBbWidth() * 1.8F);
            this.getLookControl().setLookAt(target);
            return;
        }
        // approach point
        if (target.getMainHandItem().is(Items.ARROW)) {
            BlockHitResult result = this.level().clip(new ClipContext(
                    target.getEyePosition(),
                    target.getEyePosition().add(target.getLookAngle().scale(40.0F)),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    CollisionContext.empty()
            ));
            if (result.getType() == HitResult.Type.BLOCK) {
                Vec3i normal = result.getDirection().getNormal();
                this.moveTowardsPosition(result.getLocation().x() + normal.getX(), result.getLocation().y() + normal.getY(), result.getLocation().z() + normal.getZ(), maxSpeed, 1.5);
                return;
            }
        }
        // strafe
        if (target.getMainHandItem().is(Items.STRING)) {
            Vec3 targetPos = target.getEyePosition();
            this.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
            this.getLookControl().setLookAt(target);

            float desiredDistance = this.getBbWidth() * 2.2F;
            float tolerance = this.getBbWidth() * 0.25F;
            float difference = (float)this.position().subtract(targetPos).horizontalDistance() - desiredDistance;
            float forwardbackwardAmount = 0;
            if (Math.abs(difference) > tolerance) forwardbackwardAmount = Mth.clamp(Mth.abs(difference / tolerance), 0, 3) * Mth.sign(difference);

            float sideToSideAmount = Mth.clamp(Mth.sin(this.tickCount * 0.008F) * 10, -1, 1) * 1.5F;

            this.getMoveControl().strafe(forwardbackwardAmount, sideToSideAmount);
        } else {
            this.moveTowardsPosition(this.getX(), this.getY(), this.getZ(), maxSpeed, 1.0);
        }

    }
    private void moveTowardsPosition(double x, double y, double z, double maxSpeed, double completionRadius) {
        this.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
        this.lookControl.setLookAt(x, y, z);
        if (this.position().distanceToSqr(x, y, z) > completionRadius*completionRadius) {
            Path path = this.getNavigation().createPath(new BlockPos((int) x, (int) y, (int) z), 0);
            if (path != null) this.getNavigation().moveTo(path, maxSpeed);
        }
    }
}
